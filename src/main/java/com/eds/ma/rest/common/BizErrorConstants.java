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
	 * 租借设备超出范围
	 */
	String DEVICE_RENT_OUT_RANGE = "E10006";

	/**
	 * 当前钱包无法提现
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

	/**
	 * 短信发送频率过高,请30秒之后重试
	 */
	String SMS_CHECK_FREQUENCY_ERROR = "E10013";

    /**
     * 短信验证码错误,清重试
     */
    String SMSCODE_ERROR = "E10014";
    /**
     * 短信验证码过期,清重试
     */
    String SMSCODE_EXPIRED = "E10015";

	/**
	 * 未查询到归还的设备
	 */
	String DEVICE_RETURN_NOT_EXIST_ERROR = "E10016";

	/**
	 * 设备已经归还,如有问题,请联系客服
	 */
	String DEVICE_ON_RETURN_STATUS_ERROR = "E10017";

	/**
	 * 归还设备超出范围
	 */
	String DEVICE_RETURN_OUT_RANGE = "E10018";

    /**
     * 未查询到归属店铺,请重新归还,如有问题,请联系客服
     */
    String DEVICE_RETURN_SP_NOT_EXIST = "E10019";

    /**
     * 设备归还发生异常,请联系客服
     */
    String DEVICE_RETURN_ORDER_ID_NOT_EXIST_ERROR = "E10020";

    /**
     * 更新订单信息失败,请联系客服
     */
    String DEVICE_RETURN_ORDER_NOT_EXIST_ERROR = "E10021";

	/**
	 * 提现验证失败,请重新提现
	 */
	String WALLET_WITHDRAW_MOBILE_EMPTY = "E10022";

	/**
	 * 您当前正在租借设备,请归还设备后再进行提现
	 */
	String DEVICE_RENT_WITHDRAW_ERROR = "E10023";

	/**
	 * 您还没有关注公众号,请关注后再进行领取
	 */
	String USER_COUPON_UNSUBSCRIBE_ERROR = "E10024";

	/**
	 * 您已经领取过优惠券了
	 */
	String USER_COUPON_UNSUBSCRIBE_CLAIM_ERROR = "E10025";

	/**
	 * 设备状态错误,无法租借,请联系客服
	 */
	String DEVICE_RENT_STATUS_ERROR = "E10026";

	/**
	 * 设备位置信息未获取到,请稍后再试
	 */
	String DEVICE_POSITISON_ERROR = "E10027";

	/**
	 * 设备状态错误,无法归还,请联系客服
	 */
	String DEVICE_RETURN_STATUS_ERROR = "E10028";

	/**
	 * 店铺信息不存在
	 */
	String SP_NOT_EXIST_ERROR = "E10029";

    /**
     * 当前店铺存在空闲设备,请及时租借
     */
    String SP_DEVICE_EXIST_IDLE_ERROR = "E10030";

    /**
     * 您已经预约该店铺的设备,请耐心等待设备空闲通知
     */
    String SP_DEVICE_RESERVE_DUPLICATE_ERROR = "E10031";


}
