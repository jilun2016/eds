package com.eds.ma.socket.message.handler;

import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.eds.ma.socket.message.vo.UploadHeadMessageVo;
import com.xcrm.log.Logger;
import org.springframework.stereotype.Component;


/**
 * 基础消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component
public class MessageHandler {

    private static Logger logger = Logger.getLogger(MessageHandler.class);

    public void processMessage(String[] mesasge){
        logger.debug("messageReceived({})", mesasge);
        parseHeadMessage(mesasge);

    }

    //解析消息头信息
    private void parseHeadMessage(String[] mesasge){
        Integer messageLength = mesasge.length;
        //解析消息,不同消息类型不同处理
        //设备主动上报命令：24个字节,(设备注册)
        if(messageLength == 24 || messageLength == 13){
            parseCommonHeadMessage(mesasge);
        }

        //服务器访问设备报文：长度35字节
        if(messageLength == 35){
            parseCommonHeadMessage(mesasge);
        }

        //设备每1分钟主动发，心跳包：12个字节
        if(messageLength == 12){

        }
    }

    private CommonHeadMessageVo parseCommonHeadMessage(String[] mesasge){
        logger.debug("parseCommonHeadMessage({})", mesasge);
        CommonHeadMessageVo commonHeadMessageVo = new CommonHeadMessageVo();
        //解析设备种类
        commonHeadMessageVo.setDeviceKind(H2L(mesasge[0]));
        //解析设备编号
        commonHeadMessageVo.setDeviceCode(H2L(mesasge,1,7));
        //解析报文编号
        commonHeadMessageVo.setMessageNo(H2L(mesasge,8,4));
        //解析报文功能码
        commonHeadMessageVo.setMessageType(H2L(mesasge,12,1));
        return commonHeadMessageVo;
    }

    private UploadHeadMessageVo parseUploadHeadMessage(String[] mesasge){
        logger.debug("parseCommonHeadMessage({})", mesasge);
        UploadHeadMessageVo uploadHeadMessageVo = new UploadHeadMessageVo();
        //解析设备种类
        uploadHeadMessageVo.setDeviceKind(H2L(mesasge[0]));
        //解析设备编号
        uploadHeadMessageVo.setDeviceCode(H2L(mesasge,1,7));
        //解析设备信号强度
        uploadHeadMessageVo.setDeviceSignalStrength(H2L(mesasge[8]));

        //解析设备状态
        String deviceStatusBinary = H2B(mesasge[9]);

        uploadHeadMessageVo.setDeviceSignalStrength(H2L(mesasge[8]));
        //解析电池容量
        uploadHeadMessageVo.setDeviceSignalStrength(H2L(mesasge[8]));

        return uploadHeadMessageVo;
    }

    private Long H2L(String hexMessage){
        return Long.parseLong(hexMessage.replaceAll("^0[x|X]", ""),16);
    }

    private Long H2L(String[] mesasge,int fromIndex,int offset){
        if(mesasge.length >= (fromIndex + offset)){
            StringBuilder hexMessage = new StringBuilder();
            for (int i = fromIndex; i < (fromIndex + offset); i++) {
                hexMessage.append(mesasge[i]);
            }
            return H2L(hexMessage.toString());
        }
        return null;
    }


    /**
     * 将十六进制的字符串转换成二进制的字符串
     *
     * @param hexMessage
     * @return
     */
    private String H2B(String hexMessage) {

        if (hexMessage == null || "".equals(hexMessage)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 将每一个十六进制字符分别转换成一个四位的二进制字符
        for (int i = 0; i < hexMessage.length(); i++) {
            String indexStr = hexMessage.substring(i, i + 1);
            StringBuilder binaryStr = new StringBuilder(Long.toBinaryString(H2L(indexStr)));
            while (binaryStr.length() < 4) {
                binaryStr.insert(0, "0");
            }
            sb.append(binaryStr);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        MessageHandler messageHandler = new MessageHandler();

        System.out.println(Long.parseLong("A0".replaceAll("^0[x|X]", ""), 16));
        System.out.println(messageHandler.H2B("A0"));
    }



}