package com.eds.ma.socket.message.handler;

import com.eds.ma.socket.SessionMap;
import com.xcrm.log.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;


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
        messageHandler.processMessage(mesasgeArray);

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