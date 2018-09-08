package com.eds.ma.bis.message;

/**
 * 消息事件
 * @Author gaoyan
 * @Date: 2018/4/14 0014
 */
public enum TmplEvent {
	/**
	 * 会员注册时
	 */
	member_register("member_register"),

	/**
	 * 钱包提现提醒
	 */
	wallet_withdraw_check("wallet_withdraw_check"),

	/**
	 * 检测结果消息推送
	 */
	device_check_message("device_check_message");


	private final String value;
	
	TmplEvent(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
