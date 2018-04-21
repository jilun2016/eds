package com.eds.ma.bis.message;

/**
 * 消息事件
 * @Author gaoyan
 * @Date: 2018/4/14 0014
 */
public enum TmplEvent {
	/**
	 * 余额充值提醒
	 */
	balance_recharge("balance_recharge"),

	/**
	 * 押金充值提醒
	 */
	deposit_recharge("deposit_recharge"),

	/**
	 * 钱包提现提醒
	 */
	wallet_withdraw_success("wallet_withdraw_success"),

	/**
	 * 钱包提现提醒
	 */
	wallet_withdraw_check("wallet_withdraw_check");


	private final String value;
	
	private TmplEvent(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
