package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.MongoDeviceHeartBeat;
import com.eds.ma.mongodb.collection.MongoDeviceHeartBeatRecord;
import com.eds.ma.socket.message.MessageTypeConstants;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.eds.ma.socket.util.SocketMessageUtils;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.eds.ma.socket.util.SocketMessageUtils.B2L;


/**
 * 心跳消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */

@Component
public class HeartBeatMessageHandler extends BaseMessageHandler {

    private static Logger logger = Logger.getLogger(HeartBeatMessageHandler.class);

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Override
    public Long getMessageType() {
        return MessageTypeConstants.DEVICE_HEARTBEAT;
    }

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //解析心跳消息
        MongoDeviceHeartBeatRecord mongoDeviceHeartBeat = parseHeartBeatMessage(commonHeadMessageVo, mesasge);
    }

    @Override
    public void sendDataMessage(CommonHeadMessageVo commonHeadMessageVo, Long... mesasgeField) {

    }


    /**
     * 解析心跳消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    public MongoDeviceHeartBeatRecord parseHeartBeatMessage(CommonHeadMessageVo commonHeadMessageVo,String[] mesasge){
        String allDeviceInfoMessage = SocketMessageUtils.H2B(mesasge[9]);
        Long deviceReturnStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,0,2);
        Long deviceElectricityStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,2,1);
        Long deviceIntakeValveStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,3,1);
        Long deviceTemperatureStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,4,1);
        Long deviceNTCStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,5,1);
        Long deviceReportStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,6,1);
        Long deviceUseStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,7,1);
        MongoDeviceHeartBeatRecord mongoDeviceHeartBeat = new MongoDeviceHeartBeatRecord();
        mongoDeviceHeartBeat.setDeviceKind(commonHeadMessageVo.getDeviceKind());
        mongoDeviceHeartBeat.setDeviceCode(commonHeadMessageVo.getDeviceCode());
        mongoDeviceHeartBeat.setDeviceUseStatus(deviceUseStatus);
        mongoDeviceHeartBeat.setDeviceReportStatus(deviceReportStatus);
        mongoDeviceHeartBeat.setDeviceNTCStatus(deviceNTCStatus);
        mongoDeviceHeartBeat.setDeviceTemperatureStatus(deviceTemperatureStatus);
        mongoDeviceHeartBeat.setDeviceIntakeValveStatus(deviceIntakeValveStatus);
        mongoDeviceHeartBeat.setDeviceElectricityStatus(deviceElectricityStatus);
        mongoDeviceHeartBeat.setDeviceReturnStatus(deviceReturnStatus);
        mongoDeviceHeartBeat.setDeviceSignalStrength(commonHeadMessageVo.getDeviceSignalStrength());
        Date now = DateFormatUtils.getNow();
        mongoDeviceHeartBeat.setCreated(now);
        mongoDeviceHeartBeat.setTimestamp(now.getTime());
        mongoTemplate.insert(mongoDeviceHeartBeat);

        Query query = new Query(Criteria.where("deviceCode").is(commonHeadMessageVo.getDeviceCode()));
        Update update = mongoDeviceHeartBeat.toMongoUpdate();
        mongoTemplate.findAndModify(query,update,MongoDeviceHeartBeat.class);
        return mongoDeviceHeartBeat;
    }
}