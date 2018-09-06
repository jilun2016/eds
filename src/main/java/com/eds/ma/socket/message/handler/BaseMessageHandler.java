package com.eds.ma.socket.message.handler;

import com.eds.ma.socket.SocketConstants;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.eds.ma.socket.util.SocketMessageUtils;

/**
 * 基础消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
public abstract class BaseMessageHandler {

    public abstract void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge);

    public abstract void sendDataMessage(CommonHeadMessageVo commonHeadMessageVo, Long... mesasgeField);

    public abstract Long getMessageType();

    //构造消息头信息
    public CommonHeadMessageVo buildHeadMessage(Long deviceCode){
        CommonHeadMessageVo commonHeadMessageVo = new CommonHeadMessageVo();
        commonHeadMessageVo.setDeviceKind(SocketConstants.DEVICE_KIND_HCHO);
        commonHeadMessageVo.setDeviceCode(deviceCode);
        commonHeadMessageVo.setMessageNo(System.nanoTime());
        commonHeadMessageVo.setMessageType(getMessageType());
        return commonHeadMessageVo;
    }

    public byte[] buildMessageCheckByte(Long headSum,Long... messageValues){
        Long checkByteSum = headSum;
        for (Long messageValue : messageValues) {
            checkByteSum +=messageValue;
        }
        Long  xorValue = checkByteSum^SocketConstants.XOR_CHECK_CODE;
        //将异或值转换成1个字节
        return SocketMessageUtils.L2Bytes(xorValue,1);
    }

}