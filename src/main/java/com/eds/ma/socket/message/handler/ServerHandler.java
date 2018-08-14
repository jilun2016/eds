package com.eds.ma.socket.message.handler;

import com.eds.ma.socket.SessionMap;
import com.eds.ma.socket.message.MessageTypeConstants;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.eds.ma.util.SpringUtils;
import com.xcrm.log.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.Objects;


/**
 * @author whl
 * @Description: mina服务端业务处理类
 * @date 2014-9-30 下午12:36:28
 */
public class ServerHandler extends IoHandlerAdapter {


    private static Logger logger = Logger.getLogger(ServerHandler.class);

    @Autowired
    private MessageHandler messageHandler;

    public ServerHandler() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause){
    }


    @Override
    public void messageReceived(IoSession session, Object message){
        logger.debug("messageReceived({})", message);

        //获取客户端发过来的key
        IoBuffer bbuf = (IoBuffer) message;
        logger.info("收到消息：" + bbuf.getHexDump());
        String[] mesasgeArray = bbuf.getHexDump().split(" ");
        CommonHeadMessageVo commonHeadMessageVo = messageHandler.parseHeadMessage(mesasgeArray);
        //如果字节错误,那么忽略消息
        if(Objects.nonNull(commonHeadMessageVo)){
            //根据消息的报文功能码不同,走不同处理
            BaseMessageHandler messageHandler = getMessageHandler(commonHeadMessageVo.getMessageType());
            if(Objects.nonNull(messageHandler)){
                messageHandler.processDataMessage(commonHeadMessageVo,mesasgeArray);
            }
        }

        //保存客户端的会话session
        SessionMap sessionMap = SessionMap.newInstance();
        sessionMap.addSession(String.valueOf(((InetSocketAddress) session.getLocalAddress()).getPort()), session);
    }


    @Override
    public void messageSent(IoSession session, Object message) {
        logger.debug("------------服务端发消息到客户端---");
    }

    @Override
    public void sessionClosed(IoSession session) {
        // TODO Auto-generated method stub
        logger.debug("远程session关闭了一个..." + session.getRemoteAddress().toString());
    }

    @Override
    public void sessionCreated(IoSession session) {
        logger.debug(session.getRemoteAddress().toString() + "----------------------create");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status){
        logger.debug(session.getServiceAddress() + "IDS");
    }

    @Override
    public void sessionOpened(IoSession session) {
        logger.debug("连接打开：" + session.getLocalAddress());
    }

    public static byte[] longToByte8(long lo) {
        byte[] targets = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((lo >>> offset) & 0xFF);
        }
        return targets;
    }

    private BaseMessageHandler getMessageHandler(Long messageType){
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

//心跳消息
//0X21,0x32,0x34,0xe4,0xc2,0xa1,0x01,0x04,0xf2,0xff,0x04,0xf4
//注册消息
//0X21,0x32,0x34,0xe4,0xc2,0xa1,0x01,0x04,0x01,0x01,0x01,0x01,0xf1,0xff,0x04,0xf4,0xff,0x04,0xf4,0xff,0x04,0xf4,0x01,0x00
//设备报告
//0x21,0x32,0x34,0xe4,0xc2,0xa1,0x01,0x04,0x00,0x00,0x00,0x01,0xB3,0xbb,0xaa,0xdd,0xcc, 0x1c,0x16,0x06,0x18,0x64,0x00,0x3f,0x02,0x01,0x32,0,0,0,0,0,0,0,0
//设备gps
//0x21,0x32,0x34,0xe4,0xc2,0xa1,0x01,0x04,0x00,0x00,0x00,0x01,0xB2,0x32,0x34,0x32, 0x36, 0x2E,0x30,0x30,0x30,0x30,0x4E,0x31,0x31,0x38,0x30,0x34,0x2E,0x30,0x30,0x30,0x30,0x45,0
}