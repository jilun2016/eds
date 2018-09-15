package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.MongoDeviceRegister;
import com.eds.ma.socket.SessionClient;
import com.eds.ma.socket.message.MessageTypeConstants;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.eds.ma.socket.util.SocketMessageUtils;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


/**
 * 注册消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component
public class RegisterMessageHandler extends BaseMessageHandler {

    private static Logger logger = Logger.getLogger(RegisterMessageHandler.class);

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    private CommonMessageHandler commonMessageHandler;


    @Override
    public Long getMessageType() {
        return MessageTypeConstants.DEVICE_REGISTER;
    }

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //解析注册消息
        MongoDeviceRegister mongoDeviceRegister = parseRegisterMessage(commonHeadMessageVo, mesasge);
        sendDataMessage(commonHeadMessageVo.getDeviceCode(),mesasge);
    }

    public void sendDataMessage(Long deviceCode,String[] mesasge) {
        SessionClient.sendMessage(deviceCode,SocketMessageUtils.HBytes(mesasge));
    }

    @Override
    public void sendDataMessage(CommonHeadMessageVo commonHeadMessageVo, Long... mesasgeField) {

    }


    /**
     * 解析注册消息
     * @param commonHeadMessageVo
     * @param mesasge
     * @return
     */
    private MongoDeviceRegister parseRegisterMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge){
        String deviceICCID = SocketMessageUtils.H2S(mesasge,13,10);
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
}