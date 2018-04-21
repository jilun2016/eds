package com.eds.ma.rest.common;

/**
 * 错误码定义
 * @Author gaoyan
 * @Date: 2017/10/19
 */
public interface BizErrorConstants {

    /**
     * 成功标示
     */
    String SUCCESS = "00000";

	/**
	 * 获取小程序用户信息失败
	 */
	String WX_MA_SESSION_QUERY_ERROR = "E10001";

	/**
	 * 设备已被租借或者不存在
	 */
	String DEVICE_NOT_EXIST_ERROR = "E10002";

    /**
     * 设备正在使用中,无法租借
     */
    String DEVICE_ON_BORROW_STATUS_ERROR = "E10003";

	/**
	 * 用户信息不存在
	 */
	String USER_NOT_EXIST_ERROR = "E10004";

    /**
     * 您的押金不足,请充值
     */
    String USER_DEPOSIT_NOT_ENOUGH_ERROR = "E10005";

	/**
	 * 归还设备超出范围
	 */
	String DEVICE_RETURN_OUT_RANGE = "E10006";

	/**
	 * 当前钱包无法体现
	 */
	String WALLET_WITHDRAW_ZERO_ERROR = "E10007";

	/**
	 * 提现发生错误,请联系客服
	 */
	String WALLET_WITHDRAW_MONEY_COMPARE_ERROR = "E10008";

    /**
     * 提现单已存在，不允许重复提交
     */
    String PAY_REFUND_ALLREADY_EXIST = "E10009";

    /**
     * 支付系统通讯失败
     */
    String PAY_SYSTEM_ERROR = "E10010";

    /**
     * 提现申请提交失败
     */
    String PAY_REFUND_FAIL = "E10011";

	/**
	 * 部分提现失败,请继续进行提现
	 */
	String WALLET_WITHDRAW_PART_ERROR = "E10012";



}
