package com.eds.ma.socket.message.handler;

import com.eds.ma.socket.message.MessageTypeConstants;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.eds.ma.socket.util.SocketMessageUtils;
import com.eds.ma.util.SpringUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * 基础消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component
public class CommonMessageHandler {

    private static Logger logger = Logger.getLogger(CommonMessageHandler.class);

    @Autowired
    protected MongoTemplate mongoTemplate;

    //解析消息头信息
    public CommonHeadMessageVo parseHeadMessage(String[] mesasge){
        logger.debug("parseHeadMessage({})", mesasge);
        Integer messageLength = mesasge.length;
        //解析消息,不同消息类型不同处理
        //设备注册上报命令：24个字节,()
        if(messageLength == 24){
            return parseCommonHeadMessage(mesasge);
        }

        //设备每1分钟主动发，心跳包：12个字节
        if(messageLength == 12){
            return parseHeartBeatHeadMessage(mesasge);
        }

        //查询设备检测结果信息：长度35字节
        if(messageLength == 35){
            return parseCommonHeadMessage(mesasge);
        }

        //设备历史上传消息：长度44字节
        if(messageLength == 44){
            return parseHistoryHeadMessage(mesasge);
        }

       return null;
    }

    private CommonHeadMessageVo parseCommonHeadMessage(String[] mesasge){
        logger.debug("parseCommonHeadMessage({})", mesasge);
        CommonHeadMessageVo commonHeadMessageVo = new CommonHeadMessageVo();
        //解析设备种类
        commonHeadMessageVo.setDeviceKind(SocketMessageUtils.H2L(mesasge[0]));
        //解析设备编号
        commonHeadMessageVo.setDeviceCode(SocketMessageUtils.H2L(mesasge,1,7));
        //解析报文编号
        commonHeadMessageVo.setMessageNo(SocketMessageUtils.H2L(mesasge,8,4));
        //解析报文功能码
        commonHeadMessageVo.setMessageType(SocketMessageUtils.H2L(mesasge,12,1));
        return commonHeadMessageVo;
    }

    private CommonHeadMessageVo parseHistoryHeadMessage(String[] mesasge){
        logger.debug("parseHistoryHeadMessage({})", mesasge);
        CommonHeadMessageVo commonHeadMessageVo = new CommonHeadMessageVo();
        //解析设备种类
        commonHeadMessageVo.setDeviceKind(SocketMessageUtils.H2L(mesasge[0]));
        //解析设备编号
        commonHeadMessageVo.setDeviceCode(SocketMessageUtils.H2L(mesasge,1,7));
        return commonHeadMessageVo;
    }

    private CommonHeadMessageVo parseHeartBeatHeadMessage(String[] mesasge){
        logger.debug("parseHeartBeatHeadMessage({})", mesasge);
        CommonHeadMessageVo commonHeadMessageVo = new CommonHeadMessageVo();
        //解析设备种类
        commonHeadMessageVo.setDeviceKind(SocketMessageUtils.H2L(mesasge[0]));
        //解析设备编号
        commonHeadMessageVo.setDeviceCode(SocketMessageUtils.H2L(mesasge,1,7));
        //解析报文功能码
        commonHeadMessageVo.setMessageType(SocketMessageUtils.H2L(mesasge[8]));
        //解析设备信号强度
        commonHeadMessageVo.setDeviceSignalStrength(SocketMessageUtils.H2L(mesasge[11]));
        return commonHeadMessageVo;
    }

    public void sendDataMessage(Long messageType,Long deviceCode, Long... mesasgeField){
        BaseMessageHandler messageHandler = getMessageHandler(messageType);
        if(Objects.nonNull(messageHandler)){
            CommonHeadMessageVo commonHeadMessageVo = messageHandler.buildHeadMessage(deviceCode);
            messageHandler.sendDataMessage(commonHeadMessageVo,mesasgeField);
        }
    }


    public BaseMessageHandler getMessageHandler(Long messageType){
        //心跳消息处理
        if(Objects.equals(messageType,MessageTypeConstants.DEVICE_HEARTBEAT)){
            return SpringUtils.getBean("heartBeatMessageHandler");
        }
        //注册消息处理
        if(Objects.equals(messageType,MessageTypeConstants.DEVICE_REGISTER)){
            return SpringUtils.getBean("registerMessageHandler");
        }
        //报告消息处理
        if(Objects.equals(messageType,MessageTypeConstants.DEVICE_REPORT)){
            return SpringUtils.getBean("reportMessageHandler");
        }

        //GPS消息处理
        if(Objects.equals(messageType,MessageTypeConstants.DEVICE_GPS)){
            return SpringUtils.getBean("gpsMessageHandler");
        }

        //设备控制消息处理
        if(Objects.equals(messageType,MessageTypeConstants.DEVICE_CONTROL)){
            return SpringUtils.getBean("deviceControlHandler");
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(String.valueOf((char)SocketMessageUtils.H2L("2e").intValue()));
        System.out.println("1200".substring(0, 3));
        System.out.println(Long.parseLong("01".replaceAll("^0[x|X]", ""), 16));
        System.out.println(SocketMessageUtils.H2B("01"));
        System.out.println(Long.parseLong("111", 2));
    }



}