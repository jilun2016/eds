package com.eds.ma.bis.coupon;

/**
 * 优惠券类型
 * @author gaoyan
 *
 */
public enum CouponTypeEnum {
	
	/**
	 * 折扣优惠券
	 */
	S_YHQLX_ZK("S_YHQLX_ZK"),
	
	/**
	 * 红包优惠券
	 */
	S_YHQLX_HB("S_YHQLX_HB");
	
	private final String value;
	
	private CouponTypeEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
