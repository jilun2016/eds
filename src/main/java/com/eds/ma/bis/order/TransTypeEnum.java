package com.eds.ma.bis.order;

/**
 * 订单交易类型
 * @Author gaoyan
 * @Date: 2018/4/5 0005
 */
public enum TransTypeEnum {

	/**
	 * 押金充值
	 */
	S_JYLX_YJCZ("S_JYLX_YJCZ"),
	/**
	 * 余额充值
	 */
	S_JYLX_YECZ("S_JYLX_YECZ"),
	/**
	 * 提现
	 */
	S_JYLX_TX("S_JYLX_TX");

	private final String value;

	TransTypeEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
