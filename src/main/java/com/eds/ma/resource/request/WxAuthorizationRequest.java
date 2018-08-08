package com.eds.ma.resource.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class WxAuthorizationRequest {

    @NotEmpty(message = "手机号不允许为空")
    private String mobile;

    @NotEmpty(message = "短信验证码不允许为空")
    private String smsCode;

    @NotEmpty(message = "用户id不允许为空")
    private String openId;
}
