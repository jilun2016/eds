package com.eds.ma.bis.device.service;

import com.eds.ma.bis.common.entity.EdsConfig;
import com.eds.ma.bis.common.service.IEdsConfigService;
import com.eds.ma.bis.coupon.CouponKindEnum;
import com.eds.ma.bis.coupon.CouponStatusEnum;
import com.eds.ma.bis.coupon.CouponTypeEnum;
import com.eds.ma.bis.coupon.entity.UserCoupon;
import com.eds.ma.bis.coupon.service.ICouponService;
import com.eds.ma.bis.device.DeviceStatusEnum;
import com.eds.ma.bis.device.OrderStatusEnum;
import com.eds.ma.bis.device.entity.Device;
import com.eds.ma.bis.device.entity.Sp;
import com.eds.ma.bis.device.entity.UserDeviceRecord;
import com.eds.ma.bis.device.vo.*;
import com.eds.ma.bis.message.TmplEvent;
import com.eds.ma.bis.message.service.IMessageService;
import com.eds.ma.bis.message.vo.SmsMessageContent;
import com.eds.ma.bis.order.OrderCodeCreater;
import com.eds.ma.bis.order.entity.Order;
import com.eds.ma.bis.order.entity.OrderCoupon;
import com.eds.ma.bis.order.service.IOrderService;
import com.eds.ma.bis.user.entity.UserReserve;
import com.eds.ma.bis.user.entity.UserReserveRecord;
import com.eds.ma.bis.user.entity.UserWallet;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.mongodb.collection.MongoDeviceGPS;
import com.eds.ma.mongodb.collection.MongoDeviceHeartBeat;
import com.eds.ma.rest.common.BizErrorConstants;
import com.eds.ma.socket.SocketConstants;
import com.eds.ma.socket.message.MessageTypeConstants;
import com.eds.ma.socket.message.handler.CommonMessageHandler;
import com.eds.ma.socket.message.service.ISocketMessageService;
import com.eds.ma.util.DistanceUtil;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.page.Pagination;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.common.util.ListUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeviceServiceImpl implements IDeviceService {

    protected Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    private BaseDaoSupport dao;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICouponService couponService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IEdsConfigService edsConfigService;

    @Autowired
    private ISocketMessageService socketMessageService;

    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private CommonMessageHandler commonMessageHandler;

    @Autowired
    private IMessageService messageService;


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
    public List<UserDeviceVo> queryUserDeviceList(Long userId, Long spId) {
        Ssqb queryDeviceSqb = Ssqb.create("com.eds.device.queryUserDeviceList")
                .setParam("userId",userId)
                .setParam("spId",spId);
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
        double checkDistance = DistanceUtil.getDistance(deviceRentDetailVo.getSpLng().doubleValue()
                ,deviceRentDetailVo.getSpLat().doubleValue()
                ,userLng.doubleValue(), userLat.doubleValue());
        if(checkDistance >edsConfig.getValidDistance()){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RENT_OUT_RANGE);
        }
        //获取设备的位置
        MongoDeviceGPS mongoDeviceGPS = socketMessageService.queryMessageGPS(sysConfig.getDeviceEnable()?deviceRentDetailVo.getDeviceGpsNo():1L);
        if(Objects.isNull(mongoDeviceGPS)){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_POSITISON_ERROR);
        }

        //设备,用户位置校验
        double checkDeviceDistance = DistanceUtil.getDistance(deviceRentDetailVo.getSpLng().doubleValue()
                ,deviceRentDetailVo.getSpLat().doubleValue()
                ,Double.valueOf(mongoDeviceGPS.getDeviceLng()), Double.valueOf(mongoDeviceGPS.getDeviceLat()));
        if(checkDeviceDistance >edsConfig.getValidDistance()){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RENT_OUT_RANGE);
        }

        //查询设备状态
        if(sysConfig.getDeviceEnable()){
            MongoDeviceHeartBeat deviceHeartBeat = socketMessageService.queryDeviceStatusInfo(deviceRentDetailVo.getDeviceOriginCode());
            if(Objects.isNull(deviceHeartBeat)
                    || Objects.equals(deviceHeartBeat.getDeviceUseStatus(),1L)
                    || Objects.equals(deviceHeartBeat.getDeviceNTCStatus(),1L)
                    || Objects.equals(deviceHeartBeat.getDeviceTemperatureStatus(),1L)
                    || Objects.equals(deviceHeartBeat.getDeviceIntakeValveStatus(),1L)
                    || Objects.equals(deviceHeartBeat.getDeviceElectricityStatus(),1L)
                    || (!Objects.equals(deviceHeartBeat.getDeviceReturnStatus(),3L))){
                throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RENT_STATUS_ERROR);
            }
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
        userDeviceRecord.setDeviceLat(BigDecimal.valueOf(Double.valueOf(mongoDeviceGPS.getDeviceLat())));
        userDeviceRecord.setDeviceLng(BigDecimal.valueOf(Double.valueOf(mongoDeviceGPS.getDeviceLat())));
        userDeviceRecord.setDeviceStatus(DeviceStatusEnum.S_SPZT_SYZ.value());
        userDeviceRecord.setOpTime(now);
        userDeviceRecord.setOrderId(order.getId());
        userDeviceRecord.setUserId(userId);
        userDeviceRecord.setSpId(deviceRentDetailVo.getSpId());
        userDeviceRecord.setUserLat(userLat);
        userDeviceRecord.setUserLng(userLng);
        saveUserDeviceRecord(userDeviceRecord);
        if(sysConfig.getDeviceEnable()){
            //硬件发送指令解锁
            sendDevcieStatusMessage(deviceRentDetailVo,SocketConstants.DEVICE_LOCK_UNLOCK);
        }
    }

    @Override
    public Long deviceReturn(Long deviceId, Long userId, BigDecimal userLat, BigDecimal userLng) {
        //校验用户信息
        EdsConfig edsConfig = edsConfigService.queryEdsConfig();
        //用户的位置和店铺的位置是否在有效距离内
        SpDetailVo spDetailVo = queryNearestbySpByCoordinate(userLat.doubleValue(), userLng.doubleValue(), edsConfig.getNearbyDistance());

        if(Objects.isNull(spDetailVo)){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_SP_NOT_EXIST);
        }

        //查询设备信息进行校验 设备状态是否租借中
        DeviceRentDetailVo deviceRentDetailVo = queryRentDeviceById(deviceId,userId);
        if(Objects.isNull(deviceRentDetailVo)){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_NOT_EXIST_ERROR);
        }

        Long orderId = deviceRentDetailVo.getOrderId();
        if(Objects.isNull(orderId)){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_ORDER_ID_NOT_EXIST_ERROR);
        }

        if((!Objects.equals(deviceRentDetailVo.getDeviceStatus(), DeviceStatusEnum.S_SPZT_SYZ.value()))
                ||(!Objects.equals(deviceRentDetailVo.getOrderStatus(), OrderStatusEnum.S_DDZT_JXZ.value()))){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_ON_RETURN_STATUS_ERROR);
        }

        //查询设备状态
        if(sysConfig.getDeviceEnable()){
            MongoDeviceHeartBeat deviceHeartBeat = socketMessageService.queryDeviceStatusInfo(deviceRentDetailVo.getDeviceOriginCode());
            if(Objects.isNull(deviceHeartBeat)
                    || Objects.equals(deviceHeartBeat.getDeviceUseStatus(),1L)
                    || Objects.equals(deviceHeartBeat.getDeviceNTCStatus(),1L)
                    || Objects.equals(deviceHeartBeat.getDeviceTemperatureStatus(),1L)
                    || Objects.equals(deviceHeartBeat.getDeviceIntakeValveStatus(),1L)
                    || Objects.equals(deviceHeartBeat.getDeviceElectricityStatus(),1L)
                    || (!Objects.equals(deviceHeartBeat.getDeviceReturnStatus(),1L)
                    && !Objects.equals(deviceHeartBeat.getDeviceReturnStatus(),0L))){
                throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_STATUS_ERROR);
            }
        }

        //获取设备的位置信息和用户的位置信息是否在有效距离内
        //设备,用户位置校验
        double checkDistance = DistanceUtil.getDistance(spDetailVo.getSpLng().doubleValue()
                ,spDetailVo.getSpLat().doubleValue()
                ,userLng.doubleValue(), userLat.doubleValue());
        if(checkDistance >edsConfig.getValidDistance()){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_OUT_RANGE);
        }

        //获取设备的位置信息和店铺的位置是否在有效距离内(硬件指令获取硬件位置)
        //获取设备的位置
        MongoDeviceGPS mongoDeviceGPS = socketMessageService.queryMessageGPS(sysConfig.getDeviceEnable()?deviceRentDetailVo.getDeviceGpsNo():1L);
        if(Objects.isNull(mongoDeviceGPS)){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_POSITISON_ERROR);
        }
        //设备,用户位置校验
        double checkDeviceDistance = DistanceUtil.getDistance(deviceRentDetailVo.getSpLng().doubleValue()
                ,deviceRentDetailVo.getSpLat().doubleValue()
                ,Double.valueOf(mongoDeviceGPS.getDeviceLng()), Double.valueOf(mongoDeviceGPS.getDeviceLat()));
        if(checkDeviceDistance > edsConfig.getValidDistance()){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RENT_OUT_RANGE);
        }

        //验证成功,更新订单,归还设备,更新设备位置信息,设备状态,钱包扣除金额
        Date now = DateFormatUtils.getNow();
        //计算金额
        BigDecimal rentFee = orderService.caculateRentFee(now);
        //查询优惠券
        List<UserCoupon> validMaxUserCouponList =couponService.queryValidUserCouponList(userId);
        BigDecimal couponMoney = BigDecimal.ZERO;
        if(ListUtil.isNotEmpty(validMaxUserCouponList)){
            //将优惠券更新为已使用状态
            validMaxUserCouponList.forEach(userCoupon -> {
                userCoupon.setCouponStatus(CouponStatusEnum.S_HYYHQZT_YSY.value());
                dao.update(userCoupon);
            });
            couponMoney = validMaxUserCouponList.stream().map(UserCoupon::getBenefit).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        rentFee = rentFee.subtract(couponMoney).compareTo(BigDecimal.ZERO)>0?rentFee.subtract(couponMoney):BigDecimal.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;
        if(rentFee.compareTo(BigDecimal.ZERO)>0){
            //更新钱包状态
            UserWallet userWallet = userService.queryUserWalletByUserIdWithLock(userId);
            //计算租借金额
            BigDecimal balanceFee = BigDecimal.ZERO;
            if(userWallet.getBalance().compareTo(BigDecimal.ZERO)>0){
                if(userWallet.getBalance().compareTo(rentFee) > 0){
                    balanceFee = rentFee;
                }else {
                    balanceFee = userWallet.getBalance();
                }
            }
            BigDecimal  depositFee = rentFee.subtract(balanceFee);
            if(userWallet.getDeposit().compareTo(depositFee)<=0){
                depositFee = userWallet.getDeposit();
            }
            userService.updateUserWallet(userId,depositFee.negate(),balanceFee.negate());


            totalFee = depositFee.add(balanceFee);
        }

        //更新订单
        Ssqb updateOrderSqb = Ssqb.create("com.eds.order.updateOrder")
                .setParam("orderId",deviceRentDetailVo.getOrderId())
                .setParam("deviceId",deviceRentDetailVo.getDeviceId())
                .setParam("userId",userId)
                .setParam("spId",spDetailVo.getSpId())
                .setParam("rentFee",rentFee)
                .setParam("totalFee",totalFee)
                .setParam("couponMoney",couponMoney)
                .setParam("returnTime",now);
        int updateOrderResult =  dao.updateByMybatis(updateOrderSqb);

        if(updateOrderResult <= 0){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_RETURN_ORDER_NOT_EXIST_ERROR);
        }

        //保存订单优惠券信息
        if(ListUtil.isNotEmpty(validMaxUserCouponList)){
            List<OrderCoupon> saveOrderCouponList =  new ArrayList<>();
            validMaxUserCouponList.forEach(userCoupon -> {
                OrderCoupon saveOrderCoupon = new OrderCoupon();
                saveOrderCoupon.setCouponId(userCoupon.getId());
                saveOrderCoupon.setOrderId(deviceRentDetailVo.getOrderId());
                saveOrderCoupon.setCreated(now);
                saveOrderCouponList.add(saveOrderCoupon);
            });
            if(ListUtil.isNotEmpty(saveOrderCouponList)){
                dao.batchSave(saveOrderCouponList,OrderCoupon.class);
            }
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

        //保存优惠券信息,老用户增加20元优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setName("老用户专享优惠券");
        userCoupon.setKind(CouponKindEnum.S_YHQZL_ORDER.value());
        userCoupon.setType(CouponTypeEnum.S_YHQLX_HB.value());
        userCoupon.setBenefit(BigDecimal.valueOf(20));
        userCoupon.setBeginTime(DateFormatUtils.getFirstTimeOfDay(now));
        userCoupon.setEndTime(DateFormatUtils.addDate(now,30));
        userCoupon.setIsDj(false);
        userCoupon.setCouponStatus(CouponStatusEnum.S_HYYHQZT_WSY.value());
        userCoupon.setCreated(DateFormatUtils.getNow());
        dao.save(userCoupon);

        //保存租借记录
        //生成设备租借记录
        UserDeviceRecord userDeviceRecord = new UserDeviceRecord();
        userDeviceRecord.setCreated(now);
        userDeviceRecord.setDeviceId(deviceId);
        userDeviceRecord.setDeviceLat(BigDecimal.valueOf(Double.valueOf(mongoDeviceGPS.getDeviceLat())));
        userDeviceRecord.setDeviceLng(BigDecimal.valueOf(Double.valueOf(mongoDeviceGPS.getDeviceLng())));
        userDeviceRecord.setDeviceStatus(DeviceStatusEnum.S_SPZT_DZJ.value());
        userDeviceRecord.setOpTime(now);
        userDeviceRecord.setOrderId(deviceRentDetailVo.getOrderId());
        userDeviceRecord.setUserId(userId);
        userDeviceRecord.setUserLat(userLat);
        userDeviceRecord.setUserLng(userLng);
        userDeviceRecord.setSpId(spDetailVo.getSpId());
        saveUserDeviceRecord(userDeviceRecord);
        //硬件发送指令上锁
        if(sysConfig.getDeviceEnable()){
            sendDevcieStatusMessage(deviceRentDetailVo,SocketConstants.DEVICE_LOCK_LOCK);
        }
        //当前店铺的预约进行推送
        deviceReserveIdleConfirm(spDetailVo.getSpId());
        return orderId;
    }

    @Override
    public void saveUserDeviceRecord(UserDeviceRecord userDeviceRecord) {
        dao.save(userDeviceRecord);
    }

    @Override
    public void sendDevcieStatusMessage(DeviceRentDetailVo deviceRentDetailVo, Long lockStatus) {
        commonMessageHandler.sendDataMessage(MessageTypeConstants.DEVICE_CONTROL,
                deviceRentDetailVo.getDeviceOriginCode(),
                lockStatus,
                48L,deviceRentDetailVo.getAdjustParamA1(),deviceRentDetailVo.getAdjustParamA2(),deviceRentDetailVo.getAdjustParamA3(),
                deviceRentDetailVo.getAdjustParamB1(),deviceRentDetailVo.getAdjustParamB2(),deviceRentDetailVo.getAdjustParamB3());
    }

    @Override
    public Pagination queryDeviceFaq(Integer pageNo, Integer pageSize) {
        Ssqb queryOrderListSqb = Ssqb.create("com.eds.device.queryDeviceFaq")
                .setParam("pageNo", pageNo)
                .setParam("pageSize", pageSize);
        queryOrderListSqb.setIncludeTotalCount(true);
        return dao.findForPage(queryOrderListSqb);
    }

    @Override
    public void queryDevicePosition(Long deviceId) {
        Device device = queryDeviceById(deviceId);
        if(Objects.isNull(device)){
            throw new BizCoreRuntimeException(BizErrorConstants.DEVICE_NOT_EXIST_ERROR);
        }

        //根据消息的报文功能码不同,走不同处理
        if(sysConfig.getDeviceEnable()){
            commonMessageHandler.sendDataMessage(MessageTypeConstants.DEVICE_GPS,device.getDeviceOriginCode());
        }


    }

    @Override
    public void sendUserReserveSmsCode(String mobile, Long spId) {
        Sp sp = dao.queryById(spId,Sp.class);
        if(Objects.isNull(sp)){
            throw new BizCoreRuntimeException(BizErrorConstants.SP_NOT_EXIST_ERROR);
        }
        List<IdleDeviceVo> idleDeviceVos = queryIdleDeviceListBySpId(spId);
        if(ListUtil.isNotEmpty(idleDeviceVos)){
            throw new BizCoreRuntimeException(BizErrorConstants.SP_DEVICE_EXIST_IDLE_ERROR);
        }

        UserReserve userReserve = queryUserReserveByMobile(mobile,spId);
        if(Objects.nonNull(userReserve) && userReserve.getIsReserveValid()){
            throw new BizCoreRuntimeException(BizErrorConstants.SP_DEVICE_RESERVE_DUPLICATE_ERROR);
        }

        Date now = DateFormatUtils.getNow();
        if(Objects.isNull(userReserve)){
            userReserve = new UserReserve();
            userReserve.setMobile(mobile);
//            String sendSmsCode = RandomStringUtils.randomNumeric(6);
            String sendSmsCode = "8888";
            userReserve.setReserveSmsCode(sendSmsCode);
            userReserve.setReserveSmsExpired(new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000));
            userReserve.setCreated(now);
            userReserve.setReserveSpId(spId);
            dao.save(userReserve);
        }else{
            Date smsExpired = userReserve.getReserveSmsExpired();
            String smsCode = userReserve.getReserveSmsCode();

            if(smsExpired != null && StringUtils.isNotEmpty(smsCode)) {
                DateTime activationCodeExpired2 = new DateTime(smsExpired);
                if(activationCodeExpired2.isBeforeNow()) {
                    //短信码已经失效则发送新的验证码
                    smsCode = RandomStringUtils.randomNumeric(6);
                }
                Date lastSendTime = new Timestamp(smsExpired.getTime() - 30 * 60 * 1000);
                if(System.currentTimeMillis() - lastSendTime.getTime() < 30 * 1000){
                    //频率小于30秒
                    throw new BizCoreRuntimeException(BizErrorConstants.SMS_CHECK_FREQUENCY_ERROR);
                }
            } else {
                smsCode = RandomStringUtils.randomNumeric(6);
            }
            smsCode = "8888";
            userReserve.setReserveSmsCode(smsCode);
            userReserve.setReserveSmsExpired(new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000));
            userReserve.setUpdated(now);
            dao.update(userReserve);
        }
//        //发送短信
//        SmsMessageContent smsMessageContent = new SmsMessageContent();
//        smsMessageContent.setTmplEvent(TmplEvent.member_register.value());
//        smsMessageContent.setMobile(mobile);
//        String sendSmsCode = Objects.equals(appId,EdsAppId.eds_ali.value())?user.getAliSmsCode():user.getWxSmsCode();
//        smsMessageContent.setSmsParams(new String[]{sendSmsCode});
//        messageService.pushSmsMessage(smsMessageContent);

    }

    @Override
    public void userReserveConfirm(Long spId, String mobile, String smsCode) {
        UserReserve userReserve = queryUserReserveByMobile(mobile,spId);

        if (Objects.isNull(userReserve)) {
            throw new BizCoreRuntimeException(BizErrorConstants.USER_RESERVE_NOT_EXIST_ERROR);
        }

        String dbSmsCode = userReserve.getReserveSmsCode();
        long now = System.currentTimeMillis();
        Date activeExpired = userReserve.getReserveSmsExpired();
        if(StringUtils.isEmpty(dbSmsCode)) {
            //验证码错误
            throw new BizCoreRuntimeException(BizErrorConstants.SMSCODE_ERROR);
        }
        if(!dbSmsCode.equals(smsCode)) {
            //验证码错误
            throw new BizCoreRuntimeException(BizErrorConstants.SMSCODE_ERROR);
        } else if(activeExpired != null && activeExpired.getTime() < now){
            //已过期
            throw new BizCoreRuntimeException(BizErrorConstants.SMSCODE_EXPIRED);
        } else {
            Ssqb updateDeviceReserveSqb = Ssqb.create("com.eds.device.updateDeviceReserveSuc")
                    .setParam("reserveSpId", spId)
                    .setParam("mobile", mobile);
            dao.updateByMybatis(updateDeviceReserveSqb);
        }
    }

    @Async
    @Override
    public void deviceReserveIdleConfirm(Long spId) {
        //查询店铺信息
        Sp sp = dao.queryById(spId,Sp.class);
        if(Objects.nonNull(sp) && sp.getDataStatus()){
            //通过spid查询当前店铺的所有预约记录
            QueryBuilder queryReserveIdleQb = QueryBuilder.where(Restrictions.eq("reserveSpId",spId))
                    .and(Restrictions.eq("isReserveValid",1))
                    .and(Restrictions.eq("dataStatus",1));
            List<UserReserve> userReserveList = dao.queryList(queryReserveIdleQb,UserReserve.class);
            if(ListUtil.isNotEmpty(userReserveList)){
                List<Long> reserveIdList = userReserveList.stream().map(UserReserve::getId).collect(Collectors.toList());
                String reserveMobiles = userReserveList.stream().map(UserReserve::getMobile).collect(Collectors.joining(","));;
                //将预约通知记录修改为无效,通知进行短信通知
                QueryBuilder updateReserveQb = QueryBuilder.where(Restrictions.in("id",reserveIdList));
                UserReserve updateUserReserve = new UserReserve();
                updateUserReserve.setIsReserveValid(false);
                dao.updateByQuery(updateUserReserve,updateReserveQb);
                Date now = DateFormatUtils.getNow();
                //增加t_eds_user_reserve_record
                List<UserReserveRecord> userReserveRecordList = new ArrayList<>();
                userReserveList.forEach(userReserve -> {
                    UserReserveRecord userReserveRecord = new UserReserveRecord();
                    userReserveRecord.setMobile(userReserve.getMobile());
                    userReserveRecord.setReserveNoticeTime(now);
                    userReserveRecord.setReserveSpId(spId);
                    userReserveRecord.setReserveTime(userReserve.getCreated());
                    userReserveRecordList.add(userReserveRecord);
                });
                dao.batchSave(userReserveRecordList,UserReserveRecord.class);
                //发送验证码短信
                SmsMessageContent smsMessageContent = new SmsMessageContent();
                smsMessageContent.setTmplEvent(TmplEvent.device_reserve_idle_confirm.value());
                smsMessageContent.setMobile(reserveMobiles);
                smsMessageContent.setSmsParams(new String[]{sp.getSpName()});
                messageService.pushSmsMessage(smsMessageContent);
            }
        }
    }

    private UserReserve queryUserReserveByMobile(String mobile, Long spId) {
        QueryBuilder queryUserReserveQb = QueryBuilder.where(Restrictions.eq("mobile",mobile))
                .and(Restrictions.eq("dataStatus",1))
                .and(Restrictions.eq("reserveSpId",spId));
        return dao.query(queryUserReserveQb,UserReserve.class);
    }

}
