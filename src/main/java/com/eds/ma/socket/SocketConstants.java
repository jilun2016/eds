package com.eds.ma.socket;

/**
 * socket模块常量
 * @Author gaoyan
 * @Date: 2017/7/13
 */
public interface SocketConstants {

	/**
	 * 设备类型-甲醛检测仪
	 */
	Long DEVICE_KIND_HCHO = 33L;

	/**
	 *  查询GPS 位置信息 请求硬件验证码 0xdd 0xcc 0xbb 0xaa
	 */
	Long GPS_REQUEST_CHECK_CODE = 3721182122L;


	/**
	 *  校验码异或标准值 0XA5
	 */
	Long XOR_CHECK_CODE = 165L;



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
