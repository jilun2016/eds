package com.eds.ma.bis.coupon.service;

import com.xcrm.common.page.Pagination;

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
     * 领取关注公众号的优惠券
     *
     * @param userId
     * @param wxUnionId
     * @return
     */
    void saveUserSubscirpeCoupon(Long userId, String wxUnionId);
}
