package com.eds.ma.util;



import com.eds.ma.config.ErrorCodeConfig;
import com.eds.ma.rest.common.ErrorMessage;
import com.eds.ma.rest.common.RestErrorCode;

import java.text.MessageFormat;

/**
 * 错误消息的读取工具类
 * 读取
 * @author gaoyan
 * @version 1.0
 * @created 2016年1月4日 下午4:37:33
 */

public class ErrorCodeMessageUtil {

	public static String getErrorCodeMessage(String key,Object ... arguments){
		String errorMessageTemplate = ErrorCodeConfig.errorMsg.get(key);
		if((arguments == null) || (arguments.length<=0)){
			return errorMessageTemplate;
		}else{
			return MessageFormat.format(errorMessageTemplate, arguments);
		}
	}

	public static ErrorMessage buildErrorMessage(String errorCode, String errorMessage) {
		return new ErrorMessage(errorCode, errorMessage);
	}

	public static ErrorMessage buildNotAuthErrorMessage() {
		return buildErrorMessage(RestErrorCode.NOT_AUTH.code(),RestErrorCode.NOT_AUTH.reason());
	}

	public static ErrorMessage buildErrorMessage(RestErrorCode unsupportedWxBrowser) {
		return buildErrorMessage(unsupportedWxBrowser.code(),unsupportedWxBrowser.reason());
	}
}
