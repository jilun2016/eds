package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.MongoDeviceRegister;
import com.eds.ma.socket.SessionClient;
import com.eds.ma.socket.SocketConstants;
import com.eds.ma.socket.message.MessageTypeConstants;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.eds.ma.socket.util.SocketMessageUtils;
import com.xcrm.cloud.database.db.util.StringUtil;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.log.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
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
    private AsyncTaskExecutor asyncTaskExecutor;


    @Override
    public Long getMessageType() {
        return MessageTypeConstants.DEVICE_REGISTER;
    }

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //解析注册消息
        MongoDeviceRegister mongoDeviceRegister = parseRegisterMessage(commonHeadMessageVo, mesasge);
        sendDataMessage(mongoDeviceRegister,mesasge);
    }

    public void sendDataMessage(MongoDeviceRegister mongoDeviceRegister,String[] mesasge) {

//        sendByte = SocketMessageUtils.HBytes("21F628852BB8C60100000001F1898604332318200034730D")
//        Long shortDeviceICCID = SocketMessageUtils.H2L(mesasge,16,7);
//        byte[] checkByte = buildMessageCheckByte(mongoDeviceRegister.sum(),MessageTypeConstants.DEVICE_REGISTER,shortDeviceICCID);
//        sendByte[23] = checkByte[0];
        asyncTaskExecutor.execute(()->{
            byte[] sendByte = SocketMessageUtils.HBytes(StringUtils.join(mesasge,""));
//            byte[] sendByte = SocketMessageUtils.HBytes(mesasge);
            SessionClient.sendMessage(mongoDeviceRegister.getDeviceCode(),sendByte);
        });
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