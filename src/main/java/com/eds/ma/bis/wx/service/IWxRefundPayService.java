package com.eds.ma.bis.wx.service;


import com.eds.ma.bis.order.entity.PayOrder;

import java.math.BigDecimal;

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
}

