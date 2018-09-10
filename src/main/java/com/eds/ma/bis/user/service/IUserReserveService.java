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

    /**
     * 用户预约确认
     * @param spId
     * @param mobile
     * @param smsCode
     */
    void userReserveConfirm(Long spId, String mobile, String smsCode);
}
