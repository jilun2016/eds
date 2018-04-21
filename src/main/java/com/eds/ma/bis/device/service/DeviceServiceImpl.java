package com.eds.ma.bis.device.service;

import com.eds.ma.bis.common.entity.EdsConfig;
import com.eds.ma.bis.common.service.IEdsConfigService;
import com.eds.ma.bis.device.DeviceStatusEnum;
import com.eds.ma.bis.device.OrderStatusEnum;
import com.eds.ma.bis.device.entity.Device;
import com.eds.ma.bis.device.entity.UserDeviceRecord;
import com.eds.ma.bis.device.vo.DeviceInfoVo;
import com.eds.ma.bis.device.vo.DeviceRentDetailVo;
import com.eds.ma.bis.order.OrderCodeCreater;
import com.eds.ma.bis.order.TransTypeEnum;
import com.eds.ma.bis.order.entity.Order;
import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.entity.UserWallet;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.wx.service.IWxPayService;
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
import java.util.Map;
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
    private IWxPayService wxPayService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IEdsConfigService edsConfigService;


    @Override
    public Device queryDeviceById(Long deviceId) {
        return dao.queryById(deviceId,Device.class);
    }

    @Override
    public int queryUserRentingDeviceCount(Long userId) {
        Ssqb queryDeviceCountSqb = Ssqb.create("com.eds.device.queryUserRentingDeviceCount")
                .setParam("userId",userId);
        return dao.findForObj(queryDeviceCountSqb,Integer.class);
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
    public DeviceRentDetailVo queryRentDeviceById(Long deviceId) {
        Ssqb queryDeviceSqb = Ssqb.create("com.eds.device.queryRentDeviceById")
                .setParam("deviceId",deviceId);
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
    public void deviceRent(Long deviceId, String openId, BigDecimal userLat, BigDecimal userLng) {
        //查询设备信息进行校验
        DeviceRentDetailVo deviceRentDetailVo = queryRentDeviceById(deviceId);
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
                ,userLat.doubleValue(), userLng.doubleValue());
        if(checkDistance >edsConfig.getValidDistance()){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_OUT_RANGE);
        }

        //对用户信息,钱包进行校验
        User user = userService.checkUserExist(openId);
        Long userId = user.getId();
        UserWallet userWallet = userService.queryUserWalletByUserIdWithLock(userId);

        //押金不足校验
        BigDecimal defaultUnitDeposit = edsConfigService.queryEdsConfigDeposit();
        BigDecimal defaultCurrentDeposit = caculateCurrentDeposit(userId,defaultUnitDeposit);
        BigDecimal allNeedDeposit = defaultCurrentDeposit.add(defaultUnitDeposit);
        if(allNeedDeposit.compareTo(userWallet.getDeposit())>0){
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
        order.setRentStartTime(now);
        order.setSpId(deviceRentDetailVo.getSpId());
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
        userDeviceRecord.setUserLat(userLat);
        userDeviceRecord.setUserLng(userLng);
        saveUserDeviceRecord(userDeviceRecord);
    }

    @Override
    public Map<String, Object> deviceDepositPrepay(String openId) {
        //计算押金
        BigDecimal defaultUnitDeposit = edsConfigService.queryEdsConfigDeposit();
        return wxPayService.prepay(openId, TransTypeEnum.S_JYLX_YJCZ.value(),defaultUnitDeposit,"支付押金");
    }

    @Override
    public void saveUserDeviceRecord(UserDeviceRecord userDeviceRecord) {
        dao.save(userDeviceRecord);
    }

    private BigDecimal caculateCurrentDeposit(Long userId,BigDecimal defaultUnitDeposit){
        int userDeviceCount = queryUserRentingDeviceCount(userId);
        return defaultUnitDeposit.multiply(BigDecimal.valueOf(userDeviceCount));
    }


}
