package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.MongoDeviceReport;
import com.eds.ma.socket.message.MessageTypeConstants;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.eds.ma.socket.util.SocketMessageUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.MILLISECOND;


/**
 * 历史报告消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component
public class HistoryMessageHandler extends BaseMessageHandler {


    private static Logger logger = Logger.getLogger(HistoryMessageHandler.class);

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Override
    public Long getMessageType() {
        return MessageTypeConstants.DEVICE_HISTORY;
    }

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //处理历史报告消息
        //查询设备检测结果信息
        MongoDeviceReport mongoDeviceReport = parseHistoryMessage(commonHeadMessageVo, mesasge);
    }

    @Override
    public void sendDataMessage(CommonHeadMessageVo commonHeadMessageVo, Long... mesasgeField) {

    }



    /**
     * 解析历史上传消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    private MongoDeviceReport parseHistoryMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge){
        MongoDeviceReport mongoDeviceReport = new MongoDeviceReport();
        mongoDeviceReport.setDeviceKind(commonHeadMessageVo.getDeviceKind());
        mongoDeviceReport.setDeviceCode(commonHeadMessageVo.getDeviceCode());

        //解析检测数据的时间
        Calendar syncDate = Calendar.getInstance();
        syncDate.set(SocketMessageUtils.H2L(mesasge[9]).intValue()+2000,
                SocketMessageUtils.H2L(mesasge[10]).intValue() - 1,
                SocketMessageUtils.H2L(mesasge[11]).intValue(),
                SocketMessageUtils.H2L(mesasge[12]).intValue(),
                SocketMessageUtils.H2L(mesasge[13]).intValue(),0);
        syncDate.set(MILLISECOND,0);

        //解析经纬度
        //解析纬度
        StringBuilder deviceLatSb = new StringBuilder();
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[14]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[15]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[16]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[17]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[18]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[19]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[20]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[21]));
        deviceLatSb.append(SocketMessageUtils.H2C(mesasge[22]));
        mongoDeviceReport.setDeviceLat(deviceLatSb.toString());
        String deviceLatDesc = SocketMessageUtils.H2C(mesasge[23]);
        mongoDeviceReport.setDeviceLatDesc(deviceLatDesc);
        //解析经度
        StringBuilder deviceLngSb = new StringBuilder();
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[24]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[25]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[26]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[27]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[28]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[29]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[30]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[31]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[32]));
        deviceLngSb.append(SocketMessageUtils.H2C(mesasge[33]));
        String deviceLngDesc = SocketMessageUtils.H2C(mesasge[34]);
        mongoDeviceReport.setDeviceLng(deviceLngSb.toString());
        mongoDeviceReport.setDeviceLngDesc(deviceLngDesc);


        mongoDeviceReport.setCreated(syncDate.getTime());

        mongoDeviceReport.setASLIntegerValue(SocketMessageUtils.H2L(mesasge[35]));
        mongoDeviceReport.setASLHighDecimal(SocketMessageUtils.H2L(mesasge[36]));
        mongoDeviceReport.setASLLowDecimal(SocketMessageUtils.H2L(mesasge[37]));

        mongoDeviceReport.setDeviceTemperature(SocketMessageUtils.H2L(mesasge[38]));
        mongoDeviceReport.setDeviceInTemperature(SocketMessageUtils.H2L(mesasge[39]));


        mongoDeviceReport.setHCHOIntegerValue(SocketMessageUtils.H2L(mesasge[40]));
        mongoDeviceReport.setHCHOHighDecimal(SocketMessageUtils.H2L(mesasge[41]));
        mongoDeviceReport.setHCHOLowDecimal(SocketMessageUtils.H2L(mesasge[42]));
        mongoTemplate.insert(mongoDeviceReport);
        return mongoDeviceReport;
    }
}