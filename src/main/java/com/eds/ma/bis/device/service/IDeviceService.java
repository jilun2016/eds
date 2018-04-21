package com.eds.ma.bis.device.service;

import com.eds.ma.bis.device.entity.Device;
import com.eds.ma.bis.device.entity.UserDeviceRecord;
import com.eds.ma.bis.device.vo.DeviceInfoVo;
import com.eds.ma.bis.device.vo.DeviceRentDetailVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
     * 查询用户使用中的设备
     * @param userId
     * @return
     */
    int queryUserRentingDeviceCount(Long userId);

    /**
     * 租借发起-->更新租借设备
     * @param deviceId
     * @param userId
     * @param orderId
     */
    void updateRentDevice(Long deviceId, Long userId, Long orderId);

    /**
     * 查询待租借设备信息
     * @param deviceId
     */
    DeviceRentDetailVo queryRentDeviceById(Long deviceId);

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
     * 租借设备
     * @param deviceId
     * @param openId
     * @param userLat
     * @param userLng
     */
    void deviceRent(Long deviceId, String openId, BigDecimal userLat, BigDecimal userLng);

    /**
     * 设备押金支付
     * @param openId
     */
    Map<String, Object> deviceDepositPrepay(String openId);

    /**
     * 保存设备租借记录
     * @param userDeviceRecord
     */
    void saveUserDeviceRecord(UserDeviceRecord userDeviceRecord);
}
