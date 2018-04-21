package com.eds.ma.bis.device;

/**
 * 订单状态枚举
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public enum OrderStatusEnum {

	/**
	 * 进行中
	 */
	S_DDZT_JXZ("S_DDZT_JXZ"),

	/**
	 * 已结束
	 */
	S_DDZT_YJS("S_DDZT_YJS");

	private final String value;

	OrderStatusEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
