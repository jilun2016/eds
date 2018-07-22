package com.eds.ma.bis.wx.service.impl;

import com.eds.ma.bis.order.OrderCodeCreater;
import com.eds.ma.bis.order.TransTypeEnum;
import com.eds.ma.bis.order.entity.FinanceIncome;
import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.wx.PayStatusEnum;
import com.eds.ma.bis.wx.sdk.common.util.RandomStringGenerator;
import com.eds.ma.bis.wx.sdk.common.util.XmlObjectMapper;
import com.eds.ma.bis.wx.sdk.pay.base.PaySetting;
import com.eds.ma.bis.wx.sdk.pay.payment.Payments;
import com.eds.ma.bis.wx.sdk.pay.payment.bean.PaymentNotification;
import com.eds.ma.bis.wx.sdk.pay.payment.bean.UnifiedOrderRequest;
import com.eds.ma.bis.wx.sdk.pay.payment.bean.UnifiedOrderResponse;
import com.eds.ma.bis.wx.sdk.pay.util.SignatureUtil;
import com.eds.ma.bis.wx.service.IWxPayService;
import com.eds.ma.config.SysConfig;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * 微信支付service
 *
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Service
@Transactional
public class WxPayServiceImpl implements IWxPayService {

    private static Logger logger = Logger.getLogger(WxPayServiceImpl.class);

    public static final String DATE_PATTERN = "yyyyMMdd";

    /**
     * 微信回调返回处理成功
     */
    public static final String RET_S = "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
    /**
     * 微信回调返回处理失败
     */
    public static final String RET_F = "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrderService orderService;

    /**
     * 获取支付订单编号
     */
    @Override
    public String getPayCode() {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        String dateString = formatter.format(new Date());
        return "v" + dateString + System.currentTimeMillis() + RandomStringUtils.randomNumeric(4);
    }

    @Override
    public Map<String, Object> prepay(String openId,String transType, BigDecimal payMoney,String payTitle) {
        User user = userService.checkUserExist(openId);
        Long userId = user.getId();
        //订单编号
        String orderCode = OrderCodeCreater.createTradeNO();
        //支付订单流水号
        String payCode = getPayCode();
        UnifiedOrderRequest unifiedOrderRequest = new UnifiedOrderRequest();
        unifiedOrderRequest.setBody(payTitle);
        unifiedOrderRequest.setTradeNumber(payCode);
        unifiedOrderRequest.setTotalFee(getWxPayMoney(payMoney));
        unifiedOrderRequest.setBillCreatedIp("127.0.0.1");
        unifiedOrderRequest.setNotifyUrl(sysConfig.getWxPayCallbackUrl());
        unifiedOrderRequest.setTradeType("JSAPI");
        unifiedOrderRequest.setOpenId(openId);
        PaySetting paySetting = getPaySetting();
        UnifiedOrderResponse response = Payments.with(paySetting).unifiedOrder(unifiedOrderRequest);


        PayOrder pay = new PayOrder();
        pay.setBuyerId(openId);
        pay.setCreated(DateFormatUtils.getNow());
        pay.setOrderCode(orderCode);
        pay.setPayCode(payCode);
        pay.setTransType(transType);
        pay.setPayMoney(payMoney);
        pay.setPayStatus(PayStatusEnum.WAIT_BUYER_PAY.value());
        pay.setUserId(userId);
        pay.setTitle(payTitle);
        orderService.savePayOrder(pay);


        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String noncestr = RandomStringGenerator.getRandomStringByLength(16);
        Map<String, Object> params2 = new TreeMap<>();
        params2.put("appId", sysConfig.getWxMaAppId());
        params2.put("timeStamp", timestamp);
        params2.put("nonceStr", noncestr);
        params2.put("package", "prepay_id=" + response.getPrepayId());
        params2.put("signType", "MD5");
        String paySign = SignatureUtil.sign(params2, sysConfig.getWxMerchantKey());
        params2.put("paySign", paySign);

        return params2;
    }

    @Override
    public String optWxPayCallback(String xml) {
        try {
            PaymentNotification paymentNotification = XmlObjectMapper.nonEmptyMapper().fromXml(xml, PaymentNotification.class);
            if (paymentNotification == null || paymentNotification.getReturnCode() == null) {
                logger.error("【支付失败】支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问");
                return RET_F;
            }

            if (paymentNotification.getReturnCode().equals("FAIL")) {
                //注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
                logger.error("【支付失败】支付API系统返回失败，请检测Post给API的数据是否规范合法, return_msg={}", paymentNotification.getReturnMessage());
                return RET_F;
            }

            PaySetting paySetting = getPaySetting();
            if (!Payments.with(paySetting).checkSignature(paymentNotification)) {
                logger.error("【支付失败】支付请求API返回的数据签名验证失败，有可能数据被篡改了");
                return RET_F;
            }

            String payCode = paymentNotification.getTradeNumber();
            String tradeNo = paymentNotification.getTransactionId();
            String nonce = paymentNotification.getNonce();
            BigDecimal totalFee = BigDecimal.valueOf(paymentNotification.getTotalFee()).divide(BigDecimal.valueOf(100));
            BigDecimal cashFee = BigDecimal.valueOf(paymentNotification.getCashFee()).divide(BigDecimal.valueOf(100));
            String sellerId = paymentNotification.getMchId();
            String openId = paymentNotification.getOpenId();
            PayOrder payOrder = orderService.queryPayOrderByPayCode(payCode);
            if (Objects.isNull(payOrder)) {
                //支付信息未找到
                return RET_F;
            }

            int result = orderService.updatePayOrderForCallBack(payCode
                    , nonce
                    , tradeNo
                    , openId
                    , payOrder.getPayMoney()
                    , totalFee
                    , cashFee
                    , paymentNotification.getTimeEndString()
                    , paymentNotification.getBankType()
                    , sellerId
                    , paymentNotification.getAppId()
                    , paymentNotification.getIsSubscribed());

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
            }
            return RET_S;
        } catch (Exception e) {
            logger.error("WX-JSAPI-NOTIFY error.wx callback data: " + xml, e);
        }
        return RET_F;
    }

    private PaySetting getPaySetting() {
        PaySetting paySetting = new PaySetting();
        paySetting.setAppId(sysConfig.getWxMaAppId());
        paySetting.setKey(sysConfig.getWxMerchantKey());
        paySetting.setMchId(sysConfig.getWxMchId());
        return paySetting;
    }

    /**
     * 获得微信支付金额 将系统BigDecimal（元） 转换为（分）
     *
     * @param payMoney
     * @return
     */
    public int getWxPayMoney(BigDecimal payMoney) {
        Assert.notNull(payMoney, "payMoney is required");
        BigDecimal orderPayMoney = payMoney.multiply(BigDecimal.valueOf(100L));
        return orderPayMoney.intValue();
    }
}
