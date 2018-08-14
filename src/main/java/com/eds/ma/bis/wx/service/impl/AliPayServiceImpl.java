package com.eds.ma.bis.wx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.eds.ma.bis.order.OrderCodeCreater;
import com.eds.ma.bis.order.OrderPayTypeEnum;
import com.eds.ma.bis.order.TransTypeEnum;
import com.eds.ma.bis.order.entity.FinanceIncome;
import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.wx.PayStatusEnum;
import com.eds.ma.bis.wx.RefundStatusEnum;
import com.eds.ma.bis.wx.entity.PayRefund;
import com.eds.ma.bis.wx.entity.PayRefundLog;
import com.eds.ma.bis.wx.service.IAliPayService;
import com.eds.ma.bis.wx.vo.AlipayRefund;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.eds.ma.rest.common.CommonConstants;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * ali支付service
 *
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Service
@Transactional
public class AliPayServiceImpl implements IAliPayService {

    private static Logger logger = Logger.getLogger(AliPayServiceImpl.class);


    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private BaseDaoSupport dao;


    /**
     * 获取支付订单编号
     */
    @Override
    public String getPayCode() {
        String dateString = DateFormatUtils.formatDate(new Date(),CommonConstants.DATE_PATTERN);
        return "v" + dateString + System.currentTimeMillis() + RandomStringUtils.randomNumeric(4);
    }

    @Override
    public String prepay(Long userId, String aliUid, String transType, BigDecimal payMoney, String payTitle) {
        //订单编号
        String orderCode = OrderCodeCreater.createTradeNO();
        //支付订单流水号
        String payCode = getPayCode();
        AlipayTradeAppPayResponse prePayResponse = aliPrePay(payCode,payTitle,payMoney);
        if(!prePayResponse.isSuccess()){
            logger.error("AliPayServiceImpl.prepay error,result:{}",prePayResponse.getBody());
            throw new BizCoreRuntimeException(BizErrorConstants.PAY_SYSTEM_ERROR);
        }

        PayOrder pay = new PayOrder();
        pay.setBuyerId(aliUid);
        pay.setCreated(DateFormatUtils.getNow());
        pay.setOrderCode(orderCode);
        pay.setPayCode(payCode);
        pay.setTransType(transType);
        pay.setPayMoney(payMoney);
        pay.setPayStatus(PayStatusEnum.WAIT_BUYER_PAY.value());
        pay.setUserId(userId);
        pay.setTitle(payTitle);
        pay.setPayType(OrderPayTypeEnum.S_ZFFS_ZFB.value());
        orderService.savePayOrder(pay);
        return prePayResponse.getBody();
    }

    @Override
    public void optAliPayCallback(Map<String, String[]> requestParams, HttpServletResponse response) {
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();

        Set<String> keySet = requestParams.keySet();
        String payCode = "";
        String trade_status = "";
        BigDecimal payMoney = null;
        for (String key : keySet) {
            StringBuilder buffer = new StringBuilder();
            for (String string : requestParams.get(key)) {
                buffer.append(string);
            }
            params.put(key, buffer.toString());
            if (key.equals("out_trade_no")) {
                // 商户订单号
                payCode = buffer.toString();
            } else if (key.equals("trade_status")) {
                // 交易状态
                trade_status = buffer.toString();
            } else if (key.equals("total_amount")) {
                // 充值金额
                payMoney = BigDecimal.valueOf(Double.valueOf(buffer.toString()));
            }
        }
        try {
            // 计算得出通知验证结果
            boolean verify_result = AlipaySignature.rsaCheckV1(params, sysConfig.getAliGatewayPublicKey(),
                    "UTF-8", "RSA2");
            // 验证成功
            if (verify_result) {
                // 交易支付成功
                if (trade_status.equals("TRADE_SUCCESS")) {
                    PayOrder payOrder = orderService.queryPayOrderByPayCode(payCode);
                    if (Objects.isNull(payOrder)) {
                        //支付信息未找到
                        logger.error("AliPayServiceImpl.optAliPayCallback occur error,payOrder not found:{}",params);
                        response.getWriter().println("fail");
                        return;
                    }

                    int result = orderService.updatePayOrderForCallBack(payCode
                            , MapUtils.getString(params,"notify_id")
                            , MapUtils.getString(params,"trade_no")
                            , MapUtils.getString(params,"buyer_id")
                            , payOrder.getPayMoney()
                            , payMoney
                            , BigDecimal.valueOf(MapUtils.getDouble(params,"receipt_amount"))
                            , MapUtils.getString(params,"gmt_payment")
                            , null
                            , MapUtils.getString(params,"seller_id")
                            , MapUtils.getString(params,"app_id")
                            , null);

                    if (result > 0) {
                        //押金充值,将押金累加到用户钱包-押金中
                        if(Objects.equals(payOrder.getTransType(), TransTypeEnum.S_JYLX_YJCZ.value())){
                            userService.updateUserWallet(payOrder.getUserId(),payOrder.getPayMoney(),null);
                        }

                        //余额充值,将押金累加到用户钱包-余额中
                        if(Objects.equals(payOrder.getTransType(), TransTypeEnum.S_JYLX_YECZ.value())){
                            userService.updateUserWallet(payOrder.getUserId(),null,payOrder.getPayMoney());
                        }
                        //保存交易记录
                        FinanceIncome financeIncome = new FinanceIncome();
                        financeIncome.setTransCode(OrderCodeCreater.createTradeNO());
                        financeIncome.setContent(payOrder.getTitle());
                        financeIncome.setUserId(payOrder.getUserId());
                        financeIncome.setOpenId(payOrder.getBuyerId());
                        financeIncome.setTransType(payOrder.getTransType());
                        financeIncome.setTransTime(DateFormatUtils.getNow());
                        financeIncome.setMoney(payOrder.getPayMoney());
                        financeIncome.setOrderCode(payOrder.getOrderCode());
                        orderService.saveFinanceIncome(financeIncome);
                        response.getWriter().println("success");
                    }else{
                        response.getWriter().println("fail");
                        logger.error("AliPayServiceImpl.optAliPayCallback update payOrder error,result:{}",params);
                    }
                }
            } else {
                // 验证失败
                logger.error("AliPayServiceImpl.optAliPayCallback verify error,result:{}",params);
                response.getWriter().println("fail");
            }
        } catch (Exception e) {
            logger.error("AliPayServiceImpl.optAliPayCallback occurs unkonwn exception",e);
            try {
                response.getWriter().println("fail");
            } catch (IOException e1) {
                logger.error("AliPayServiceImpl.optAliPayCallback occurs unkonwn IOException",e);
            }
        }
    }

    private AlipayTradeAppPayResponse aliPrePay(String payCode,String payTitle,BigDecimal payMoney){
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient( sysConfig.getAliGatewayUrl(),sysConfig.getAliMaAppId(),
                sysConfig.getAliGatewayPrivateKey(),"json","GBK",sysConfig.getAliGatewayPublicKey(),"RSA2");

        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(payTitle);
        model.setSubject(payTitle);
        model.setOutTradeNo(payCode);
        model.setTimeoutExpress("30m");
        model.setTotalAmount(String.valueOf(payMoney));
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(sysConfig.getAliPayCallbackUrl());
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            return alipayClient.sdkExecute(request);
        } catch (Exception e) {
            logger.error("AliPayServiceImpl.aliPrePay error",e);
            throw new BizCoreRuntimeException(BizErrorConstants.PAY_SYSTEM_ERROR);
        }
    }
}
