package com.eds.ma.bis.wx.sdk.pay.payment;

/**
 * 支付常量
 * @Author gaoyan
 * @Date: 2018/8/5
 */
public class WxPayConstants {
	
	/**
	 * SUCCESS—退款成功
	 */
	public static final String REFUND_SUC = "SUCCESS";
	/**
	 * FAIL—退款失败
	 */
	public static final String REFUND_FAIL = "FAIL";
	/**
	 * PROCESSING—退款处理中
	 */
	public static final String REFUND_PROCESSING = "PROCESSING";
	/**
	 * NOTSURE—未确定，需要商户原退款单号重新发起
	 */
	public static final String REFUND_NOTSURE = "NOTSURE";
	/**
	 * CHANGE—转入代发，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，
	 * 资金回流到商户的现金帐号，需要商户人工干预，通过线下或者财付通转账的方式进行退款。
	 */
	public static final String REFUND_CHANGE = "CHANGE";
}
