package com.eds.ma.socket.handler;

import com.eds.ma.socket.DeviceCodeConstant;
import com.eds.ma.socket.SessionMap;
import com.eds.ma.socket.test.ByteAndStr16;
import com.eds.ma.util.NumberUtil;
import com.xcrm.log.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.net.InetSocketAddress;
import java.util.Objects;


/**
 * @author whl
 * @Description: mina服务端业务处理类
 * @date 2014-9-30 下午12:36:28
 */
public class ServerHandler extends IoHandlerAdapter {


    private static Logger logger = Logger.getLogger(ServerHandler.class);


    public ServerHandler() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
    }


    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        logger.debug("messageReceived({})", message);

        //获取客户端发过来的key
        IoBuffer bbuf = (IoBuffer) message;
        System.out.println("收到消息：" + bbuf.getHexDump());
        String[] mesasgeArray = bbuf.getHexDump().split(" ");
        if((mesasgeArray.length >= 22)
                && (Objects.equals(Long.parseLong(mesasgeArray[0],16) , DeviceCodeConstant.DEVICE_ORDER_START_FLAG))
                && (Objects.equals(Long.parseLong(mesasgeArray[21],16) , DeviceCodeConstant.DEVICE_ORDER_END_FLAG))){
            Long deviceOriginId = Long.parseLong(mesasgeArray[3]+mesasgeArray[4]+mesasgeArray[5]+mesasgeArray[6],16);
            Long deviceLng = Long.parseLong(mesasgeArray[12]+mesasgeArray[13]+mesasgeArray[14]+mesasgeArray[15],16);;
            Long deviceLat = Long.parseLong(mesasgeArray[16]+mesasgeArray[17]+mesasgeArray[18]+mesasgeArray[19],16);;
            Long deviceCode = Long.parseLong(mesasgeArray[7],16);
            //仪表状态 归位未借 1
            if(Objects.equals(deviceCode,DeviceCodeConstant.DEVICE_STATUS_DZJ)){

            }

            //仪表状态 租借中 2
            if(Objects.equals(deviceCode,DeviceCodeConstant.DEVICE_STATUS_SYZ)){

            }

            //仪表状态 已锁定 3
            if(Objects.equals(deviceCode,DeviceCodeConstant.DEVICE_STATUS_YSD)){

            }

            //仪表状态 测量中 4
            if(Objects.equals(deviceCode,DeviceCodeConstant.DEVICE_STATUS_CLZ)){

            }

            //仪表状态 本次测量报告 5
            if(Objects.equals(deviceCode,DeviceCodeConstant.DEVICE_STATUS_REPORT)){
                Long deviceReport = Long.parseLong(mesasgeArray[10]+mesasgeArray[11],16);
            }



            //保存客户端的会话session
            SessionMap sessionMap = SessionMap.newInstance();
            sessionMap.addSession(String.valueOf(((InetSocketAddress) session.getLocalAddress()).getPort()), session);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        logger.debug("------------服务端发消息到客户端---");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        logger.debug("远程session关闭了一个..." + session.getRemoteAddress().toString());
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.debug(session.getRemoteAddress().toString() + "----------------------create");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        logger.debug(session.getServiceAddress() + "IDS");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        logger.debug("连接打开：" + session.getLocalAddress());
    }

    public static void main(String[] args) {
        byte[] bytes = new byte[4];
        bytes[0] = 0;
        bytes[1] = -68;
        bytes[2] = 97;
        bytes[3] = 78;
        NumberUtil.bytes2int(bytes);

    }

}