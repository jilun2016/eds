package com.eds.ma.bis.coupon.service;

import com.eds.ma.bis.coupon.entity.UserCoupon;
import com.eds.ma.bis.user.vo.UserShareCouponVo;
import com.xcrm.common.page.Pagination;

import java.util.List;

/**
 * 用户接口
 * @Author gaoyan
 * @Date: 2017/12/24
 */
public interface ICouponService {

    /**
     * 查询用户的优惠券
     * @param userId
     * @param couponStatus
     * @param pageNo
     * @param pageSize
     * @return
     */
    Pagination queryUserCouponList(Long userId, String couponStatus, Integer pageNo, Integer pageSize);

    /**
     * 查询有效的用户的优惠券最大金额的优惠券
     * @param userId
     * @return
     */
    List<UserCoupon> queryValidUserCouponList(Long userId);

    /**
     * 领取关注公众号的优惠券
     *
     * @param userId
     * @param wxUnionId
     * @return
     */
    void saveUserSubscirpeCoupon(Long userId, String wxUnionId);

    /**
     * 保存用户分享的优惠券
     * @param userId
     * @param openId
     */
    void saveUserDistCoupon(Long userId, String openId);


    /**
     * 查询分享优惠券的关注用户信息
     * @param openId
     */
    Pagination queryUserShareCouponDetail(String openId, Integer pageNo, Integer pageSize);
}
