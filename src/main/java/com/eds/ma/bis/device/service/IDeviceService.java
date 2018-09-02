package com.eds.ma.bis.device.service;

import com.eds.ma.bis.device.entity.Device;
import com.eds.ma.bis.device.entity.DeviceRelation;
import com.eds.ma.bis.device.entity.UserDeviceRecord;
import com.eds.ma.bis.device.vo.*;
import com.xcrm.common.page.Pagination;

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
     * 查询店铺所属的空闲设备信息
     * @param spId 店铺id
     */
    List<IdleDeviceVo> queryIdleDeviceListBySpId(Long spId);

    /**
     * 通过用户id查询用户使用中的设备
     * @param userId 用户id
     * @param spId
     */
    List<UserDeviceVo> queryUserDeviceList(Long userId, Long spId);

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
     * @param userId
     * @param userLat
     * @param userLng
     */
    void deviceRent(Long deviceId, Long userId, BigDecimal userLat, BigDecimal userLng);

    /**
     * 归还设备
     * @param deviceId
     * @param userId
     * @param userLat
     * @param userLng
     */
    Long deviceReturn(Long deviceId, Long userId, BigDecimal userLat, BigDecimal userLng);

    /**
     * 保存设备租借记录
     * @param userDeviceRecord
     */
    void saveUserDeviceRecord(UserDeviceRecord userDeviceRecord);

    /**
     * 根据设备id查询硬件设备信息
     * @param deviceId
     * @return
     */
    DeviceRelation queryDeviceRelationByDeviceId(Long deviceId);

    /**
     * 发送设备锁定消息到客户端
     * @param deviceId 设备id
     * @param lockStatus 锁定状态 {@link com.eds.ma.socket.SocketConstants}
     */
    void sendDevcieStatusMessage(Long deviceId,Integer lockStatus);

    /**
     * 异步更新设备锁定状态
     * @param deviceId
     * @param lockStatus
     */
    void asyncUpdateDeviceStatus(Long deviceId,Integer lockStatus);

    /**
     * 查询设备常见问题
     * @param pageNo 页码
     * @param pageSize 页大小
     * @return
     */
    Pagination queryDeviceFaq(Integer pageNo, Integer pageSize);

    /**
     * 向硬件发送查询位置指令
     * @param deviceId
     */
    void queryDevicePosition(Long deviceId);
}
