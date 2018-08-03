package com.eds.ma.bis.common;

/**
 * EdsAppId
 * @Author gaoyan
 * @Date: 2018/8/2
 */
public enum EdsAppId {

	/**
	 * 微信客户端
	 */
	eds_wx("eds_wx"),
	/**
	 * ali客户端
	 */
	eds_ali("eds_ali");

	private final String value;

	EdsAppId(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}

}
