package com.eds.ma.socket.handler;

import com.eds.ma.mongodb.MongoDbDaoSupport;
import com.eds.ma.socket.DeviceCodeConstant;
import com.eds.ma.socket.SessionMap;
import com.eds.ma.socket.SocketConstants;
import com.eds.ma.socket.test.ByteAndStr16;
import com.eds.ma.socket.vo.DeviceDataVo;
import com.eds.ma.util.NumberUtil;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;

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
    private MongoDbDaoSupport mongoDbDaoSupport;

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
        System.out.println("收到消息：" + bbuf.getHexDump());
        String[] mesasgeArray = bbuf.getHexDump().split(" ");
        if((mesasgeArray.length >= 22)
                && (Objects.equals(Long.parseLong(mesasgeArray[0],16) , DeviceCodeConstant.DEVICE_ORDER_START_FLAG))
                && (Objects.equals(Long.parseLong(mesasgeArray[21],16) , DeviceCodeConstant.DEVICE_ORDER_END_FLAG))){
            Long deviceType = Long.parseLong(mesasgeArray[2],16);
            Long deviceLength = Long.parseLong(mesasgeArray[1],16);
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
            mongoDbDaoSupport.save(deviceDataVo);
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