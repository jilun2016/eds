package com.eds.ma.bis.device;

/**
 * 甲醛检测结果状态枚举
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public enum HchoResultEnum {

	/**
	 * 合格
	 */
	HCHO_RESULT_OK("合格"),

	/**
	 * 轻度污染
	 */
	HCHO_RESULT_LIGHT("轻度污染"),

	/**
	 * 重度污染
	 */
	HCHO_RESULT_HEAVY("重度污染"),

	/**
	 * 极度污染
	 */
	HCHO_RESULT_EXTREME("极度污染");

	private final String value;

	HchoResultEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
