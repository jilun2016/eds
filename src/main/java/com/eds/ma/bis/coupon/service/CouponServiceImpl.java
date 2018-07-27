package com.eds.ma.bis.coupon.service;

import com.eds.ma.bis.coupon.CouponKindEnum;
import com.eds.ma.bis.coupon.CouponStatusEnum;
import com.eds.ma.bis.coupon.CouponTypeEnum;
import com.eds.ma.bis.coupon.entity.UserCoupon;
import com.eds.ma.bis.coupon.vo.UserCouponClaimStatusVo;
import com.eds.ma.bis.user.UserDistStatusEnum;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.entity.UserDist;
import com.eds.ma.bis.user.entity.UserDistItem;
import com.eds.ma.bis.user.entity.WxUser;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.user.vo.UserShareCouponVo;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.xcrm.cloud.database.db.BaseDaoSupport;
import com.xcrm.cloud.database.db.query.QueryBuilder;
import com.xcrm.cloud.database.db.query.Ssqb;
import com.xcrm.cloud.database.db.query.expression.Restrictions;
import com.xcrm.common.page.Pagination;
import com.xcrm.common.util.DateFormatUtils;
import com.xcrm.common.util.ListUtil;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Transactional
@Service
public class CouponServiceImpl implements ICouponService {

    protected Logger log = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Autowired
    private BaseDaoSupport dao;

    @Autowired
    private IUserService userService;

    @Override
    public UserCouponClaimStatusVo queryUserCouponClaimStatusList(String openId,String wxUnionId) {
        UserCouponClaimStatusVo userCouponClaimStatusVo = new UserCouponClaimStatusVo();
        QueryBuilder queryDistQb = QueryBuilder.where(Restrictions.eq("dataStatus",1))
                .and(Restrictions.eq("sponsorOpenId", openId));
        List<UserDist> userDistList = dao.queryList(queryDistQb,UserDist.class);
        if(ListUtil.isEmpty(userDistList)){
            userCouponClaimStatusVo.setCouponShareStatus(true);
        }else {
            //如果非空,那么存在进行中的,那么还是去分享状态
            if(userDistList.stream().anyMatch(userDist -> Objects.equals(userDist.getDistStatus(),UserDistStatusEnum.S_DIST_JXZ.value()))){
                userCouponClaimStatusVo.setCouponShareStatus(true);
            }else{
                userCouponClaimStatusVo.setCouponShareStatus(false);
            }
        }

        QueryBuilder querySubQb = QueryBuilder.where(Restrictions.eq("wxUnionId",wxUnionId))
                .and(Restrictions.eq("dataStatus",1));
        WxUser wxUser = dao.query(querySubQb,WxUser.class);
        if(Objects.nonNull(wxUser)&& BooleanUtils.isTrue(wxUser.getSubscribeStatus())){
            userCouponClaimStatusVo.setUserSubsribeStatus(true);
        }else{
            userCouponClaimStatusVo.setUserSubsribeStatus(false);
        }
        User user = userService.queryUserByOpenId(openId);
        userCouponClaimStatusVo.setCouponSubsribeClaimStatus(user.getSubscribeCoupon());
        return userCouponClaimStatusVo;
    }

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
    public List<UserCoupon> queryValidUserCouponList(Long userId) {
        List<UserCoupon> validMaxUserCouponList = new ArrayList<>();
        QueryBuilder querySubQb = QueryBuilder.where(Restrictions.eq("userId",userId))
                .and(Restrictions.eq("couponStatus",CouponStatusEnum.S_HYYHQZT_WSY.value()))
                .and(Restrictions.ge("endTime",DateFormatUtils.getStringToday()))
                .and(Restrictions.eq("dataStatus",1));
        List<UserCoupon> dbUserCouponList = dao.queryList(querySubQb,UserCoupon.class);
        //找出基础的最大的优惠券,和可叠加的最大优惠券
        if(ListUtil.isNotEmpty(dbUserCouponList)){
            dbUserCouponList.stream().filter(UserCoupon::getIsDj)
                    .max((o1, o2) -> o1.getBenefit().compareTo(o2.getBenefit())).ifPresent(validMaxUserCouponList::add);
            dbUserCouponList.stream().filter(userCoupon -> !userCoupon.getIsDj())
                    .max((o1, o2) -> o1.getBenefit().compareTo(o2.getBenefit())).ifPresent(validMaxUserCouponList::add);

        }
        return validMaxUserCouponList;
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
            //更新用户已领取关注公众号优惠券
            User updateUser = new User();
            updateUser.setId(userId);
            updateUser.setSubscribeCoupon(true);
            dao.update(updateUser);
        }else{
            throw new BizCoreRuntimeException(BizErrorConstants.USER_COUPON_UNSUBSCRIBE_ERROR);
        }

    }

    @Override
    public void saveUserDistCoupon(Long userId, String openId) {
        //查询进行中的分享的id
        QueryBuilder queryDistQb = QueryBuilder.where(Restrictions.eq("sponsorOpenId", openId))
                .and(Restrictions.eq("distStatus", UserDistStatusEnum.S_DIST_JXZ.value()))
                .and(Restrictions.eq("dataStatus", 1));
        UserDist userDist = dao.query(queryDistQb, UserDist.class);
        if(Objects.nonNull(userDist)
                && Objects.equals(userDist.getDistStatus(),UserDistStatusEnum.S_DIST_JXZ.value())){
            //查询已经激活的用户
            QueryBuilder queryDistItemQb = QueryBuilder.where(Restrictions.eq("distId", userDist.getId()))
                    .and(Restrictions.eq("isActived", 1))
                    .and(Restrictions.eq("dataStatus", 1));
            List<UserDistItem> userDistItemList = dao.queryList(queryDistItemQb, UserDistItem.class);
            if(ListUtil.isNotEmpty(userDistItemList)){
                Integer shareCount = userDistItemList.size();
                BigDecimal benefit = shareCount>5?BigDecimal.valueOf(20):BigDecimal.TEN;
                Date now = DateFormatUtils.getNow();
                UserCoupon userCoupon = new UserCoupon();
                userCoupon.setUserId(userId);
                userCoupon.setName("分享优惠券");
                userCoupon.setKind(CouponKindEnum.S_YHQLX_SHARE.value());
                userCoupon.setType(CouponTypeEnum.S_YHQLX_HB.value());
                userCoupon.setBenefit(benefit);
                userCoupon.setBeginTime(DateFormatUtils.getFirstTimeOfDay(now));
                userCoupon.setEndTime(DateFormatUtils.addDate(now,30));
                userCoupon.setIsDj(false);
                userCoupon.setCouponStatus(CouponStatusEnum.S_HYYHQZT_WSY.value());
                userCoupon.setCreated(DateFormatUtils.getNow());
                dao.save(userCoupon);
            }
            //将分享状态更新为已结束
            userDist.setDistStatus(UserDistStatusEnum.S_DIST_YJS.value());
            dao.update(userDist);
        }
    }

    @Override
    public Pagination queryUserShareCouponDetail(String openId, Integer pageNo, Integer pageSize) {
        Ssqb queryShareCouponSqb = Ssqb.create("com.eds.coupon.queryUserShareCouponDetail")
                .setParam("openId",openId)
                .setParam("pageNo", pageNo)
                .setParam("pageSize", pageSize);
        queryShareCouponSqb.setIncludeTotalCount(true);
        return dao.findForPage(queryShareCouponSqb);
    }
}
