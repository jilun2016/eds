package com.eds.ma.bis.wx.service;


import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.wx.entity.PayRefund;

import java.math.BigDecimal;
import java.util.List;

/**
 * 微信支付退款服务
 * @Author gaoyan
 * @Date: 2018/4/5
 */
public interface IWxRefundPayService {
    /**
     * 退款申请接口
     * @param payOrder
     * @param tkFee
     */
    void submiteRefund(PayOrder payOrder, BigDecimal tkFee);

    /**
     * 微信退货查询处理接口
     * @param payRefundList
     */
    void wxRefundQuery(List<PayRefund> payRefundList);
}

