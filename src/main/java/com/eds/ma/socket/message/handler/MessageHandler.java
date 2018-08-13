package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.*;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.MILLISECOND;


/**
 * 基础消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component
public class MessageHandler {

    private static Logger logger = Logger.getLogger(MessageHandler.class);

    @Autowired
    protected MongoTemplate mongoTemplate;

    //解析消息头信息
    public CommonHeadMessageVo parseHeadMessage(String[] mesasge){
        logger.debug("parseHeadMessage({})", mesasge);
        Integer messageLength = mesasge.length;
        //解析消息,不同消息类型不同处理
        //设备注册上报命令：24个字节,()
        if(messageLength == 24){
            return parseCommonHeadMessage(mesasge);
        }

        //设备每1分钟主动发，心跳包：12个字节
        if(messageLength == 12){
            return parseHeartBeatHeadMessage(mesasge);
        }

        //查询设备检测结果信息：长度35字节
        if(messageLength == 35){
            return parseCommonHeadMessage(mesasge);
        }

        //设备历史上传消息：长度44字节
        if(messageLength == 44){
            return parseHistoryHeadMessage(mesasge);
        }

       return null;
    }

    /**
     * 解析心跳消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    public MongoDeviceHeartBeat parseHeartBeatMessage(CommonHeadMessageVo commonHeadMessageVo,String[] mesasge){
        String allDeviceInfoMessage = H2B(mesasge[9]);
        Long deviceReturnStatus = B2L(allDeviceInfoMessage,0,2);
        Long deviceElectricityStatus = B2L(allDeviceInfoMessage,2,1);
        Long deviceIntakeValveStatus = B2L(allDeviceInfoMessage,3,1);
        Long deviceTemperatureStatus = B2L(allDeviceInfoMessage,4,1);
        Long deviceNTCStatus = B2L(allDeviceInfoMessage,5,1);
        Long deviceReportStatus = B2L(allDeviceInfoMessage,6,1);
        Long deviceUseStatus = B2L(allDeviceInfoMessage,7,1);
        MongoDeviceHeartBeat mongoDeviceHeartBeat = new MongoDeviceHeartBeat();
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
        return mongoDeviceHeartBeat;
    }

    /**
     * 解析查询报告消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    public MongoDeviceReport parseReportMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge){
        MongoDeviceReport mongoDeviceReport = new MongoDeviceReport();
        mongoDeviceReport.setDeviceKind(commonHeadMessageVo.getDeviceKind());
        mongoDeviceReport.setDeviceCode(commonHeadMessageVo.getDeviceCode());
        mongoDeviceReport.setMessageNo(commonHeadMessageVo.getMessageNo());
        String checkCode = H2S(mesasge,13,4);
        mongoDeviceReport.setMessageCheckCode(checkCode);
        Long deviceTemperature = H2L(mesasge[17]);
        Long deviceInTemperature = H2L(mesasge[18]);
        Long HCHOHighValue = H2L(mesasge[19]);
        Long HCHOLowValue = H2L(mesasge[20]);
        Long HCHOValue = H2L(mesasge[21]);
        Long ASLHighValue = H2L(mesasge[22]);
        Long ASLLowValue = H2L(mesasge[23]);
        Long ASLDecimals = H2L(mesasge[24]);
        mongoDeviceReport.setDeviceTemperature(deviceTemperature);
        mongoDeviceReport.setDeviceInTemperature(deviceInTemperature);
        mongoDeviceReport.setHCHOHighValue(HCHOHighValue);
        mongoDeviceReport.setHCHOLowValue(HCHOLowValue);
        mongoDeviceReport.setHCHOValue(HCHOValue);
        mongoDeviceReport.setASLHighValue(ASLHighValue);
        mongoDeviceReport.setASLLowValue(ASLLowValue);
        mongoDeviceReport.setASLDecimals(ASLDecimals);

        String allDeviceInfoMessage = H2B(mesasge[25]);
        Long deviceReturnStatus = B2L(allDeviceInfoMessage,0,2);
        Long deviceElectricityStatus = B2L(allDeviceInfoMessage,2,1);
        Long deviceIntakeValveStatus = B2L(allDeviceInfoMessage,3,1);
        Long deviceTemperatureStatus = B2L(allDeviceInfoMessage,4,1);
        Long deviceNTCStatus = B2L(allDeviceInfoMessage,5,1);
        Long deviceReportStatus = B2L(allDeviceInfoMessage,6,1);
        Long deviceUseStatus = B2L(allDeviceInfoMessage,7,1);
        mongoDeviceReport.setDeviceUseStatus(deviceUseStatus);
        mongoDeviceReport.setDeviceReportStatus(deviceReportStatus);
        mongoDeviceReport.setDeviceNTCStatus(deviceNTCStatus);
        mongoDeviceReport.setDeviceTemperatureStatus(deviceTemperatureStatus);
        mongoDeviceReport.setDeviceIntakeValveStatus(deviceIntakeValveStatus);
        mongoDeviceReport.setDeviceElectricityStatus(deviceElectricityStatus);
        mongoDeviceReport.setDeviceReturnStatus(deviceReturnStatus);
        mongoDeviceReport.setDeviceElectricity(H2L(mesasge[26]));
        Date now = DateFormatUtils.getNow();
        mongoDeviceReport.setCreated(now);
        mongoDeviceReport.setTimestamp(now.getTime());
        mongoTemplate.insert(mongoDeviceReport);
        return mongoDeviceReport;
    }

    /**
     * 解析设备控制消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    public MongoDeviceControl parseControlMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge){
        MongoDeviceControl mongoDeviceControl = new MongoDeviceControl();
        mongoDeviceControl.setDeviceKind(commonHeadMessageVo.getDeviceKind());
        mongoDeviceControl.setDeviceCode(commonHeadMessageVo.getDeviceCode());
        mongoDeviceControl.setMessageNo(commonHeadMessageVo.getMessageNo());
        String checkCode = H2S(mesasge,13,4);
        mongoDeviceControl.setMessageCheckCode(checkCode);

        String allDeviceInfoMessage = H2B(mesasge[17]);
        Long deviceReturnStatus = B2L(allDeviceInfoMessage,0,2);
        Long deviceElectricityStatus = B2L(allDeviceInfoMessage,2,1);
        Long deviceIntakeValveStatus = B2L(allDeviceInfoMessage,3,1);
        Long deviceTemperatureStatus = B2L(allDeviceInfoMessage,4,1);
        Long deviceNTCStatus = B2L(allDeviceInfoMessage,5,1);
        Long deviceReportStatus = B2L(allDeviceInfoMessage,6,1);
        Long deviceUseStatus = B2L(allDeviceInfoMessage,7,1);
        mongoDeviceControl.setDeviceUseStatus(deviceUseStatus);
        mongoDeviceControl.setDeviceReportStatus(deviceReportStatus);
        mongoDeviceControl.setDeviceNTCStatus(deviceNTCStatus);
        mongoDeviceControl.setDeviceTemperatureStatus(deviceTemperatureStatus);
        mongoDeviceControl.setDeviceIntakeValveStatus(deviceIntakeValveStatus);
        mongoDeviceControl.setDeviceElectricityStatus(deviceElectricityStatus);
        mongoDeviceControl.setDeviceReturnStatus(deviceReturnStatus);
        mongoDeviceControl.setDeviceElectricity(H2L(mesasge[18]));

        //解析返回消息的时间
        Calendar syncDate = Calendar.getInstance();
        syncDate.set(H2L(mesasge[19]).intValue()+2000,
                H2L(mesasge[20]).intValue() - 1,
                H2L(mesasge[21]).intValue(),
                H2L(mesasge[22]).intValue(),
                H2L(mesasge[23]).intValue(),0);
        syncDate.set(MILLISECOND,0);
        mongoDeviceControl.setMessageSyncDate(syncDate.getTime());

        Date now = DateFormatUtils.getNow();
        mongoDeviceControl.setCreated(now);
        mongoDeviceControl.setTimestamp(now.getTime());
        mongoTemplate.insert(mongoDeviceControl);
        return mongoDeviceControl;
    }

    /**
     * 解析历史上传消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    public MongoDeviceReport parseHistoryMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge){
        MongoDeviceReport mongoDeviceReport = new MongoDeviceReport();
        mongoDeviceReport.setDeviceKind(commonHeadMessageVo.getDeviceKind());
        mongoDeviceReport.setDeviceCode(commonHeadMessageVo.getDeviceCode());

        //解析检测数据的时间
        Calendar syncDate = Calendar.getInstance();
        syncDate.set(H2L(mesasge[9]).intValue()+2000,
                H2L(mesasge[10]).intValue() - 1,
                H2L(mesasge[11]).intValue(),
                H2L(mesasge[12]).intValue(),
                H2L(mesasge[13]).intValue(),0);
        syncDate.set(MILLISECOND,0);



        //解析经纬度
        //解析纬度
        StringBuilder deviceLatSb = new StringBuilder();
        deviceLatSb.append(H2C(mesasge[14]));
        deviceLatSb.append(H2C(mesasge[15]));
        deviceLatSb.append(H2C(mesasge[16]));
        deviceLatSb.append(H2C(mesasge[17]));
        deviceLatSb.append(H2C(mesasge[18]));
        deviceLatSb.append(H2C(mesasge[19]));
        deviceLatSb.append(H2C(mesasge[20]));
        deviceLatSb.append(H2C(mesasge[21]));
        deviceLatSb.append(H2C(mesasge[22]));
        String deviceLatDesc = H2C(mesasge[23]);

        //解析经度
        StringBuilder deviceLngSb = new StringBuilder();
        deviceLngSb.append(H2C(mesasge[24]));
        deviceLngSb.append(H2C(mesasge[25]));
        deviceLngSb.append(H2C(mesasge[26]));
        deviceLngSb.append(H2C(mesasge[27]));
        deviceLngSb.append(H2C(mesasge[28]));
        deviceLngSb.append(H2C(mesasge[29]));
        deviceLngSb.append(H2C(mesasge[30]));
        deviceLngSb.append(H2C(mesasge[31]));
        deviceLngSb.append(H2C(mesasge[32]));
        deviceLngSb.append(H2C(mesasge[33]));
        String deviceLngDesc = H2C(mesasge[34]);


        mongoDeviceReport.setCreated(syncDate.getTime());

        Long ASLHighValue = H2L(mesasge[35]);
        Long ASLLowValue = H2L(mesasge[36]);
        Long ASLDecimals = H2L(mesasge[37]);

        Long deviceTemperature = H2L(mesasge[38]);
        Long deviceInTemperature = H2L(mesasge[39]);


        Long HCHOHighValue = H2L(mesasge[40]);
        Long HCHOLowValue = H2L(mesasge[41]);
        Long HCHOValue = H2L(mesasge[42]);


        return mongoDeviceReport;
    }

    /**
     * 解析注册消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    public MongoDeviceRegister parseRegisterMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge){
        String deviceICCID = H2S(mesasge,13,10);
        MongoDeviceRegister mongoDeviceRegister = new MongoDeviceRegister();
        mongoDeviceRegister.setDeviceKind(commonHeadMessageVo.getDeviceKind());
        mongoDeviceRegister.setDeviceCode(commonHeadMessageVo.getDeviceCode());
        mongoDeviceRegister.setMessageNo(commonHeadMessageVo.getMessageNo());
        mongoDeviceRegister.setDeviceICCID(deviceICCID);
        Date now = DateFormatUtils.getNow();
        mongoDeviceRegister.setCreated(now);
        mongoDeviceRegister.setTimestamp(now.getTime());
        mongoTemplate.insert(mongoDeviceRegister);
        return mongoDeviceRegister;
    }

    /**
     * 解析查询报告消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    public MongoDeviceGPS parseGPSMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge){
        MongoDeviceGPS mongoDeviceGPS = new MongoDeviceGPS();
        mongoDeviceGPS.setDeviceKind(commonHeadMessageVo.getDeviceKind());
        mongoDeviceGPS.setDeviceCode(commonHeadMessageVo.getDeviceCode());
        mongoDeviceGPS.setMessageNo(commonHeadMessageVo.getMessageNo());
        //解析经纬度
        //解析纬度
        StringBuilder deviceLatSb = new StringBuilder();
        deviceLatSb.append(H2C(mesasge[13]));
        deviceLatSb.append(H2C(mesasge[14]));
        deviceLatSb.append(H2C(mesasge[15]));
        deviceLatSb.append(H2C(mesasge[16]));
        deviceLatSb.append(H2C(mesasge[17]));
        deviceLatSb.append(H2C(mesasge[18]));
        deviceLatSb.append(H2C(mesasge[19]));
        deviceLatSb.append(H2C(mesasge[20]));
        deviceLatSb.append(H2C(mesasge[21]));
        String deviceLatDesc = H2C(mesasge[22]);
        mongoDeviceGPS.setDeviceLat(deviceLatSb.toString());
        mongoDeviceGPS.setDeviceLatDesc(deviceLatDesc);

        //解析经度
        StringBuilder deviceLngSb = new StringBuilder();
        deviceLngSb.append(H2C(mesasge[23]));
        deviceLngSb.append(H2C(mesasge[24]));
        deviceLngSb.append(H2C(mesasge[25]));
        deviceLngSb.append(H2C(mesasge[26]));
        deviceLngSb.append(H2C(mesasge[27]));
        deviceLngSb.append(H2C(mesasge[28]));
        deviceLngSb.append(H2C(mesasge[29]));
        deviceLngSb.append(H2C(mesasge[30]));
        deviceLngSb.append(H2C(mesasge[31]));
        deviceLngSb.append(H2C(mesasge[32]));
        String deviceLngDesc = H2C(mesasge[33]);
        mongoDeviceGPS.setDeviceLng(deviceLngSb.toString());
        mongoDeviceGPS.setDeviceLngDesc(deviceLngDesc);

        Date now = DateFormatUtils.getNow();
        mongoDeviceGPS.setCreated(now);
        mongoDeviceGPS.setTimestamp(now.getTime());
        mongoTemplate.insert(mongoDeviceGPS);
        return mongoDeviceGPS;
    }

    private CommonHeadMessageVo parseCommonHeadMessage(String[] mesasge){
        logger.debug("parseCommonHeadMessage({})", mesasge);
        CommonHeadMessageVo commonHeadMessageVo = new CommonHeadMessageVo();
        //解析设备种类
        commonHeadMessageVo.setDeviceKind(H2L(mesasge[0]));
        //解析设备编号
        commonHeadMessageVo.setDeviceCode(H2L(mesasge,1,7));
        //解析报文编号
        commonHeadMessageVo.setMessageNo(H2L(mesasge,8,4));
        //解析报文功能码
        commonHeadMessageVo.setMessageType(H2L(mesasge,12,1));
        return commonHeadMessageVo;
    }

    private CommonHeadMessageVo parseHistoryHeadMessage(String[] mesasge){
        logger.debug("parseHistoryHeadMessage({})", mesasge);
        CommonHeadMessageVo commonHeadMessageVo = new CommonHeadMessageVo();
        //解析设备种类
        commonHeadMessageVo.setDeviceKind(H2L(mesasge[0]));
        //解析设备编号
        commonHeadMessageVo.setDeviceCode(H2L(mesasge,1,7));
        return commonHeadMessageVo;
    }

    private CommonHeadMessageVo parseHeartBeatHeadMessage(String[] mesasge){
        logger.debug("parseHeartBeatHeadMessage({})", mesasge);
        CommonHeadMessageVo commonHeadMessageVo = new CommonHeadMessageVo();
        //解析设备种类
        commonHeadMessageVo.setDeviceKind(H2L(mesasge[0]));
        //解析设备编号
        commonHeadMessageVo.setDeviceCode(H2L(mesasge,1,7));
        //解析报文功能码
        commonHeadMessageVo.setMessageType(H2L(mesasge[8]));
        //解析设备信号强度
        commonHeadMessageVo.setDeviceSignalStrength(H2L(mesasge[11]));
        return commonHeadMessageVo;
    }

    private Long B2L(String binaryMessage){
        return Long.parseLong(binaryMessage,2);
    }

    private Long B2L(String binaryMessage,int fromIndex,int offset){
        if(binaryMessage.length() >= (fromIndex + offset)){
            String subMessage = binaryMessage.substring(fromIndex,fromIndex+offset);
            return B2L(subMessage);
        }
        return null;
    }

    private Long H2L(String hexMessage){
        return Long.parseLong(hexMessage.replaceAll("^0[x|X]", ""),16);
    }

    private String H2S(String[] mesasge,int fromIndex,int offset){
        if(mesasge.length >= (fromIndex + offset)){
            StringBuilder hexMessage = new StringBuilder();
            for (int i = fromIndex; i < (fromIndex + offset); i++) {
                hexMessage.append(mesasge[i]);
            }
            return hexMessage.toString();
        }
        return null;
    }

    private Long H2L(String[] mesasge,int fromIndex,int offset){
        if(mesasge.length >= (fromIndex + offset)){
            StringBuilder hexMessage = new StringBuilder();
            for (int i = fromIndex; i < (fromIndex + offset); i++) {
                hexMessage.append(mesasge[i]);
            }
            return H2L(hexMessage.toString());
        }
        return null;
    }

    private String H2C(String hexMessage){
        int messageInt = Integer.parseInt(hexMessage.replaceAll("^0[x|X]", ""),16);
        return String.valueOf((char)messageInt);
    }

    /**
     * 将十六进制的字符串转换成二进制的字符串
     *
     * @param hexMessage
     * @return
     */
    private String H2B(String hexMessage) {

        if (hexMessage == null || "".equals(hexMessage)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 将每一个十六进制字符分别转换成一个四位的二进制字符
        for (int i = 0; i < hexMessage.length(); i++) {
            String indexStr = hexMessage.substring(i, i + 1);
            StringBuilder binaryStr = new StringBuilder(Long.toBinaryString(H2L(indexStr)));
            while (binaryStr.length() < 4) {
                binaryStr.insert(0, "0");
            }
            sb.append(binaryStr);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        MessageHandler messageHandler = new MessageHandler();
        System.out.println(String.valueOf((char)messageHandler.H2L("2e").intValue()));
        System.out.println("1200".substring(0, 3));
        System.out.println(Long.parseLong("01".replaceAll("^0[x|X]", ""), 16));
        System.out.println(messageHandler.H2B("01"));
        System.out.println(Long.parseLong("111", 2));
    }



}