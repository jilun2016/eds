package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.MongoDeviceRegister;
import com.eds.ma.mongodb.collection.MongoDeviceReport;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 报告消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component
public class ReportMessageHandler extends BaseMessageHandler {


    private static Logger logger = Logger.getLogger(ReportMessageHandler.class);

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //查询设备检测结果信息
        MongoDeviceReport mongoDeviceReport = messageHandler.parseReportMessage(commonHeadMessageVo, mesasge);
    }
}