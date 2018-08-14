package com.eds.ma.socket.message.handler;

import com.eds.ma.mongodb.collection.MongoDeviceControl;
import com.eds.ma.socket.message.vo.CommonHeadMessageVo;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 设备控制消息解析处理
 * @Author gaoyan
 * @Date: 2018/7/23
 */
@Component
public class DeviceControlHandler extends BaseMessageHandler {

    private static Logger logger = Logger.getLogger(DeviceControlHandler.class);


    @Autowired
    private MessageHandler messageHandler;

    @Override
    public void processDataMessage(CommonHeadMessageVo commonHeadMessageVo, String[] mesasge) {
        //查询设备检测结果信息
        MongoDeviceControl mongoDeviceControl = messageHandler.parseControlMessage(commonHeadMessageVo, mesasge);
    }
}