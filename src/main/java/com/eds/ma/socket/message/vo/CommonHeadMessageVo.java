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

    public byte[] toBytes(){

        byte[] bts = new byte[13];
        bts[0] =  (byte) deviceKind.intValue();
        bts[1] =  (byte) deviceCode.intValue();
        bts[2] =  (byte) 0x34;
        bts[3] =  (byte) 0xe4;
        bts[4] =  (byte) 0xc2;
        bts[5] =  (byte) 0xa1;
        bts[6] =  (byte) 0x01;
        bts[7] =  (byte) 0x04;
        bts[8] =  (byte) 0x01;
        bts[9] =  (byte) 0x01;
        bts[10] =  (byte) 0x01;
        bts[11] =  (byte) 0x01;
        bts[12] =  (byte) 0xf1;
        return bts;
    }

}
