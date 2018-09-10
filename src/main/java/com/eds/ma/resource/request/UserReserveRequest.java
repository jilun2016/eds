package com.eds.ma.resource.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class UserReserveRequest {

    @NotEmpty(message = "手机号不允许为空")
    private String mobile;

    @NotEmpty(message = "短信验证码不允许为空")
    private String smsCode;

    @NotNull(message="店铺id不予许为空")
    private Long spId;
}
