package com.eds.ma.util;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Cookie;
import java.util.Map;

/**
 * Cookie 辅助类
 */
public class CookieUtils {
	

	/**
	 * 获得cookie
	 * 
	 * @param cookies cookies
	 * @param name cookie name
	 * @return if exist return cookie, else return null.
	 */
	public static Cookie getCookie(Map<String, Cookie> cookies, String name) {
		if(MapUtils.isNotEmpty(cookies)){
			return cookies.get(name);
		}
		return null;
	}

	/**
	 * 根据部署路径，将cookie保存在根目录。
	 *
	 * @param request
	 * @param response
	 * @param name
	 * @param value
	 * @param expiry
	 * @param domain
	 * @return
	 */
	public static javax.servlet.http.Cookie addCookie(HttpServletRequest request,
													  HttpServletResponse response, String name, String value,
													  Integer expiry, String domain) {
		javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(name, value);
		if (expiry != null) {
			cookie.setMaxAge(expiry);
		}
		if (StringUtils.isNotBlank(domain)) {
			cookie.setDomain(domain);
		}
		String ctx = request.getContextPath();
		cookie.setPath(StringUtils.isBlank(ctx) ? "/" : ctx);
		response.addCookie(cookie);
		return cookie;
	}

}
