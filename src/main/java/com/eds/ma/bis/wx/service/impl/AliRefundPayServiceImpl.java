package com.eds.ma.bis.wx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.eds.ma.bis.order.OrderCodeCreater;
import com.eds.ma.bis.order.OrderPayTypeEnum;
import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.wx.RefundStatusEnum;
import com.eds.ma.bis.wx.entity.PayRefund;
import com.eds.ma.bis.wx.entity.PayRefundLog;
import com.eds.ma.bis.wx.service.IAliRefundPayService;
import com.eds.ma.bis.wx.vo.AlipayRefund;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * ali支付service
 *
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Service
@Transactional
public class AliRefundPayServiceImpl implements IAliRefundPayService {

    private static Logger logger = Logger.getLogger(AliRefundPayServiceImpl.class);


    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private BaseDaoSupport dao;

    @Override
    public void submiteRefund(PayOrder payOrder, BigDecimal tkFee) {
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient( sysConfig.getAliGatewayUrl(),sysConfig.getAliMaAppId(),
                sysConfig.getAliGatewayPrivateKey(),"json","GBK",sysConfig.getAliGatewayPublicKey(),"RSA2");
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayRefund alipayRefund= new AlipayRefund();
        alipayRefund.setOut_trade_no(payOrder.getPayCode());//这个是商户的订单号
        alipayRefund.setTrade_no(payOrder.getTradeNo());//这个是支付宝的订单号
        alipayRefund.setRefund_amount(String.valueOf(tkFee));//退款金额
        alipayRefund.setRefund_reason("提现");//退款说明
        String orderTkCode = OrderCodeCreater.createTradeNO();
        alipayRefund.setOut_request_no(orderTkCode);
        request.setBizContent(JSONObject.toJSONString(alipayRefund));//2个都可以，这个参数的顺序 不影响退款
        //退款日志
        PayRefundLog log = new PayRefundLog(payOrder.getPayCode(),payOrder.getOrderCode(),orderTkCode,tkFee);
        log.setReqData(request.getBizContent());
        try {
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            log.setResData(response.getBody());
            log.setErrorCode(response.getCode()+"@"+response.getSubCode());
            log.setErrDes(response.getMsg()+"@"+response.getSubMsg());
            if (response.isSuccess()) {
                //保存一条退款记录
                PayRefund entity = new PayRefund();
                entity.setPayType(OrderPayTypeEnum.S_ZFFS_ZFB.value());
                entity.setCreated(new Timestamp(System.currentTimeMillis()));
                entity.setOrderCode(payOrder.getOrderCode());
                entity.setOrderTkCode(orderTkCode);
                entity.setPayCode(payOrder.getPayCode());
                entity.setPayTradeNo(payOrder.getTradeNo());
                entity.setRefundFee(tkFee);
                entity.setRefundStatus(RefundStatusEnum.REFUND_ING.value());
                entity.setSellerId(payOrder.getSellerId());
                entity.setBuyerId(payOrder.getBuyerId());
                savePayRefund(entity);
            } else {
                logger.error("AliPayServiceImpl.submiteRefund fail",response.getBody());
                throw new BizCoreRuntimeException(BizErrorConstants.PAY_REFUND_FAIL);
            }
        } catch (AlipayApiException e) {
            logger.error("AliPayServiceImpl.submiteRefund error",e);
            log.setErrorCode("eds refund sumbmit error");
            log.setErrDes(e.getMessage());
            throw new BizCoreRuntimeException(BizErrorConstants.PAY_REFUND_FAIL);
        } finally {
            saveRefundLog(log);
        }
    }

    @Override
    public void aliRefundQuery(List<PayRefund> payRefundList) {
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient( sysConfig.getAliGatewayUrl(),sysConfig.getAliMaAppId(),
                sysConfig.getAliGatewayPrivateKey(),"json","GBK",sysConfig.getAliGatewayPublicKey(),"RSA2");
        for(PayRefund payRefund:payRefundList){
            operateRefundRecord(alipayClient,payRefund);
        }

    }

    /**
     * 退款处理
     */
    private void operateRefundRecord(AlipayClient alipayClient,PayRefund payRefund) {

        String orderTkCode = payRefund.getOrderTkCode();

        PayRefundLog log = new PayRefundLog(payRefund.getPayCode(),payRefund.getOrderCode()
                ,orderTkCode,payRefund.getRefundFee());
        log.setReqData("{\"trade_no\":"+payRefund.getPayTradeNo()+"}");
        try {
            AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
            request.setBizContent("{" + "\"out_request_no\":\""+payRefund.getOrderTkCode()+"\","+
                    "\"trade_no\":\""+payRefund.getPayTradeNo()+"\"}");
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                notifyBiz(payRefund);
            } else {
                log.setErrorCode(response.getCode()+"@"+response.getSubCode());
                log.setErrDes(response.getMsg()+"@"+response.getSubMsg());
                logger.error("ali refund query result fails.params: payRefund:{}", payRefund);
            }
            log.setResData(response.getBody());
        } catch (Exception e) {
            log.setErrorCode("ali refund query error");
            log.setErrDes(e.getMessage());
            logger.error("###############queryRefund get Exception ########", e);
        } finally {
            saveRefundLog(log);
        }
    }

    /**
     * 通知业务系统，退款处理已成功
     * @param payRefund
     */
    private void notifyBiz(PayRefund payRefund) {
        //更新t_b_pay_refund 状态为退款成功
        PayRefund updatePayRefund = new PayRefund();
        updatePayRefund.setRefundOkTime(DateFormatUtils.getNow());
        updatePayRefund.setId(payRefund.getId());
        updatePayRefund.setRefundStatus(RefundStatusEnum.REFUND_SUCCESS.value());
        updatePayRefund.setMemo("退款成功");
        dao.update(updatePayRefund);
    }

    private PayRefund savePayRefund(PayRefund entity) {
        dao.save(entity);
        return entity;
    }

    private void saveRefundLog(PayRefundLog log) {
        dao.save(log);
    }
}
