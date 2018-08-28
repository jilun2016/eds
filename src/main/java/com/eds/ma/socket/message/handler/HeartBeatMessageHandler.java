package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.MongoDeviceHeartBeat;
import com.eds.ma.mongodb.collection.MongoDeviceHeartBeatRecord;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 心跳消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */

@Component
public class HeartBeatMessageHandler extends BaseMessageHandler {

    private static Logger logger = Logger.getLogger(HeartBeatMessageHandler.class);

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //解析心跳消息
        MongoDeviceHeartBeatRecord mongoDeviceHeartBeat = messageHandler.parseHeartBeatMessage(commonHeadMessageVo, mesasge);
    }
}