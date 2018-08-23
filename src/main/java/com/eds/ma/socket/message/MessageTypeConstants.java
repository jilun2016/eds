package com.eds.ma.socket.message;

/**
 * 设备消息类型
 * @Author gaoyan
 * @Date: 2018/7/28
 */
public interface MessageTypeConstants {

	/**
	 * 设备控制和设置参数 0xb1
	 */
	Long DEVICE_CONTROL = 177L;

	/**
	 * 设备GPS位置信息 0xb2
	 */
	Long DEVICE_GPS = 178L;

	/**
	 * 设备检测结果信息   0xb3
	 */
	Long DEVICE_REPORT = 179L;

	/**
	 * 设备主动注册   0xf1
	 */
	Long DEVICE_REGISTER = 241L;

	/**
	 * 设备心跳命令   0xf2
	 */
	Long DEVICE_HEARTBEAT = 242L;

	/**
	 * 设备历史检测命令   0xf3
	 */
	Long DEVICE_HISTORY = 243L;

	/**
	 * 消息流向 硬件--->服务
	 */
	Integer MESSAGE_FLOW_RECEIVE = 1;

	/**
	 * 消息流向 服务--->硬件
	 */
	Integer MESSAGE_FLOW_SEND = 2;

}
