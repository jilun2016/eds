package com.eds.ma.bis.wx.service.impl;

import com.eds.ma.bis.order.OrderCodeCreater;
import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.wx.RefundStatusEnum;
import com.eds.ma.bis.wx.entity.PayRefund;
import com.eds.ma.bis.wx.entity.PayRefundLog;
import com.eds.ma.bis.wx.sdk.pay.base.PaySetting;
import com.eds.ma.bis.wx.sdk.pay.payment.Payments;
import com.eds.ma.bis.wx.sdk.pay.payment.bean.RefundRequest;
import com.eds.ma.bis.wx.sdk.pay.payment.bean.RefundResponse;
import com.eds.ma.bis.wx.sdk.pay.payment.bean.Signature;
import com.eds.ma.bis.wx.service.IWxRefundPayService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 微信支付退款service
 *
 * @Author gaoyan
 * @Date: 2017/6/12
 */
@Service
@Transactional
public class WxRefundPayServiceImpl implements IWxRefundPayService {

    private static Logger logger = Logger.getLogger(WxRefundPayServiceImpl.class);

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private BaseDaoSupport dao;


    @Override
    public void submiteRefund(PayOrder payOrder,BigDecimal tkFee){

        String payCode = payOrder.getPayCode();
        PayRefund dbPayRefund = queryPayRefundByPayCode(payCode);
        if(Objects.nonNull(dbPayRefund)){
            //退款单已存在，不要重复提交
            throw new BizCoreRuntimeException(BizErrorConstants.PAY_REFUND_ALLREADY_EXIST);
        }

        //查询当前的支付方式
        PaySetting paySetting = getPaySetting();

        BigDecimal orderPayMoney = payOrder.getPayMoney().multiply(BigDecimal.valueOf(100L));
        BigDecimal weixinTkFee = tkFee.multiply(BigDecimal.valueOf(100L));

        String orderTkCode = OrderCodeCreater.createTradeNO();
        String orderCode = payOrder.getOrderCode();
        RefundRequest req = new RefundRequest();
        req.setTransactionId(payOrder.getTradeNo());
        req.setTradeNumber(payOrder.getPayCode());
        req.setRefundNumber(orderTkCode);
        req.setTotalFee(orderPayMoney.intValue());
        req.setRefundFee(weixinTkFee.intValue());

        //退款日志
        PayRefundLog log = new PayRefundLog(payCode,orderCode,orderTkCode,tkFee);
        log.setReqData(req.toString());
        final Map<String,Object> result = new HashMap<>();
        try {
            Payments.with(paySetting).refund(req, new Payments.ResultListener() {
                @Override
                public void onFailByReturnCodeError(RefundResponse arg0) {
                    result.put("result", BizErrorConstants.PAY_SYSTEM_ERROR);
                    result.put("data", arg0);
                }
                @Override
                public void onFailByReturnCodeFail(RefundResponse arg0) {
                    result.put("result", BizErrorConstants.PAY_SYSTEM_ERROR);
                    result.put("data", arg0);
                }
                @Override
                public void onRefundFail(RefundResponse arg0) {
                    result.put("result", BizErrorConstants.PAY_REFUND_FAIL);
                    result.put("data", arg0);
                }
                @Override
                public void onRefundSuccess(RefundResponse arg0) {
                    result.put("result", BizErrorConstants.SUCCESS);
                    result.put("data", arg0);
                }
            });

            RefundResponse data = (RefundResponse) result.get("data");
            if(Objects.nonNull(data)){
                log.setResData(data.toString());
                log.setErrorCode(data.getErrorCode());
                log.setErrDes(data.getErrorCodeDesc());
            }

            if(Objects.equals(BizErrorConstants.SUCCESS, result.get("result"))) {
                //保存一条退款记录
                PayRefund entity = new PayRefund();
                entity.setCreated(new Timestamp(System.currentTimeMillis()));
                entity.setOrderCode(orderCode);
                entity.setOrderTkCode(orderTkCode);
                entity.setPayCode(payOrder.getPayCode());
                entity.setPayTradeNo(payOrder.getTradeNo());
                entity.setRefundFee(tkFee);
                entity.setRefundId(Objects.nonNull(data)?data.getRefundId():null);
                entity.setRefundStatus(RefundStatusEnum.REFUND_ING.value());
                entity.setSellerId(payOrder.getSellerId());
                entity.setBuyerId(payOrder.getBuyerId());
                savePayRefund(entity);
            } else {
                //微信返回错误
                throw new BizCoreRuntimeException(result.get("result").toString());
            }

        } catch (BizCoreRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.setErrorCode("eds refund sumbmit error");
            log.setErrDes(e.getMessage());
            logger.error("###############submiteRefund get Exception ########", e);
            throw new BizCoreRuntimeException(BizErrorConstants.PAY_REFUND_FAIL);
        } finally {
            saveRefundLog(log);
        }
    }

    private PayRefund savePayRefund(PayRefund entity) {
        dao.save(entity);
        return entity;
    }

    private void saveRefundLog(PayRefundLog log) {
        dao.save(log);
    }

    private PaySetting getPaySetting() {
        PaySetting paySetting = new PaySetting();
        paySetting.setAppId(sysConfig.getWxMaAppId());
        paySetting.setKey(sysConfig.getWxMerchantKey());
        paySetting.setMchId(sysConfig.getWxMchId());
        return paySetting;
    }

    private PayRefund queryPayRefundByPayCode(String payCode) {
        QueryBuilder query = QueryBuilder.where(Restrictions.eq("payCode", payCode))
                .and(Restrictions.eq("dataStatus", 1));
        return dao.query(query, PayRefund.class);
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
