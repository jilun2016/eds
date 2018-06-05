package com.eds.ma.socket;

/**
 * @Description: 设备指令信息
 * @author whl
 * @date 2014-9-30 下午12:35:35
 *
 */
public interface DeviceCodeConstant  {

	/**
	 * 设备指令开始标记 0x18
	 */
	Long DEVICE_ORDER_START_FLAG = 24L;

	/**
	 * 设备指令结束标记 0x28
	 */
    Long DEVICE_ORDER_END_FLAG = 40L;

	/**
	 * 仪表状态 归位未借 1
	 */
    Long DEVICE_STATUS_DZJ = 1L;

	/**
	 * 仪表状态 租借中 2
	 */
    Long DEVICE_STATUS_SYZ = 2L;

	/**
	 * 仪表状态 已锁定 3
	 */
    Long DEVICE_STATUS_YSD = 3L;

	/**
	 * 仪表状态 测量中 4
	 */
    Long DEVICE_STATUS_CLZ = 4L;

	/**
	 * 仪表状态 本次测量报告
	 */
    Long DEVICE_STATUS_REPORT = 5L;

}
