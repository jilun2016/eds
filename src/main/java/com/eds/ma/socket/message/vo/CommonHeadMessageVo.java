package com.eds.ma.socket.message.vo;

import com.eds.ma.socket.util.SocketMessageUtils;
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

        byte[] deviceKindBytes  =  SocketMessageUtils.L2Bytes(deviceKind,1);
        byte[] deviceCodeBytes = SocketMessageUtils.L2Bytes(deviceCode,7);
        byte[] messageNoBytes = SocketMessageUtils.L2Bytes(messageNo,4);
        byte[] messageTypeBytes = SocketMessageUtils.L2Bytes(messageType,1);

        return SocketMessageUtils.combineBytes(deviceKindBytes,deviceCodeBytes,messageNoBytes,messageTypeBytes);
    }

    public static void main(String[] args) {
        CommonHeadMessageVo commonHeadMessageVo = new CommonHeadMessageVo();
        commonHeadMessageVo.setMessageType(1L);
        commonHeadMessageVo.setMessageNo(123L);
        commonHeadMessageVo.setDeviceKind(1L);
        commonHeadMessageVo.setDeviceCode(123L);
        System.out.println(commonHeadMessageVo.toBytes());
    }

}
