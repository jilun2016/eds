package com.eds.ma.bis.user.vo;

import com.eds.ma.bis.order.entity.PayOrder;

import java.math.BigDecimal;

/**
 * 退款参数
 * @Author gaoyan
 * @Date: 2017/12/24
 */
public class PayRefundVo {

    /**
     * 退款金额
     */
    private BigDecimal refundMoney;

    /**
     * 支付订单
     */
    private PayOrder payOrder;

    public BigDecimal getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(BigDecimal refundMoney) {
        this.refundMoney = refundMoney;
    }

    public PayOrder getPayOrder() {
        return payOrder;
    }

    public void setPayOrder(PayOrder payOrder) {
        this.payOrder = payOrder;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PayRefundVo{");
        sb.append("refundMoney=").append(refundMoney);
        sb.append(", payOrder=").append(payOrder);
        sb.append('}');
        return sb.toString();
    }
}
