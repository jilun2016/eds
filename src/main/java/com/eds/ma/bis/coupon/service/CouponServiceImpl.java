package com.eds.ma.bis.coupon.service;

import com.eds.ma.bis.coupon.CouponKindEnum;
import com.eds.ma.bis.coupon.CouponStatusEnum;
import com.eds.ma.bis.coupon.CouponTypeEnum;
import com.eds.ma.bis.coupon.entity.UserCoupon;
import com.eds.ma.bis.user.entity.WxUser;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.page.Pagination;
import com.xcrm.common.util.DateFormatUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Transactional
@Service
public class CouponServiceImpl implements ICouponService {

    protected Logger log = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Autowired
    private BaseDaoSupport dao;


    @Override
    public Pagination queryUserCouponList(Long userId, String couponStatus, Integer pageNo, Integer pageSize) {
        Ssqb queryUserCouponsSqb = Ssqb.create("com.eds.coupon.queryUserCouponList")
                .setParam("couponStatus", couponStatus)
                .setParam("userId", userId)
                .setParam("pageNo", pageNo)
                .setParam("pageSize", pageSize);
        queryUserCouponsSqb.setIncludeTotalCount(true);
        return dao.findForPage(queryUserCouponsSqb);
    }

    @Override
    public void saveUserSubscirpeCoupon(Long userId, String wxUnionId) {
        //通过wxUnionId查询是否已经关注公众号
        QueryBuilder querySubQb = QueryBuilder.where(Restrictions.eq("wxUnionId",wxUnionId))
                .and(Restrictions.eq("dataStatus",1));
        WxUser wxUser = dao.query(querySubQb,WxUser.class);
        if(Objects.nonNull(wxUser) && BooleanUtils.isTrue(wxUser.getSubscribeStatus())){
            Date now = DateFormatUtils.getNow();
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setName("公众号优惠券");
            userCoupon.setKind(CouponKindEnum.S_YHQZL_SUBSCRIBE.value());
            userCoupon.setType(CouponTypeEnum.S_YHQLX_HB.value());
            userCoupon.setBenefit(BigDecimal.TEN);
            userCoupon.setBeginTime(DateFormatUtils.getFirstTimeOfDay(now));
            userCoupon.setEndTime(DateFormatUtils.addDate(now,30));
            userCoupon.setIsDj(true);
            userCoupon.setCouponStatus(CouponStatusEnum.S_HYYHQZT_WSY.value());
            userCoupon.setCreated(DateFormatUtils.getNow());
            dao.save(userCoupon);
        }else{
            throw new BizCoreRuntimeException(BizErrorConstants.USER_COUPON_UNSUBSCRIBE_ERROR);
        }

    }
}
