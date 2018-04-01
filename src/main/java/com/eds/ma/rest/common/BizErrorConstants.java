package com.eds.ma.rest.common;

/**
 * 错误码定义
 * @Author gaoyan
 * @Date: 2017/10/19
 */
public interface BizErrorConstants {

	/**
	 * 获取小程序用户信息失败
	 */
	String WX_MA_SESSION_QUERY_ERROR = "E10001";

	/**
	 * 设备信息不存在
	 */
	String DEVICE_NOT_EXIST_ERROR = "E10002";

    /**
     * 设备正在使用中,无法租借
     */
    String DEVICE_ON_BORROW_STATUS_ERROR = "E10003";
}
