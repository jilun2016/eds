package com.eds.ma.bis.coupon;

/**
 * 优惠券种类
 * @author gaoyan
 *
 */
public enum CouponKindEnum {

	/**
	 * 订阅公众号优惠券
	 */
	S_YHQZL_SUBSCRIBE("S_YHQZL_SUBSCRIBE"),

	/**
	 * 分享优惠券
	 */
	S_YHQLX_SHARE("S_YHQLX_SHARE"),

	/**
	 * 下单获取优惠券
	 */
	S_YHQZL_ORDER("S_YHQZL_ORDER");

	private final String value;

	CouponKindEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
