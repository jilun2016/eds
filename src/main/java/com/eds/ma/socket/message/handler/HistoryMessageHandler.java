package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.MongoDeviceControl;
import com.eds.ma.mongodb.collection.MongoDeviceReport;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 历史报告消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component
public class HistoryMessageHandler extends BaseMessageHandler {


    private static Logger logger = Logger.getLogger(HistoryMessageHandler.class);

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //处理历史报告消息
        //查询设备检测结果信息
        MongoDeviceReport mongoDeviceReport = messageHandler.parseHistoryMessage(commonHeadMessageVo, mesasge);

    }
}