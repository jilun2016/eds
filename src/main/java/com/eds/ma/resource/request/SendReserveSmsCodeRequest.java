package com.eds.ma.resource.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SendReserveSmsCodeRequest {
	
	@NotEmpty(message="手机号不予许为空")
	@Pattern(regexp="^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$")
	private String mobile;

	@NotNull(message="店铺id不予许为空")
	private Long spId;

	private String jcaptchaCode;

}
