package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.MongoDeviceGPS;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 位置消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component(value = "gpsMessageHandler")
public class GPSMessageHandler extends BaseMessageHandler {

    private static Logger logger = Logger.getLogger(GPSMessageHandler.class);

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //解析位置消息
        MongoDeviceGPS mongoDeviceGPS = messageHandler.parseGPSMessage(commonHeadMessageVo, mesasge);
    }
}