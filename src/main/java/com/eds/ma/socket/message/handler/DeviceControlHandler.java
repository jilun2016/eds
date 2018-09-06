package com.eds.ma.socket.message.handler;

import com.eds.ma.bis.device.entity.Device;
import com.eds.ma.mongodb.collection.MongoDeviceControl;
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
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTimeUtils;
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

    @Autowired
    protected BaseDaoSupport dao;

    @Override
    public Long getMessageType() {
        return MessageTypeConstants.DEVICE_CONTROL;
    }

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //查询设备检测结果信息
        MongoDeviceControl mongoDeviceControl = parseControlMessage(commonHeadMessageVo, mesasge);
    }

    public static void main(String[] args) {
        byte[] syncTimeYear = SocketMessageUtils.L2Bytes(Calendar.getInstance().get(Calendar.YEAR ) -2000,1);
        byte[] syncTimeMonth = SocketMessageUtils.L2Bytes( Calendar.getInstance().get(Calendar.MONTH )+1,1);
        byte[] syncTimeDay = SocketMessageUtils.L2Bytes(Calendar.getInstance().get(Calendar.DAY_OF_MONTH ),1);
        byte[] syncTimeHour = SocketMessageUtils.L2Bytes(Calendar.getInstance().get(Calendar.HOUR ),1);
        byte[] syncTimeMiniute = SocketMessageUtils.L2Bytes(Calendar.getInstance().get(Calendar.MINUTE ),1);
    }

    /**
     * 发送设备控制信息
     * @param commonHeadMessageVo
     * @param mesasgeField [0]指令设备控制 0xa5|0x5a
     *                     [1]使用时长
     *                     [2]补偿数据A整数位
     *                     [3]补偿数据A小数位
     *                     [4]补偿数据A小数位
     *                     [5]补偿数据B整数位
     *                     [6]补偿数据B小数位
     *                     [7]补偿数据B小数位
     */
    @Override
    public void sendDataMessage(CommonHeadMessageVo commonHeadMessageVo, Long... mesasgeField) {
        byte[] headBytes = commonHeadMessageVo.toBytes();
        byte[] messageTypeBytes  =  SocketMessageUtils.L2Bytes(MessageTypeConstants.DEVICE_CONTROL,1);
        byte[] checkBytes  =  SocketMessageUtils.L2Bytes(SocketConstants.CONTROL_REQUEST_CHECK_CODE,4);
        byte[] controlStatusBytes = SocketMessageUtils.L2Bytes(mesasgeField[0],1);
        byte[] syncTimeYear = SocketMessageUtils.L2Bytes(Calendar.getInstance().get(Calendar.YEAR ) - 2000,1);
        byte[] syncTimeMonth = SocketMessageUtils.L2Bytes( Calendar.getInstance().get(Calendar.MONTH )+1,1);
        byte[] syncTimeDay = SocketMessageUtils.L2Bytes(Calendar.getInstance().get(Calendar.DAY_OF_MONTH ),1);
        byte[] syncTimeHour = SocketMessageUtils.L2Bytes(Calendar.getInstance().get(Calendar.HOUR ),1);
        byte[] syncTimeMiniute = SocketMessageUtils.L2Bytes(Calendar.getInstance().get(Calendar.MINUTE ),1);
        byte[] useTime = SocketMessageUtils.L2Bytes(mesasgeField[1],1);

        byte[] adjustParamAIntegerByte = SocketMessageUtils.L2Bytes(mesasgeField[2],1);
        byte[] adjustParamA1DecByte = SocketMessageUtils.L2Bytes(mesasgeField[3],1);
        byte[] adjustParamA2DecByte = SocketMessageUtils.L2Bytes(mesasgeField[4],1);

        byte[] adjustParamBIntegerByte = SocketMessageUtils.L2Bytes(mesasgeField[5],1);
        byte[] adjustParamB1DecByte = SocketMessageUtils.L2Bytes(mesasgeField[6],1);
        byte[] adjustParamB2DecByte = SocketMessageUtils.L2Bytes(mesasgeField[7],1);
        byte[] reserveBytes = SocketMessageUtils.buildZeroBytes(4);

        byte[] checkByte = buildMessageCheckByte(commonHeadMessageVo.sum(),MessageTypeConstants.DEVICE_GPS,SocketConstants.GPS_REQUEST_CHECK_CODE);
        byte[] messageBytes = SocketMessageUtils.combineBytes(headBytes,messageTypeBytes,checkBytes,
                controlStatusBytes,
                syncTimeYear,
                syncTimeMonth,
                syncTimeDay,
                syncTimeHour,
                syncTimeMiniute,
                useTime,
                adjustParamAIntegerByte,
                adjustParamA1DecByte,
                adjustParamA2DecByte,
                adjustParamBIntegerByte,
                adjustParamB1DecByte,
                adjustParamB2DecByte,
                reserveBytes,
                checkByte);
        SessionClient.sendMessage(commonHeadMessageVo.getDeviceCode(),messageBytes);
        //发送消息完成后,将消息编号保存到db中
        QueryBuilder updateQb = QueryBuilder.where(Restrictions.eq("deviceOriginCode",commonHeadMessageVo.getDeviceCode()))
                .and(Restrictions.eq("dataStatus",1));
        Device updateDevice = new Device();
        updateDevice.setDeviceControlNo(commonHeadMessageVo.getMessageNo());
        dao.updateByQuery(updateDevice,updateQb);
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