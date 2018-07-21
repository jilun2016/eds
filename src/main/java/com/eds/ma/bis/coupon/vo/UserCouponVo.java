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
public class UserCouponVo {

    private Long couponId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券类型
     */
    private String couponType;

    /**
     * 优惠 包含(折扣 红包)
     */
    private BigDecimal benefit;

    /**
     * 开始时间
     */
    private Date beginTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 优惠券状态
     */
    private String couponStatus;

}
