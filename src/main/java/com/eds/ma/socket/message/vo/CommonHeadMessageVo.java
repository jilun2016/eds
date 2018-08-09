package com.eds.ma.socket.message.vo;

import lombok.Data;

/**
 * 通用设备消息头信息
 * @Author gaoyan
 * @Date: 2018/7/28
 */
@Data
public class CommonHeadMessageVo {

    /**
     * 设备种类
     */
    private Long deviceKind;
    /**
     * 设备编号
     */
    private Long deviceCode;
    /**
     * 报文编号
     * 对于心跳上传命令,该字段未使用
     */
    private Long messageNo;
    /**
     * 报文功能码
     */
    private Long messageType;

    /**
     * 设备信号强度
     * 对于非心跳上传命令,该字段未使用
     */
    private Long deviceSignalStrength;

}
