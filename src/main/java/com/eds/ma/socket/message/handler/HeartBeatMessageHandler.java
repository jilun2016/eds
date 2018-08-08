package com.eds.ma.socket.message.handler;

import com.xcrm.log.Logger;
import org.springframework.stereotype.Component;


/**
 * 心跳消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */

@Component
public class HeartBeatMessageHandler extends BaseMessageHandler {
    private static Logger logger = Logger.getLogger(HeartBeatMessageHandler.class);

    @Override
    public void processDataMessage() {

    }
}