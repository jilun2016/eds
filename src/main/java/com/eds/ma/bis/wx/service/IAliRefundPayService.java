package com.eds.ma.bis.wx.service;


import com.eds.ma.bis.order.entity.PayOrder;
import com.eds.ma.bis.wx.entity.PayRefund;

import java.math.BigDecimal;
import java.util.List;

/**
 * ali支付退款服务
 * @Author gaoyan
 * @Date: 2018/4/5
 */
public interface IAliRefundPayService {

    /**
     * ali退款申请接口
     * @param payOrder
     * @param tkFee
     */
    void submiteRefund(PayOrder payOrder, BigDecimal tkFee);

    /**
     * ali退款查询接口
     * @param payRefundList
     */
    void aliRefundQuery(List<PayRefund> payRefundList);
}

