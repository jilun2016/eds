package com.eds.ma.bis.user.service;

import com.eds.ma.bis.common.service.IEdsConfigService;
import com.eds.ma.bis.device.entity.Sp;
import com.eds.ma.bis.device.service.IDeviceService;
import com.eds.ma.bis.device.vo.IdleDeviceVo;
import com.eds.ma.bis.message.service.IMessageService;
import com.eds.ma.bis.user.entity.AliUser;
import com.eds.ma.bis.user.entity.UserReserve;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.common.util.ListUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 用户基本信息service
 *
 * @Author gaoyan
 * @Date: 2018/2/10
 */
@Transactional
@Service
public class UserReserveServiceImpl implements IUserReserveService {

    protected Logger log = LoggerFactory.getLogger(UserReserveServiceImpl.class);

    @Autowired
    private BaseDaoSupport dao;


    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IEdsConfigService edsConfigService;


    @Override
    public void sendUserReserveSmsCode(String mobile, Long spId) {
        Sp sp = dao.queryById(spId,Sp.class);
        if(Objects.isNull(sp)){
            throw new BizCoreRuntimeException(BizErrorConstants.SP_NOT_EXIST_ERROR);
        }
        List<IdleDeviceVo> idleDeviceVos = deviceService.queryIdleDeviceListBySpId(spId);
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

    }

    private UserReserve queryUserReserveByMobile(String mobile, Long spId) {
        QueryBuilder queryUserReserveQb = QueryBuilder.where(Restrictions.eq("mobile",mobile))
                .and(Restrictions.eq("dataStatus",1))
                .and(Restrictions.eq("reserveSpId",spId));
        return dao.query(queryUserReserveQb,UserReserve.class);
    }

}
