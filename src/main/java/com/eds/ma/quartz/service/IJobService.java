package com.eds.ma.quartz.service;

public interface IJobService {

    /**
     * 定时刷新微信Token
     */
    void wxRefreshToken();

    /**
     * 定时清理过期基础优惠券和会员优惠券
     */
    void couponExpiredRunJob();

}
