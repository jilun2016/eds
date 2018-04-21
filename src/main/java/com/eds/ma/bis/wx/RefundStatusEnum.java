package com.eds.ma.bis.wx;

/**
 * 退款状态
* @author Brian   
* @date 2016年3月9日 下午4:01:12
 */
public enum RefundStatusEnum {
	
	/**
	 * 退款中
	 */
	REFUND_ING("refunding"),
	/**
	 * 退款成功
	 */
	REFUND_SUCCESS("refund_suc"),
	/**
	 * 退款超时 针对微信退款的方式
	 */
	REFUND_TIMEOUT("refund_timeout"),
	/**
	 * 退款失败
	 */
	REFUND_FAILED("refund_failed");
	
	private final String value;
	
	private RefundStatusEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
