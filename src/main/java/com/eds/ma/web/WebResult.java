package com.eds.ma.web;

import com.xcrm.common.http.ResultParseException;
import com.xcrm.common.json.JacksonJsonHandler;
import com.xcrm.common.json.JsonHandler;
import com.xcrm.common.util.ListUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: WebResult
 * @author Brian
 * @date 2014年5月16日 下午2:49:03
 * @version
 */
public class WebResult {
	
	private static final Logger log = LoggerFactory.getLogger(WebResult.class);
	
	public static JsonHandler jsonHandler;
	
	static {
		log.debug("JacksonJsonHandler is inited");
		jsonHandler = new JacksonJsonHandler();
	}
	
	/**
	 * 发送json。使用UTF-8编码。(默认成功返回)
	 * 发送成功标示
	 * @param response
	 *            HttpServletResponse
	 * 
	 */
	public static void renderJsonSucMsg(HttpServletResponse response) {
		render(response, "application/json;charset=UTF-8", "{\"result\":1,\"msg\":\"\"}", HttpServletResponse.SC_OK);
	}
	/**
	 * 发送json。使用UTF-8编码。(成功返回，带提示语)
	 * 发送成功标示
	 * @param response
	 *            HttpServletResponse
	 * 
	 */
	public static void renderJsonSucMsg(HttpServletResponse response, String msg) {
		render(response, "application/json;charset=UTF-8", "{\"result\":1,\"msg\":\""+msg+"\"}", HttpServletResponse.SC_OK);
	}
	/**
	 * 发送json。使用UTF-8编码。
	 * 发送成功标示
	 * @param response
	 *            HttpServletResponse
	 * 
	 */
	public static void renderJsonData(HttpServletResponse response, String msg , int httpStatus) {
		render(response, "application/json;charset=UTF-8", "{\"result\":1,\"msg\":\""+msg+"\"}",httpStatus);
	}
	/**
	 * 发送json。使用UTF-8编码。
	 * 发送成功标示
	 * @param response
	 *            HttpServletResponse
	 *  {"result":1,"msg":"","data":" + data + "}
	 */
	public static void renderJsonDataDefault(HttpServletResponse response, Object data) {
		String json = "";
		try {
			json = jsonHandler.marshaller(data);
		} catch (ResultParseException e) {
			e.printStackTrace();
		}
		render(response, "application/json;charset=UTF-8", "{\"result\":1,\"msg\":\"\",\"data\":" + json + "}", HttpServletResponse.SC_OK);
	}
	
	/**
	 * 发送json。使用UTF-8编码。
	 * 发送成功标示
	 * @param response
	 *            HttpServletResponse
	 *  {"result":1,"msg":"","data":" + data + "}
	 */
	public static void renderJsonDataFail(HttpServletResponse response, String msg) {
		render(response, "application/json;charset=UTF-8", "{\"result\":0,\"msg\":\""+msg+"\"}", HttpServletResponse.SC_OK);
	}
	
	
	public static void renderJsonFailResult(HttpServletResponse response, BindingResult result){
		String msg = getBindingResult(result);
		render(response, "application/json;charset=UTF-8", "{\"result\":0,\"msg\":"+msg+"}", HttpServletResponse.SC_OK);
	}

	public static String renderJsonFailResult(BindingResult result){
		return getBindingResult(result);
	}
	
	private static String getBindingResult(BindingResult result){
		List<String> fieldErrorMsgList = new ArrayList<String>();
		List<String> globalErrorMsgList = new ArrayList<String>();
		HashMap<String,List<String>> resultMap = new HashMap<String,List<String>>();
		if(result.getFieldErrorCount()>0){
			List<FieldError> fieldErrorList = result.getFieldErrors();
			if(ListUtil.isNotEmpty(fieldErrorList)){
				for(FieldError fieldError:fieldErrorList){
					fieldErrorMsgList.add(fieldError.getDefaultMessage());
				}
			}
			if(ListUtil.isNotEmpty(fieldErrorMsgList)){
				resultMap.put("fieldError", fieldErrorMsgList);
			}
		}
		
		if(result.getGlobalErrorCount()>0){
			List<ObjectError> globalErrorList = result.getGlobalErrors();
			if(ListUtil.isNotEmpty(globalErrorList)){
				for(ObjectError objectError:globalErrorList){
					globalErrorMsgList.add(objectError.getDefaultMessage());
				}
			}
			if(ListUtil.isNotEmpty(globalErrorMsgList)){
				resultMap.put("globalError", globalErrorMsgList);
			}
		}
		
		
		String msg = "";
		try {
			msg = jsonHandler.marshaller(resultMap);
		} catch (ResultParseException e) {
			e.printStackTrace();
		}
		return msg;
	}
	/**
	 * 发送json。使用UTF-8编码。
	 * session失效 返回 httpCode = 403
	 * @param response
	 *            HttpServletResponse
	 * @param msg
	 *            发送的字符串
	 */
	public static void renderJsonSessionTimeOutMsg(HttpServletResponse response) {
		render(response, "application/json;charset=UTF-8", "{\"result\":0,\"msg\":\"账号登陆超时,请重新登录\"}", HttpServletResponse.SC_UNAUTHORIZED);
	}
	/**
	 * 发送json。使用UTF-8编码。
	 * 发送失败标示
	 * @param response
	 *            HttpServletResponse
	 * @param msg
	 *            发送的字符串
	 */
	public static void renderJsonObjectNotExist(HttpServletResponse response, String msg) {
		render(response, "application/json;charset=UTF-8", "{\"result\":0,\"msg\":\""+msg+"\"}", HttpServletResponse.SC_NOT_FOUND);
	}
	
	
	
	/**
	 * 发送json
	 * 发送成功标示
	 * 
	 */
	public static String renderJsonSucMsg() {
		return "{\"result\":1,\"msg\":\"\"}";
	}
	
	/**
	 * 发送json。使用UTF-8编码。(成功返回，带提示语)
	 * 发送成功标示
	 * @param msg
	 */
	public static String renderJsonSucMsg(String msg) {
		return "{\"result\":1,\"msg\":\""+msg+"\"}";
	}
	
	/**
	 * 发送json。使用UTF-8编码。
	 * 发送成功标示
	 * @param data
	 *  {"result":1,"msg":"","data":" + data + "}
	 */
	public static String renderJsonDataDefault(Object data) {
		String json = "";
		try {
			json = jsonHandler.marshaller(data);
		} catch (ResultParseException e) {
			e.printStackTrace();
		}
		return "{\"result\":1,\"msg\":\"\",\"data\":" + json + "}";
	}
	
	/**
	 * 发送json。使用UTF-8编码。
	 * 发送成功标示
	 * @param msg
	 *  {"result":1,"msg":"","data":" + data + "}
	 */
	public static String renderJsonDataFail(String msg) {
		return "{\"result\":0,\"msg\":\""+msg+"\"}";
	}
	
	
	/**
	 * 发送内容。使用UTF-8编码。
	 * 
	 * @param response
	 * @param contentType
	 * @param text
	 */
	public static void render(HttpServletResponse response, String contentType,
                              String text, int httpStatus) {
		response.setContentType(contentType);
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setStatus(httpStatus);
		try {
			response.setContentLength(text.getBytes("utf-8").length);
		} catch (UnsupportedEncodingException e1) {
			log.error("render exception：",e1);
		}
		try {
			response.getWriter().write(text);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
