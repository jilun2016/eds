package com.eds.ma.quartz.service;

public interface IJobService {

    /**
     * 定时刷新微信Token
     */
    void wxRefreshToken();

}
