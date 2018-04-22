package com.eds.ma.bis.message;

/**
 * 消息推送类型
 * @Author gaoyan
 * @Date: 2017/7/10
 */
public enum MessagePushTypeEnum {

	/**
	 * 微信粉丝消息
	 */
	MESSAGE_WX("MESSAGE_WX") {
		@Override
		public String getDesc() {
			return "微信粉丝消息";
		}
	},

	/**
	 * 手机短信
	 */
	MESSAGE_SMS("MESSAGE_SMS"){
		@Override
		public String getDesc() {
			return "手机短信";
		}
	};

	private final String value;

	MessagePushTypeEnum(final String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}

	public String getDesc() {
		return "";
	}
}
