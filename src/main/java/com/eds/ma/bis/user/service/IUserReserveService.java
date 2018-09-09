package com.eds.ma.bis.user.service;

/**
 * 用户预约接口
 * @Author gaoyan
 * @Date: 2017/12/24
 */
public interface IUserReserveService {

    /**
     * 发送设备预约的短信验证码
     * @param mobile
     * @param spId
     */
    void sendUserReserveSmsCode(String mobile, Long spId);
}
