package com.eds.ma.bis.coupon.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户优惠券信息
 * @Author gaoyan
 * @Date: 2018/7/22
 */
@Data
public class UserCouponClaimStatusVo {

    /**
     * 分享优惠券,领取状态
     */
    private Boolean couponShareStatus;

    /**
     * 订阅公众号优惠券关注状态
     */
    private Boolean userSubsribeStatus;

    /**
     * 订阅公众号优惠券关注是否领取
     */
    private Boolean couponSubsribeClaimStatus;
}
