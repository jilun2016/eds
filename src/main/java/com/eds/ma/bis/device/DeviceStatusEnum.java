package com.eds.ma.bis.device;

/**
 * 设备状态枚举
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public enum DeviceStatusEnum {

	/**
	 * 使用中
	 */
	S_SPZT_SYZ("S_SPZT_SYZ"),

	/**
	 * 待租借
	 */
	S_SPZT_DZJ("S_SPZT_DZJ");
	
	private final String value;
	
	DeviceStatusEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
