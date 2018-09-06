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
	 *  设备控制信息 请求硬件验证码 0xaa 0xbb 0xcc 0xdd
	 */
	Long CONTROL_REQUEST_CHECK_CODE = 2864434397L;

	/**
	 *  设备报告信息 请求硬件验证码 0xbb 0xaa 0xdd 0xcc
	 */
	Long REPORT_REQUEST_CHECK_CODE = 3148537292L;

	/**
	 *  校验码异或标准值 0XA5
	 */
	Long XOR_CHECK_CODE = 165L;

	/**
	 * 指令 设备控制 0xa5 允许
	 */
	Long DEVICE_LOCK_LOCK = 165L;

	/**
	 * 指令 设备控制 0x5a 锁定
	 */
	Long DEVICE_LOCK_UNLOCK = 90L;

}
