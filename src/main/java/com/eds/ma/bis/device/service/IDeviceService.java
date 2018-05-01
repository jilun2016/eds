package com.eds.ma.bis.device.service;

import com.eds.ma.bis.device.entity.Device;
import com.eds.ma.bis.device.entity.UserDeviceRecord;
import com.eds.ma.bis.device.vo.*;
import com.eds.ma.bis.user.entity.User;

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
     * 查询店铺所属的空闲设备信息
     * @param spId 店铺id
     */
    List<IdleDeviceVo> queryIdleDeviceListBySpId(Long spId);

    /**
     * 通过用户id查询用户使用中的设备
     * @param userId 用户id
     */
    List<UserDeviceVo> queryUserDeviceList(Long userId);

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
     * @param userId
     */
    DeviceRentDetailVo queryRentDeviceById(Long deviceId, Long userId);

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
     * 查询指定坐标下指定范围内的附近的店铺信息
     * @param userLat   用户纬度
     * @param userLng   用户精度
     * @param nearbyDistance 指定范围
     * @return
     */
    SpDetailVo queryNearestbySpByCoordinate(double userLat, double userLng,Integer nearbyDistance);

    /**
     * 租借设备
     * @param deviceId
     * @param openId
     * @param userLat
     * @param userLng
     */
    void deviceRent(Long deviceId, String openId, BigDecimal userLat, BigDecimal userLng);

    /**
     * 归还设备
     * @param deviceId
     * @param user
     * @param userLat
     * @param userLng
     */
    void deviceReturn(Long deviceId, User user, BigDecimal userLat, BigDecimal userLng);

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
