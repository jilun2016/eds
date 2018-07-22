package com.eds.ma.bis.user;

/**
 * 分享状态类型
 * @author gaoyan
 *
 */
public enum UserDistStatusEnum {

	/**
	 * 分享进行中
	 */
	S_DIST_JXZ("S_DIST_JXZ"),

	/**
	 * 分享结束
	 */
	S_DIST_YJS("S_DIST_YJS");

	private final String value;

	UserDistStatusEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
