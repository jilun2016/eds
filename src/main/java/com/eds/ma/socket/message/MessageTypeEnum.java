package com.eds.ma.socket.message;

/**
 * 设备消息类型
 * @Author gaoyan
 * @Date: 2018/7/28
 */
public interface MessageTypeEnum {

	/**
	 * 设备控制和设置参数 0xb1
	 */
	Long DEVICE_STATUS = 177L;

	/**
	 * 设备GPS位置信息 0xb2
	 */
	Long DEVICE_POSITION = 178L;

	/**
	 * 设备检测结果信息   0xb3
	 */
	Long DEVICE_REPORT = 179L;

	/**
	 * 设备主动上报命令   0xf1
	 */
	Long DEVICE_UPLOAD = 241L;

}
