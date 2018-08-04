package com.eds.ma.bis.wx.vo;

import lombok.Data;

/**
 * 支付宝退款 类
 * @Author gaoyan
 * @Date: 2018/8/5
 */
@Data
public class AlipayRefund {
    /**
     * 必须 商户订单号
     */
    private String out_trade_no;
    /**
     * 必须 支付宝交易号
     */
    private String trade_no;
    /**
     * 必须 退款金额
     */
    private String refund_amount;
    /**
     * 可选 代表 退款的原因说明
     */
    private String refund_reason;
    /**
     * 可选 标识一次退款请求，同一笔交易多次退款需要保证唯一（就是out_request_no在2次退款一笔交易时，要不一样），如需部分退款，则此参数必传
     */
    private String out_request_no;
    /**
     * 可选 代表 商户的操作员编号
     */
    private String operator_id;
    /**
     * 可选 代表 商户的门店编号
     */
    private String store_id;
    /**
     * 可选 代表 商户的终端编号
     */
    private String terminal_id;
}
