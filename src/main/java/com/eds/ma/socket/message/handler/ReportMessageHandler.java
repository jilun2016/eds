package com.eds.ma.socket.message.handler;

import com.eds.ma.bis.device.entity.Device;
import com.eds.ma.mongodb.collection.MongoDeviceReport;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 报告消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component
public class ReportMessageHandler extends BaseMessageHandler {


    private static Logger logger = Logger.getLogger(ReportMessageHandler.class);

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected BaseDaoSupport dao;

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //查询设备检测结果信息
        MongoDeviceReport mongoDeviceReport = parseReportMessage(commonHeadMessageVo, mesasge);
    }

    /**
     * 发送设备报告信息
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
        byte[] messageTypeBytes  =  SocketMessageUtils.L2Bytes(MessageTypeConstants.DEVICE_REPORT,1);
        byte[] checkBytes  =  SocketMessageUtils.L2Bytes(SocketConstants.REPORT_REQUEST_CHECK_CODE,4);
        long syncTimeYearValue = Calendar.getInstance().get(Calendar.YEAR ) - 2000;
        byte[] syncTimeYear = SocketMessageUtils.L2Bytes(syncTimeYearValue,1);
        long syncTimeMonthValue = Calendar.getInstance().get(Calendar.MONTH )+1;
        byte[] syncTimeMonth = SocketMessageUtils.L2Bytes( syncTimeMonthValue,1);
        long syncTimeDayValue = Calendar.getInstance().get(Calendar.DAY_OF_MONTH );
        byte[] syncTimeDay = SocketMessageUtils.L2Bytes(syncTimeDayValue,1);
        long syncTimeHourValue = Calendar.getInstance().get(Calendar.HOUR );
        byte[] syncTimeHour = SocketMessageUtils.L2Bytes(syncTimeHourValue,1);
        long syncTimeMiniuteValue = Calendar.getInstance().get(Calendar.MINUTE );
        byte[] syncTimeMiniute = SocketMessageUtils.L2Bytes(syncTimeMiniuteValue,1);
        byte[] reserveBytes = SocketMessageUtils.buildZeroBytes(12);

        byte[] checkByte = buildMessageCheckByte(
                commonHeadMessageVo.sum(),
                MessageTypeConstants.DEVICE_REPORT,
                SocketConstants.REPORT_REQUEST_CHECK_CODE,
                syncTimeYearValue,
                syncTimeMonthValue,
                syncTimeDayValue,
                syncTimeHourValue,
                syncTimeMiniuteValue);
        byte[] messageBytes = SocketMessageUtils.combineBytes(headBytes,messageTypeBytes,checkBytes,
                syncTimeYear,
                syncTimeMonth,
                syncTimeDay,
                syncTimeHour,
                syncTimeMiniute,
                reserveBytes,
                checkByte);
        SessionClient.sendMessage(commonHeadMessageVo.getDeviceCode(),messageBytes);
        //发送消息完成后,将消息编号保存到db中
        QueryBuilder updateQb = QueryBuilder.where(Restrictions.eq("deviceOriginCode",commonHeadMessageVo.getDeviceCode()))
                .and(Restrictions.eq("dataStatus",1));
        Device updateDevice = new Device();
        updateDevice.setDeviceReportNo(commonHeadMessageVo.getMessageNo());
        dao.updateByQuery(updateDevice,updateQb);
    }

    @Override
    public Long getMessageType() {
        return MessageTypeConstants.DEVICE_REPORT;
    }

    /**
     * 解析查询报告消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    private MongoDeviceReport parseReportMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge){
        MongoDeviceReport mongoDeviceReport = new MongoDeviceReport();
        mongoDeviceReport.setDeviceKind(commonHeadMessageVo.getDeviceKind());
        mongoDeviceReport.setDeviceCode(commonHeadMessageVo.getDeviceCode());
        mongoDeviceReport.setMessageNo(commonHeadMessageVo.getMessageNo());
        String checkCode = SocketMessageUtils.H2S(mesasge,13,4);
        mongoDeviceReport.setMessageCheckCode(checkCode);
        Long deviceTemperature = SocketMessageUtils.H2L(mesasge[17]);
        Long deviceInTemperature = SocketMessageUtils.H2L(mesasge[18]);
        Long HCHOHighValue = SocketMessageUtils.H2L(mesasge[19]);
        Long HCHOLowValue = SocketMessageUtils.H2L(mesasge[20]);
        Long HCHOValue = SocketMessageUtils.H2L(mesasge[21]);
        Long ASLHighValue = SocketMessageUtils.H2L(mesasge[22]);
        Long ASLLowValue = SocketMessageUtils.H2L(mesasge[23]);
        Long ASLDecimals = SocketMessageUtils.H2L(mesasge[24]);
        mongoDeviceReport.setDeviceTemperature(deviceTemperature);
        mongoDeviceReport.setDeviceInTemperature(deviceInTemperature);
        mongoDeviceReport.setHCHOHighValue(HCHOHighValue);
        mongoDeviceReport.setHCHOLowValue(HCHOLowValue);
        mongoDeviceReport.setHCHOValue(HCHOValue);
        mongoDeviceReport.setASLHighValue(ASLHighValue);
        mongoDeviceReport.setASLLowValue(ASLLowValue);
        mongoDeviceReport.setASLDecimals(ASLDecimals);

        String allDeviceInfoMessage = SocketMessageUtils.H2B(mesasge[25]);
        Long deviceReturnStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,0,2);
        Long deviceElectricityStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,2,1);
        Long deviceIntakeValveStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,3,1);
        Long deviceTemperatureStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,4,1);
        Long deviceNTCStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,5,1);
        Long deviceReportStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,6,1);
        Long deviceUseStatus = SocketMessageUtils.B2L(allDeviceInfoMessage,7,1);
        mongoDeviceReport.setDeviceUseStatus(deviceUseStatus);
        mongoDeviceReport.setDeviceReportStatus(deviceReportStatus);
        mongoDeviceReport.setDeviceNTCStatus(deviceNTCStatus);
        mongoDeviceReport.setDeviceTemperatureStatus(deviceTemperatureStatus);
        mongoDeviceReport.setDeviceIntakeValveStatus(deviceIntakeValveStatus);
        mongoDeviceReport.setDeviceElectricityStatus(deviceElectricityStatus);
        mongoDeviceReport.setDeviceReturnStatus(deviceReturnStatus);
        mongoDeviceReport.setDeviceElectricity(SocketMessageUtils.H2L(mesasge[26]));
        Date now = DateFormatUtils.getNow();
        mongoDeviceReport.setCreated(now);
        mongoDeviceReport.setTimestamp(now.getTime());
        mongoTemplate.insert(mongoDeviceReport);
        return mongoDeviceReport;
    }
}