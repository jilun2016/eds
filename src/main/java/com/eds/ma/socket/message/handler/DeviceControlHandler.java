package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.MongoDeviceControl;
import com.eds.ma.socket.message.MessageTypeConstants;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.eds.ma.socket.util.SocketMessageUtils;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.MILLISECOND;


/**
 * 设备控制消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component
public class DeviceControlHandler extends BaseMessageHandler {

    private static Logger logger = Logger.getLogger(DeviceControlHandler.class);

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Override
    public Long getMessageType() {
        return MessageTypeConstants.DEVICE_CONTROL;
    }

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //查询设备检测结果信息
        MongoDeviceControl mongoDeviceControl = parseControlMessage(commonHeadMessageVo, mesasge);
    }

    @Override
    public void sendDataMessage(CommonHeadMessageVo commonHeadMessageVo, String... mesasgeField) {

    }

    /**
     * 解析设备控制消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    private MongoDeviceControl parseControlMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge){
        MongoDeviceControl mongoDeviceControl = new MongoDeviceControl();
        mongoDeviceControl.setDeviceKind(commonHeadMessageVo.getDeviceKind());
        mongoDeviceControl.setDeviceCode(commonHeadMessageVo.getDeviceCode());
        mongoDeviceControl.setMessageNo(commonHeadMessageVo.getMessageNo());
        String checkCode = SocketMessageUtils.H2S(mesasge,13,4);
        mongoDeviceControl.setMessageCheckCode(checkCode);

        String allDeviceInfoMessage = SocketMessageUtils.H2B(mesasge[17]);
        Long deviceReturnStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,0,2);
        Long deviceElectricityStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,2,1);
        Long deviceIntakeValveStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,3,1);
        Long deviceTemperatureStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,4,1);
        Long deviceNTCStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,5,1);
        Long deviceReportStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,6,1);
        Long deviceUseStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,7,1);
        mongoDeviceControl.setDeviceUseStatus(deviceUseStatus);
        mongoDeviceControl.setDeviceReportStatus(deviceReportStatus);
        mongoDeviceControl.setDeviceNTCStatus(deviceNTCStatus);
        mongoDeviceControl.setDeviceTemperatureStatus(deviceTemperatureStatus);
        mongoDeviceControl.setDeviceIntakeValveStatus(deviceIntakeValveStatus);
        mongoDeviceControl.setDeviceElectricityStatus(deviceElectricityStatus);
        mongoDeviceControl.setDeviceReturnStatus(deviceReturnStatus);
        mongoDeviceControl.setDeviceElectricity(SocketMessageUtils.H2L(mesasge[18]));

        //解析返回消息的时间
        Calendar syncDate = Calendar.getInstance();
        syncDate.set(SocketMessageUtils.H2L(mesasge[19]).intValue()+2000,
                SocketMessageUtils.H2L(mesasge[20]).intValue() - 1,
                SocketMessageUtils.H2L(mesasge[21]).intValue(),
                SocketMessageUtils.H2L(mesasge[22]).intValue(),
                SocketMessageUtils.H2L(mesasge[23]).intValue(),0);
        syncDate.set(MILLISECOND,0);
        mongoDeviceControl.setMessageSyncDate(syncDate.getTime());

        Date now = DateFormatUtils.getNow();
        mongoDeviceControl.setCreated(now);
        mongoDeviceControl.setTimestamp(now.getTime());
        mongoTemplate.insert(mongoDeviceControl);
        return mongoDeviceControl;
    }
}