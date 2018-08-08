package com.eds.ma.bis.order;

/**
 * 订单支付类型
 * @author gaoyan
 *
 */
public enum OrderPayTypeEnum {
	
	/**
	 * 微信
	 */
	S_ZFFS_WX("S_ZFFS_WX"),
	/**
	 * 支付宝
	 */
	S_ZFFS_ZFB("S_ZFFS_ZFB");

	private final String value;
	
	OrderPayTypeEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
