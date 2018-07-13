package com.eds.ma.socket;

/**
 * socket模块常量
 * @Author gaoyan
 * @Date: 2017/7/13
 */
public interface SocketConstants {

	/**
	 *  指令 硬件上传
	 */
	Integer DEVICE_FLOW_CLIENT_UPLOAD = 1;

	/**
	 * 指令 服务端发送
	 */
	Integer DEVICE_FLOW_SERVER_SEND = 2;


	/**
	 * 指令 锁定仪表
	 */
	Integer DEVICE_LOCK_LOCK = 5;

	/**
	 * 指令 解锁仪表
	 */
	Integer DEVICE_LOCK_UNLOCK = 6;

}