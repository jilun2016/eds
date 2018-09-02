package com.eds.ma.socket.message.service;


import com.eds.ma.mongodb.collection.MongoDeviceHeartBeat;

public interface ISocketMessageService {

    /**
     * 查询设备状态信息
     * @param deviceOriginCode
     * @return
     */
    MongoDeviceHeartBeat queryDeviceStatusInfo(String deviceOriginCode);

}
