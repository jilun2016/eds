package com.eds.ma.bis.coupon;

/**
 * 会员优惠券状态
 * @author gaoyan
 *
 */
public enum CouponStatusEnum {
	
	/**
	 * 未使用
	 */
	S_HYYHQZT_WSY("S_HYYHQZT_WSY"),
	
	/**
	 * 已使用
	 */
	S_HYYHQZT_YSY("S_HYYHQZT_YSY"),
	
	/**
	 * 已过期
	 */
	S_HYYHQZT_YGQ("S_HYYHQZT_YGQ");
	
	private final String value;
	
	CouponStatusEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
