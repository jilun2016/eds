package com.eds.ma.resource.request;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * 发送短信验证码请求
 * @Author gaoyan
 * @Date: 2018/4/22
 */
public class SendSmsCodeRequest {
	
	@NotEmpty(message="手机号不予许为空")
	@Pattern(regexp="^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$")
	private String mobile;
	
	
	private String jcaptchaCode;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getJcaptchaCode() {
		return jcaptchaCode;
	}

	public void setJcaptchaCode(String jcaptchaCode) {
		this.jcaptchaCode = jcaptchaCode;
	}

	@Override
	public String toString() {
		return "SendSmsCodeRequest [mobile=" + mobile + ", jcaptchaCode=" + jcaptchaCode + "]";
	}

}
