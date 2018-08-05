package com.eds.ma.bis.wx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.eds.ma.bis.order.OrderCodeCreater;
import com.eds.ma.bis.order.OrderPayTypeEnum;
import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.wx.RefundStatusEnum;
import com.eds.ma.bis.wx.entity.PayRefund;
import com.eds.ma.bis.wx.entity.PayRefundLog;
import com.eds.ma.bis.wx.sdk.pay.base.PaySetting;
import com.eds.ma.bis.wx.sdk.pay.payment.Payments;
import com.eds.ma.bis.wx.sdk.pay.payment.WxPayConstants;
import com.eds.ma.bis.wx.sdk.pay.payment.bean.RefundQuery;
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
import com.xcrm.common.json.JacksonJsonHandler;
import com.xcrm.common.json.JsonHandler;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
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
                entity.setPayType(OrderPayTypeEnum.S_ZFFS_WX.value());
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


    @Override
    public void wxRefundQuery(List<PayRefund> payRefundList) {
        for(PayRefund payRefund:payRefundList){
            operateRefundRecord(payRefund);
        }
    }

    /**
     * 退款处理
     */
    private void operateRefundRecord(PayRefund payRefund) {

        String orderTkCode = payRefund.getOrderTkCode();

        PayRefundLog log = new PayRefundLog(payRefund.getPayCode(),payRefund.getOrderCode()
                ,orderTkCode,payRefund.getRefundFee());

        try {
            operateWxRefund(payRefund, log);
        } catch (Exception e) {
            log.setErrorCode("xcrm refund query error");
            log.setErrDes(e.getMessage());
            logger.error("###############queryRefund get Exception ########", e);
        } finally {
            saveRefundLog(log);
        }
    }

    /**
     * 微信退款处理
     * @param payRefund
     * @param log
     * @throws Exception
     */
    private void operateWxRefund(PayRefund payRefund, PayRefundLog log) throws Exception {
        //退款单号
        String orderTkCode = payRefund.getOrderTkCode();
        log.setReqData("{\"refundId\":"+payRefund.getRefundId()+"}");
        //查询当前的支付方式
        PaySetting paySetting = getPaySetting();
        RefundQuery refundQuery = Payments.with(paySetting).refundQueryByRefundId(payRefund.getRefundId());
        if(Objects.isNull(refundQuery)
                ||Objects.isNull(refundQuery.getReturnCode())
                ||Objects.equals(refundQuery.getReturnCode(),"FAIL")){
            if(Objects.nonNull(refundQuery)){
                log.setResData(refundQuery.toString());
            }
            logger.error("wx refund query returnCode fails.params: paySetting:{},payRefund:{}", paySetting,payRefund);
        }else if(Objects.equals(refundQuery.getResultCode(),"FAIL")){
            log.setResData(refundQuery.toString());
            log.setErrorCode(refundQuery.getErrorCode());
            log.setErrDes(refundQuery.getErrorCodeDesc());
            logger.error("wx refund query resultCode fails.params: paySetting:{},payRefund:{}", paySetting,payRefund);
        }else{
            log.setResData(refundQuery.toString());
            List<RefundQuery.Refund> refundOrderDataList = refundQuery.getRefunds();
            for(RefundQuery.Refund wxRefundData : refundOrderDataList) {
                //该笔 微信返回商户退款单号
                String outRefundNo = wxRefundData.getOutRefundNo();
                //该笔退款状态
                String refundStatus = wxRefundData.getRefundStatus();
                if(orderTkCode.equals(outRefundNo)) {
                    switch (refundStatus) {
                        //不做处理等待下一次查询
                        case WxPayConstants.REFUND_NOTSURE:
                        case WxPayConstants.REFUND_PROCESSING:
                            //退款申请时间
                            long created = payRefund.getCreated().getTime();
                            long now = System.currentTimeMillis();
                            long between = (now - created) / (1000 * 60 * 60);
                            if(between >= 48) {
                                //退款时间大于48小时，认为是退款超时，需要人工处理
                                operateRefundTimeout(payRefund, "48小时退款状态未确定，退款状态:" + refundStatus);
                                log.setErrorCode("48小时退款状态未确定，WX_refundStatus=" + refundStatus);
                                log.setErrDes("48小时退款状态未确定，需要人工干预进行处理");
                            } else {
                                log.setErrorCode("WX_refundStatus==" + refundStatus);
                                log.setErrDes("需要等待下次查询来确定退款状态");
                            }
                            break;
                        //退款异常，需要人工干预，或者运管平台处理
                        case WxPayConstants.REFUND_CHANGE:
                        case WxPayConstants.REFUND_FAIL:
                            log.setErrorCode("WX_refundStatus==" + refundStatus);
                            log.setErrDes("退款异常，需要人工干预，或者运管平台处理");
                            notifyWgPlatform(payRefund, "退款异常，需要人工干预，或者运管平台处理,退款状态:" + refundStatus);
                            break;
                        //处理成功
                        case WxPayConstants.REFUND_SUC:
                            notifyBiz(payRefund);
                            break;
                        default:
                            log.setErrorCode("WX_refundStatus==" + refundStatus);
                            log.setErrDes("微信返回未知的退款状态，不知咋处理了!!!!");
                            notifyWgPlatform(payRefund, "微信返回未知的退款状态,退款状态:" + refundStatus);
                            break;
                    }
                }
            }
        }
    }

    /**
     * 退款处理超时，支付系统无法处理，需要人工干预
     * @param payRefund      退款对象
     * @param memo   接口返回数据（wx,alipay）
     */
    private void operateRefundTimeout(PayRefund payRefund,String memo) {
        PayRefund updateFailMemoPayRefund = new PayRefund();
        updateFailMemoPayRefund.setMemo(memo);
        updateFailMemoPayRefund.setRefundStatus(RefundStatusEnum.REFUND_TIMEOUT.value());
        updateFailMemoPayRefund.setId(payRefund.getId());
        dao.update(updateFailMemoPayRefund);
    }

    /**
     * 退款处理失败，支付系统无法处理，需要人工干预
     * @param payRefund      退款对象
     * @param memo   接口返回数据（wx,alipay）
     */
    private void notifyWgPlatform(PayRefund payRefund,String memo) {
        PayRefund updateFailMemoPayRefund = new PayRefund();
        updateFailMemoPayRefund.setMemo(memo);
        updateFailMemoPayRefund.setRefundStatus(RefundStatusEnum.REFUND_FAILED.value());
        updateFailMemoPayRefund.setId(payRefund.getId());
        dao.update(updateFailMemoPayRefund);
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
