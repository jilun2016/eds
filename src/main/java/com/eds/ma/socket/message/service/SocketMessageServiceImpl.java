package com.eds.ma.socket.message.service;

import com.eds.ma.config.SysConfig;
import com.eds.ma.mongodb.collection.MongoDeviceGPS;
import com.eds.ma.mongodb.collection.MongoDeviceHeartBeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SocketMessageServiceImpl implements ISocketMessageService {

    protected Logger logger = LoggerFactory.getLogger(SocketMessageServiceImpl.class);

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    private SysConfig sysConfig;


    @Override
    public MongoDeviceHeartBeat queryDeviceStatusInfo(String deviceOriginCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deviceCode").is(Long.valueOf(deviceOriginCode)));
        return mongoTemplate.findOne(query,MongoDeviceHeartBeat.class);
    }

    @Override
    public MongoDeviceGPS queryMessageGPS(Long messageNo) {
        Query query = new Query();
        query.addCriteria(Criteria.where("messageNo").is(messageNo));
        return mongoTemplate.findOne(query,MongoDeviceGPS.class);
    }
}
