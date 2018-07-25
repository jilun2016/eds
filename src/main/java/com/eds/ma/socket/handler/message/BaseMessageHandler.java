package com.eds.ma.socket.handler.message;

import com.xcrm.log.Logger;


/**
 * 基础消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
public abstract class BaseMessageHandler {


    private static Logger logger = Logger.getLogger(BaseMessageHandler.class);

    public void process(String[] mesasge){
        logger.debug("messageReceived({})", mesasge);

    }

    public void parse(String[] mesasge){
        //解析消息,不同消息类型不同处理
        //设备主动上报命令：24个字节,(设备注册)
        //设备每1分钟主动发，心跳包：12个字节
        //服务器访问设备报文：长度35字节
        if(mesasge.length == 24){

        }

        if(mesasge.length == 12){

        }

        if(mesasge.length == 35){

        }

    }


    public static void main(String[] args) {
        String binaryValue = Long.toBinaryString(129);
        //取最后八位作为校验码字节
        String checkTenValueStr = binaryValue.substring(binaryValue.length() - 8);
        Integer.toString (Integer.parseInt (checkTenValueStr, 2), 16);
//        byte[] temp = new byte[3];
//        temp[0] =  (byte) 18;
//        temp[1] =  (byte) 0x01;
//        temp[2] =  (byte) 0x11;
//        System.out.println(temp);
    }

}