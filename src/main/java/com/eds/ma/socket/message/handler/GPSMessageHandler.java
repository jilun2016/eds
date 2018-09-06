package com.eds.ma.socket.message.handler;

import com.eds.ma.bis.device.entity.Device;
import com.eds.ma.mongodb.collection.MongoDeviceGPS;
import com.eds.ma.socket.SessionClient;
import com.eds.ma.socket.SocketConstants;
import com.eds.ma.socket.message.MessageTypeConstants;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.eds.ma.socket.util.SocketMessageUtils;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * 位置消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component(value = "gpsMessageHandler")
public class GPSMessageHandler extends BaseMessageHandler {

    private static Logger logger = Logger.getLogger(GPSMessageHandler.class);

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected BaseDaoSupport dao;

    @Override
    public Long getMessageType() {
        return MessageTypeConstants.DEVICE_GPS;
    }

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //解析位置消息
        MongoDeviceGPS mongoDeviceGPS = parseGPSMessage(commonHeadMessageVo, mesasge);
    }

    /**
     * mesasgeField 定义为无效
     * @param commonHeadMessageVo
     * @param mesasgeField
     */
    @Override
    public void sendDataMessage(CommonHeadMessageVo commonHeadMessageVo, Long... mesasgeField) {
        byte[] headBytes = commonHeadMessageVo.toBytes();
        byte[] messageTypeBytes  =  SocketMessageUtils.L2Bytes(MessageTypeConstants.DEVICE_GPS,1);
        byte[] checkBytes  =  SocketMessageUtils.L2Bytes(SocketConstants.GPS_REQUEST_CHECK_CODE,4);
        byte[] reserveBytes = SocketMessageUtils.buildZeroBytes(17);
        byte[] checkByte = buildMessageCheckByte(commonHeadMessageVo.sum(),MessageTypeConstants.DEVICE_GPS,SocketConstants.GPS_REQUEST_CHECK_CODE);
        byte[] messageBytes = SocketMessageUtils.combineBytes(headBytes,messageTypeBytes,checkBytes,reserveBytes,checkByte);
        SessionClient.sendMessage(commonHeadMessageVo.getDeviceCode(),messageBytes);
        //发送消息完成后,将消息编号保存到db中
        QueryBuilder updateQb = QueryBuilder.where(Restrictions.eq("deviceOriginCode",commonHeadMessageVo.getDeviceCode()))
                .and(Restrictions.eq("dataStatus",1));
        Device updateDevice = new Device();
        updateDevice.setDeviceGpsNo(commonHeadMessageVo.getMessageNo());
        dao.updateByQuery(updateDevice,updateQb);
    }

    /**
     * 解析查询报告消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    private MongoDeviceGPS parseGPSMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge){
        MongoDeviceGPS mongoDeviceGPS = new MongoDeviceGPS();
        mongoDeviceGPS.setDeviceKind(commonHeadMessageVo.getDeviceKind());
        mongoDeviceGPS.setDeviceCode(commonHeadMessageVo.getDeviceCode());
        mongoDeviceGPS.setMessageNo(commonHeadMessageVo.getMessageNo());
        //解析经纬度
        //解析纬度
        StringBuilder deviceLatSb = new StringBuilder();
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[13]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[14]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[15]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[16]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[17]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[18]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[19]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[20]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[21]));
        String deviceLatDesc = SocketMessageUtils.H2C(mesasge[22]);
        mongoDeviceGPS.setDeviceLat(deviceLatSb.toString());
        mongoDeviceGPS.setDeviceLatDesc(deviceLatDesc);

        //解析经度
        StringBuilder deviceLngSb = new StringBuilder();
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[23]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[24]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[25]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[26]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[27]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[28]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[29]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[30]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[31]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[32]));
        String deviceLngDesc = SocketMessageUtils.H2C(mesasge[33]);
        mongoDeviceGPS.setDeviceLng(deviceLngSb.toString());
        mongoDeviceGPS.setDeviceLngDesc(deviceLngDesc);

        Date now = DateFormatUtils.getNow();
        mongoDeviceGPS.setCreated(now);
        mongoDeviceGPS.setTimestamp(now.getTime());
        mongoTemplate.insert(mongoDeviceGPS);
        return mongoDeviceGPS;
    }
}