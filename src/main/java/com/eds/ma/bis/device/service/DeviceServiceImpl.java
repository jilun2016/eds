package com.eds.ma.bis.device.service;

import com.eds.ma.bis.device.DeviceStatusEnum;
import com.eds.ma.bis.device.entity.Device;
import com.eds.ma.bis.device.vo.DeviceInfoVo;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.Ssqb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class DeviceServiceImpl implements IDeviceService {

    protected Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    private BaseDaoSupport dao;


    @Override
    public Device queryDeviceById(Long deviceId) {
        return dao.queryById(deviceId,Device.class);
    }

    @Override
    public List<DeviceInfoVo.DeviceDetailVo> queryNearbyDevices(double minUserLat, double maxUserLat, double minUserLng, double maxUserLng) {
        Ssqb queryDeviceSqb = Ssqb.create("com.eds.device.queryNearbyDevices")
                .setParam("minDeviceLat",minUserLat)
                .setParam("maxDeviceLat",maxUserLat)
                .setParam("minDeviceLng",minUserLng)
                .setParam("maxDeviceLng",maxUserLng);
        return dao.findForList(queryDeviceSqb,DeviceInfoVo.DeviceDetailVo.class);
    }

    @Override
    public void deviceDepositPrepay(String openId, Long deviceId, BigDecimal userLat, BigDecimal userLng) {
        //查询设备信息进行校验
        Device device = queryDeviceById(deviceId);
        if(Objects.isNull(device)){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_NOT_EXIST_ERROR);
        }

        if(Objects.equals(device.getDeviceStatus(), DeviceStatusEnum.S_SPZT_SYZ.value())){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_ON_BORROW_STATUS_ERROR);
        }




    }
}
