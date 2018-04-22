package com.eds.ma.resource.request;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 用户提现请求
 * @Author gaoyan
 * @Date: 2018/4/1
 */
public class UserWithdrawRequest {

    @NotEmpty(message="短信验证码不允许为空")
    private String smsCode;

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserWithdrawRequest{");
        sb.append("smsCode='").append(smsCode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}