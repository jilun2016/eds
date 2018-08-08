package com.eds.ma.socket.message.handler;

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

    public abstract void processDataMessage();

    public void parse(String[] mesasge){
        parseBaseMessage(mesasge);
    }

    /**
     * 解析基础消息
     * @param mesasge
     */
    private void parseBaseMessage(String[] mesasge){
        System.out.println(mesasge);
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