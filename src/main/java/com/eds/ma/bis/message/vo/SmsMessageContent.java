package com.eds.ma.bis.message.vo;

public class SmsMessageContent extends MessageContent{

    /**
     * 手机号
     */
    private String mobile;

	/**
	 * 短信参数
	 */
	private Object[] smsParams;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Object[] getSmsParams() {
		return smsParams;
	}

	public void setSmsParams(Object[] smsParams) {
		this.smsParams = smsParams;
	}
}
