package com.eds.ma.socket.message.service;


import com.eds.ma.mongodb.collection.MongoDeviceGPS;
import com.eds.ma.mongodb.collection.MongoDeviceHeartBeat;

public interface ISocketMessageService {

    /**
     * 查询设备状态信息
     * @param deviceOriginCode
     * @return
     */
    MongoDeviceHeartBeat queryDeviceStatusInfo(Long deviceOriginCode);

    /**
     * 查询设备位置信息
     * @param messageNo 消息编码
     * @return
     */
    MongoDeviceGPS queryMessageGPS(Long messageNo);

}
