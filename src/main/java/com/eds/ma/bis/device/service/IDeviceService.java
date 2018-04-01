package com.eds.ma.bis.device.service;

import com.eds.ma.bis.device.entity.Device;
import com.eds.ma.bis.device.vo.DeviceInfoVo;
import com.eds.ma.bis.user.entity.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * 设备service
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public interface IDeviceService {

    /**
     * 查询设备信息
     * @param deviceId
     */
    Device queryDeviceById(Long deviceId);

    /**
     * 查询指定范围附近的设备
     * @param minUserLat   最小纬度
     * @param maxUserLat   最大纬度
     * @param minUserLng   最小精度
     * @param maxUserLng   最大精度
     * @return
     */
    List<DeviceInfoVo.DeviceDetailVo> queryNearbyDevices(double minUserLat, double maxUserLat, double minUserLng, double maxUserLng);

    /**
     * 设备押金支付
     * @param openId
     * @param deviceId
     * @param userLat
     * @param userLng
     */
    void deviceDepositPrepay(String openId, Long deviceId, BigDecimal userLat, BigDecimal userLng);
}
