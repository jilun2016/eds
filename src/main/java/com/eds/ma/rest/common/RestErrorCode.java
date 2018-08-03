package com.eds.ma.rest.common;

public enum RestErrorCode {
	
	NOT_AUTH("1000", "授权失败"),
	
	NOT_FOUND("1001", "资源不存在"),
	
	REQUEST_FIELD_INVALID("1003", "请求字段非法"),
	
	SIGNATURE_NOT_MATCH ("1004", "请求的签名不匹配"),
	
	HTTP_HEADER_FIELD_INVALID("1005", "httpHeader缺少必要参数"),
	
	INTERNAL_SERVER_ERROR("1007", "系统内部错误"),
	
	REQUEST_TIME_TOO_SKEWED("1006" ,"您的计算机时间与北京时间不符，请调整时间后重新操作"),
	
	ACCESS_TOKEN_EXPIRED("1008", "免签令牌已失效"),
	
	UNSUPPORTED_MEDIA_TYPE("1009", "Unsupported Media Type"),

	UNSUPPORTED_WX_BROWSER("1010", "Unsupported Browser"),

	WX_NOT_AUTH("1011", ""),

	WX_AUTH_USER_INFO_ERROR("1012", "获取用户信息失败"),

	WX_AUTH_USER_NO_SUBSCRIBE("1013", "微信用户未关注公众号");


	private final String code;
    private final String reason;
	
	
	RestErrorCode(final String code, final String reasonPhrase) {
		this.code = code;
		this.reason = reasonPhrase;
	}


	public String code() {
		return code;
	}

	public String reason() {
		return reason;
	}
	
	/**
     * Convert a numerical status code into the corresponding Status.
     *
     * @param statusCode the numerical status code.
     * @return the matching Status or null is no matching Status is defined.
     */
    public static RestErrorCode fromCode(final String code) {
        for (RestErrorCode s : RestErrorCode.values()) {
            if (s.code.equals(code) ) {
                return s;
            }
        }
        return null;
    }
}
