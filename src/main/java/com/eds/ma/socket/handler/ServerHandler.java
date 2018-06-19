package com.eds.ma.socket.handler;

import com.eds.ma.bis.device.service.IDeviceService;
import com.eds.ma.mongodb.MongoDbDaoSupport;
import com.eds.ma.socket.DeviceCodeConstant;
import com.eds.ma.socket.SessionMap;
import com.eds.ma.socket.SocketConstants;
import com.eds.ma.socket.vo.DeviceDataVo;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;
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
    private IDeviceService deviceService;

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
        if((mesasgeArray.length >= 22)
                && (Objects.equals(Long.parseLong(mesasgeArray[0],16) , DeviceCodeConstant.DEVICE_ORDER_START_FLAG))
                && (Objects.equals(Long.parseLong(mesasgeArray[21],16) , DeviceCodeConstant.DEVICE_ORDER_END_FLAG))){
            Long deviceType = Long.parseLong(mesasgeArray[1],16);
            Long deviceLength = Long.parseLong(mesasgeArray[2],16);
            Long deviceId = Long.parseLong(mesasgeArray[3]+mesasgeArray[4]+mesasgeArray[5]+mesasgeArray[6],16);
            Long deviceCode = Long.parseLong(mesasgeArray[7],16);
            Long deviceElectricity = Long.parseLong(mesasgeArray[8],16);
            Long deviceTemperature = Long.parseLong(mesasgeArray[9],16);
            Long deviceReport = Long.parseLong(mesasgeArray[10]+mesasgeArray[11],16);
            Long deviceLng = Long.parseLong(mesasgeArray[12]+mesasgeArray[13]+mesasgeArray[14]+mesasgeArray[15],16);
            Long deviceLat = Long.parseLong(mesasgeArray[16]+mesasgeArray[17]+mesasgeArray[18]+mesasgeArray[19],16);
            DeviceDataVo deviceDataVo = new DeviceDataVo();
            deviceDataVo.setCreated(DateFormatUtils.getNow());
            deviceDataVo.setDeviceFlowType(SocketConstants.DEVICE_FLOW_CLIENT_UPLOAD);
            deviceDataVo.setDeviceType(deviceType);
            deviceDataVo.setDeviceLength(deviceLength);
            deviceDataVo.setDeviceId(deviceId);
            deviceDataVo.setDeviceCode(deviceCode);
            deviceDataVo.setDeviceElectricity(deviceElectricity);
            deviceDataVo.setDeviceTemperature(deviceTemperature);
            deviceDataVo.setDeviceReport(BigDecimal.valueOf(deviceReport).divide(BigDecimal.valueOf(100),2,BigDecimal.ROUND_HALF_UP));
            deviceDataVo.setDeviceLng(BigDecimal.valueOf(deviceLng).divide(BigDecimal.valueOf(10000),2,BigDecimal.ROUND_HALF_UP));
            deviceDataVo.setDeviceLat(BigDecimal.valueOf(deviceLat).divide(BigDecimal.valueOf(10000),2,BigDecimal.ROUND_HALF_UP));
            deviceService.asyncSaveMessage(deviceDataVo);
            //进行消息反馈
            byte[] temp = new byte[10];
            temp[0] =  18;
            temp[1] =  1;
            temp[2] =  8;
            temp[3] = (byte) Integer.valueOf(mesasgeArray[3]).intValue();
            temp[4] = (byte) Integer.valueOf(mesasgeArray[4]).intValue();
            temp[5] = (byte) Integer.valueOf(mesasgeArray[5]).intValue();
            temp[6] = (byte) Integer.valueOf(mesasgeArray[6]).intValue();
            temp[7] =  1;
            //对字节求和
            Long byteSum =  Long.parseLong(String.valueOf(temp[1]),16);
            byteSum += Long.parseLong(String.valueOf(temp[2]),16);
            byteSum += Long.parseLong(String.valueOf(temp[3])+String.valueOf(temp[4])+String.valueOf(temp[5])+String.valueOf(temp[6]),16);
            byteSum += Long.parseLong(String.valueOf(temp[7]),16);
            String binaryValue = Long.toBinaryString(byteSum);
            //取最后八位作为校验码字节
            String checkTenValueStr = binaryValue.substring(binaryValue.length() - 8);
            String checkHexValue = Integer.toString (Integer.parseInt (checkTenValueStr, 2), 16);
            temp[8] = (byte) Integer.valueOf(checkHexValue).intValue();
            temp[9] =  26;
            IoBuffer buffer = IoBuffer.allocate(10);
            // 自动扩容
            buffer.setAutoExpand(true);
            // 自动收缩
            buffer.setAutoShrink(true);
            buffer.put(temp);
            buffer.flip();
            logger.info("发送消息：" + buffer.getHexDump());
            session.write(buffer);
            //保存客户端的会话session
            SessionMap sessionMap = SessionMap.newInstance();
            sessionMap.addSession(String.valueOf(((InetSocketAddress) session.getLocalAddress()).getPort()), session);
        }
        //仪表锁定/解锁处理
        if((mesasgeArray.length >= 10)
                && (Objects.equals(Long.parseLong(mesasgeArray[0],16) , DeviceCodeConstant.DEVICE_ORDER_START_FLAG))
                && (Objects.equals(Long.parseLong(mesasgeArray[9],16) , DeviceCodeConstant.DEVICE_ORDER_END_FLAG))){
            Long deviceType = Long.parseLong(mesasgeArray[1],16);
            Long deviceLength = Long.parseLong(mesasgeArray[2],16);
            Long deviceId = Long.parseLong(mesasgeArray[3]+mesasgeArray[4]+mesasgeArray[5]+mesasgeArray[6],16);
            Long deviceCode = Long.parseLong(mesasgeArray[7],16);
            DeviceDataVo deviceDataVo = new DeviceDataVo();
            deviceDataVo.setCreated(DateFormatUtils.getNow());
            deviceDataVo.setDeviceFlowType(SocketConstants.DEVICE_FLOW_CLIENT_UPLOAD);
            deviceDataVo.setDeviceType(deviceType);
            deviceDataVo.setDeviceLength(deviceLength);
            deviceDataVo.setDeviceId(deviceId);
            deviceDataVo.setDeviceCode(deviceCode);
            deviceService.asyncSaveMessage(deviceDataVo);
            deviceService.asyncUpdateDeviceStatus(deviceId,deviceType.intValue());
            //保存客户端的会话session
            SessionMap sessionMap = SessionMap.newInstance();
            sessionMap.addSession(String.valueOf(((InetSocketAddress) session.getLocalAddress()).getPort()), session);
        }
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