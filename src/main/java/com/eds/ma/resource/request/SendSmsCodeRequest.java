package com.eds.ma.resource.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * 发送短信验证码请求
 * @Author gaoyan
 * @Date: 2018/4/22
 */
@Data
public class SendSmsCodeRequest {
	
	@NotEmpty(message="手机号不予许为空")
	@Pattern(regexp="^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$")
	private String mobile;

	@NotEmpty(message = "appId不允许为空")
	private String appId;
	
	private String jcaptchaCode;

}
