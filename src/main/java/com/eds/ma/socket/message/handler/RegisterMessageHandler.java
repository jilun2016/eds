package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.MongoDeviceRegister;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 注册消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component
public class RegisterMessageHandler extends BaseMessageHandler {

    private static Logger logger = Logger.getLogger(RegisterMessageHandler.class);

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //解析注册消息
        MongoDeviceRegister mongoDeviceRegister = messageHandler.parseRegisterMessage(commonHeadMessageVo, mesasge);
    }
}