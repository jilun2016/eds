package com.eds.ma.socket.message.vo;

import lombok.Data;

/**
 * 上传设备消息头信息
 * @Author gaoyan
 * @Date: 2018/7/28
 */
@Data
public class UploadHeadMessageVo {

    /**
     * 设备种类
     */
    private Long deviceKind;
    /**
     * 设备编号
     */
    private Long deviceCode;

    /**
     * 设备信号强度
     */
    private Long deviceSignalStrength;

}
