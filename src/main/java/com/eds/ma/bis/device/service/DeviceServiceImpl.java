package com.eds.ma.bis.device.service;

import com.eds.ma.bis.common.entity.EdsConfig;
import com.eds.ma.bis.common.service.IEdsConfigService;
import com.eds.ma.bis.device.DeviceStatusEnum;
import com.eds.ma.bis.device.OrderStatusEnum;
import com.eds.ma.bis.device.entity.Device;
import com.eds.ma.bis.device.entity.UserDeviceRecord;
import com.eds.ma.bis.device.vo.*;
import com.eds.ma.bis.order.OrderCodeCreater;
import com.eds.ma.bis.order.entity.Order;
import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.eds.ma.util.DistanceUtil;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.common.util.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class DeviceServiceImpl implements IDeviceService {

    protected Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    private BaseDaoSupport dao;

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IEdsConfigService edsConfigService;


    @Override
    public Device queryDeviceById(Long deviceId) {
        return dao.queryById(deviceId,Device.class);
    }

    @Override
    public List<IdleDeviceVo> queryIdleDeviceListBySpId(Long spId) {
        Ssqb queryDeviceSqb = Ssqb.create("com.eds.device.queryIdleDeviceListBySpId")
                .setParam("spId",spId);
        return dao.findForList(queryDeviceSqb,IdleDeviceVo.class);
    }

    @Override
    public List<UserDeviceVo> queryUserDeviceList(Long userId) {
        Ssqb queryDeviceSqb = Ssqb.create("com.eds.device.queryUserDeviceList");
        return dao.findForList(queryDeviceSqb,UserDeviceVo.class);
    }

    @Override
    public void updateRentDevice(Long deviceId, Long userId, Long orderId) {
        Ssqb updateDeviceSqb = Ssqb.create("com.eds.device.updateRentDevice")
                .setParam("orderId",orderId)
                .setParam("userId",userId)
                .setParam("deviceId",deviceId);
        dao.updateByMybatis(updateDeviceSqb);
    }

    @Override
    public DeviceRentDetailVo queryRentDeviceById(Long deviceId, Long userId) {
        Ssqb queryDeviceSqb = Ssqb.create("com.eds.device.queryRentDeviceById")
                .setParam("deviceId",deviceId)
                .setParam("userId",userId);
        return dao.findForObj(queryDeviceSqb,DeviceRentDetailVo.class);
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
    public SpDetailVo queryNearestbySpByCoordinate(double userLat, double userLng, Integer nearbyDistance) {
        Ssqb queryDeviceSqb = Ssqb.create("com.eds.device.queryNearbySpByCoordinate")
                .setParam("userLat",userLat)
                .setParam("userLng",userLng)
                .setParam("nearbyDistance",nearbyDistance);
        return dao.findForObj(queryDeviceSqb,SpDetailVo.class);
    }

    @Override
    public void deviceRent(Long deviceId, Long userId, BigDecimal userLat, BigDecimal userLng) {
        //查询设备信息进行校验
        DeviceRentDetailVo deviceRentDetailVo = queryRentDeviceById(deviceId, null);
        if(Objects.isNull(deviceRentDetailVo)){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_NOT_EXIST_ERROR);
        }

        if(!Objects.equals(deviceRentDetailVo.getDeviceStatus(), DeviceStatusEnum.S_SPZT_DZJ.value())){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_ON_BORROW_STATUS_ERROR);
        }

        EdsConfig edsConfig = edsConfigService.queryEdsConfig();
        //设备,用户位置校验
        double checkDistance = DistanceUtil.getDistance(deviceRentDetailVo.getDeviceLng().doubleValue()
                ,deviceRentDetailVo.getDeviceLat().doubleValue()
                ,userLng.doubleValue(), userLat.doubleValue());
        if(checkDistance >edsConfig.getValidDistance()){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RENT_OUT_RANGE);
        }

        //对用户信息,钱包进行校验
        //押金不足校验
        boolean isValid = userService.checkUserRentDepositValid(userId);
        if(!isValid){
            throw new BizCoreRuntimeException(BizErrorConstants.USER_DEPOSIT_NOT_ENOUGH_ERROR);
        }

        Date now = DateFormatUtils.getNow();
        //校验通过,生成设备状态 订单等信息
        //生成订单
        Order order = new Order();
        order.setCreated(now);
        order.setDeviceId(deviceId);
        order.setOrderCode(OrderCodeCreater.createTradeNO());
        order.setOrderStatus(OrderStatusEnum.S_DDZT_JXZ.value());
        order.setRentTime(now);
        order.setRentSpId(deviceRentDetailVo.getSpId());
        order.setUserId(userId);
        orderService.saveOrder(order);
        //更新设备状态使用中
        updateRentDevice(deviceId, userId, order.getId());

        //生成设备租借记录
        UserDeviceRecord userDeviceRecord = new UserDeviceRecord();
        userDeviceRecord.setCreated(now);
        userDeviceRecord.setDeviceId(deviceId);
        userDeviceRecord.setDeviceLat(deviceRentDetailVo.getDeviceLat());
        userDeviceRecord.setDeviceLng(deviceRentDetailVo.getDeviceLng());
        userDeviceRecord.setDeviceStatus(DeviceStatusEnum.S_SPZT_SYZ.value());
        userDeviceRecord.setOpTime(now);
        userDeviceRecord.setOrderId(order.getId());
        userDeviceRecord.setUserId(userId);
        userDeviceRecord.setSpId(deviceRentDetailVo.getSpId());
        userDeviceRecord.setUserLat(userLat);
        userDeviceRecord.setUserLng(userLng);
        saveUserDeviceRecord(userDeviceRecord);
    }

    @Override
    public void deviceReturn(Long deviceId, User user, BigDecimal userLat, BigDecimal userLng) {
        //校验用户信息
        Long userId = user.getId();

        //查询设备信息进行校验 设备状态是否租借中
        DeviceRentDetailVo deviceRentDetailVo = queryRentDeviceById(deviceId,userId);
        if(Objects.isNull(deviceRentDetailVo)){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_NOT_EXIST_ERROR);
        }

        if(Objects.isNull(deviceRentDetailVo.getOrderId())){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_ORDER_ID_NOT_EXIST_ERROR);
        }

        if(!Objects.equals(deviceRentDetailVo.getDeviceStatus(), DeviceStatusEnum.S_SPZT_SYZ.value())){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_ON_RETURN_STATUS_ERROR);
        }

        EdsConfig edsConfig = edsConfigService.queryEdsConfig();


        //获取设备的位置信息和用户的位置信息是否在有效距离内
        //设备,用户位置校验
        double checkDistance = DistanceUtil.getDistance(deviceRentDetailVo.getDeviceLng().doubleValue()
                ,deviceRentDetailVo.getDeviceLat().doubleValue()
                ,userLng.doubleValue(), userLat.doubleValue());
        if(checkDistance >edsConfig.getValidDistance()){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_OUT_RANGE);
        }

        //获取设备的位置信息和店铺的位置是否在有效距离内(硬件指令获取硬件位置)
        BigDecimal deviceLng = BigDecimal.ZERO;

        BigDecimal deviceLat = BigDecimal.ZERO;

        //用户的位置和店铺的位置是否在有效距离内
        SpDetailVo spDetailVo = queryNearestbySpByCoordinate(userLat.doubleValue(), userLng.doubleValue(), edsConfig.getNearbyDistance());
        if(Objects.isNull(spDetailVo)){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_SP_NOT_EXIST);
        }
        //验证成功,更新订单,归还设备,更新设备位置信息,设备状态
        Date now = DateFormatUtils.getNow();
        //计算金额
        BigDecimal orderMoney = BigDecimal.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;
        //更新订单
        Ssqb updateOrderSqb = Ssqb.create("com.eds.order.updateOrder")
                .setParam("orderId",deviceRentDetailVo.getOrderId())
                .setParam("deviceId",deviceRentDetailVo.getDeviceId())
                .setParam("userId",userId)
                .setParam("spId",spDetailVo.getSpId())
                .setParam("orderMoney",orderMoney)
                .setParam("totalFee",totalFee)
                .setParam("returnTime",now);
        int updateOrderResult =  dao.updateByMybatis(updateOrderSqb);

        if(updateOrderResult <= 0){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_ORDER_NOT_EXIST_ERROR);
        }

        //更新设备位置信息,及设备状态
        Ssqb updateDeviceSqb = Ssqb.create("com.eds.device.updateReturnDevice")
                .setParam("orderId",deviceRentDetailVo.getOrderId())
                .setParam("deviceId",deviceRentDetailVo.getDeviceId())
                .setParam("userId",userId)
                .setParam("spId",spDetailVo.getSpId());
        int updateDeviceResult = dao.updateByMybatis(updateDeviceSqb);
        if(updateDeviceResult<= 0){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_ORDER_NOT_EXIST_ERROR);
        }

        //保存租借记录
        //生成设备租借记录
        UserDeviceRecord userDeviceRecord = new UserDeviceRecord();
        userDeviceRecord.setCreated(now);
        userDeviceRecord.setDeviceId(deviceId);
        userDeviceRecord.setDeviceLat(deviceLat);
        userDeviceRecord.setDeviceLng(deviceLng);
        userDeviceRecord.setDeviceStatus(DeviceStatusEnum.S_SPZT_DZJ.value());
        userDeviceRecord.setOpTime(now);
        userDeviceRecord.setOrderId(deviceRentDetailVo.getOrderId());
        userDeviceRecord.setUserId(userId);
        userDeviceRecord.setUserLat(userLat);
        userDeviceRecord.setUserLng(userLng);
        userDeviceRecord.setSpId(spDetailVo.getSpId());
        saveUserDeviceRecord(userDeviceRecord);
    }

    @Override
    public void saveUserDeviceRecord(UserDeviceRecord userDeviceRecord) {
        dao.save(userDeviceRecord);
    }




}
