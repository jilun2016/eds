package com.eds.ma.rest.integration;

import com.eds.ma.bis.wx.WxTokenTypeEnum;
import com.eds.ma.bis.wx.entity.AccessToken;
import com.eds.ma.bis.wx.service.IWxService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.eds.ma.rest.common.CommonConstants;
import com.eds.ma.rest.common.ErrorMessage;
import com.eds.ma.rest.common.RestErrorCode;
import com.eds.ma.util.CookieUtils;
import com.eds.ma.util.ErrorCodeMessageUtil;
import com.eds.ma.util.HTTPUtil;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * 微信授权验证
 * @Author gaoyan
 * @Date: 2018/2/10
 */
public class AuthoricationFilter implements ContainerRequestFilter,ContainerResponseFilter {
	
	private static Logger logger = Logger.getLogger(AuthoricationFilter.class);

	@Autowired
	private SysConfig sysConfig;

	@Autowired
	private IWxService wxService;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String agent = requestContext.getHeaderString("user-agent");

		//判断是否微信登录,非微信登陆的话 跳转提示
		if (!agent.toLowerCase().contains("micromessenger")) {
			ErrorMessage errorMessage = ErrorCodeMessageUtil.buildErrorMessage(RestErrorCode.UNSUPPORTED_WX_BROWSER);
			requestContext.abortWith(errorMessage.buildUnauthorizedResponse());
			return;
		}

		Cookie cookieFromOpenId = CookieUtils.getCookie(requestContext.getCookies(), CommonConstants.WX_OPEN_ID_COOKIE);
		//如果cookie为空,那么返回未授权,需要重新登录
		if(Objects.isNull(cookieFromOpenId)){
			String wxAuthUrl = wxService.buildWxAuthRedirect("WxRedirectUrl");
			logger.info("AuthoricationFilter login redirectUrl :{},wxAuthUrl :{}",wxAuthUrl);
			ErrorMessage errorMessage = ErrorCodeMessageUtil.buildErrorMessage(RestErrorCode.WX_NOT_AUTH.code(),wxAuthUrl);
			requestContext.abortWith(errorMessage.buildUnauthorizedResponse());
			return ;
		}else{
			String openId = cookieFromOpenId.getValue();
			//获取微信用户信息,校验是否微信授权
			AccessToken accessToken = wxService.queryAccessTokenByType(WxTokenTypeEnum.ACCESS_TOKEN, sysConfig.getWxAppId());
			Map<String,Object> userInfoParaMap = new HashMap<>();
			userInfoParaMap.put("access_token",accessToken.getToken());
			userInfoParaMap.put("openid",openId);
			userInfoParaMap.put("lang","zh_CN");
			Map<String, Object> wxUserMap = HTTPUtil.sendGet(sysConfig.getWxUserInfoUrl(),userInfoParaMap);
			if(MapUtils.isNotEmpty(wxUserMap)){
				if(wxUserMap.containsKey("errmsg")){
					String errmsg = MapUtils.getString(wxUserMap,"errmsg","获取用户信息失败");
					logger.error("WxResource.queryWxUser occurs error.openId:{},userInfoParaMap:{},errmsg:{}",openId,userInfoParaMap,errmsg);
					ErrorMessage errorMessage = ErrorCodeMessageUtil.buildErrorMessage(RestErrorCode.WX_AUTH_USER_INFO_ERROR.code(),RestErrorCode.WX_AUTH_USER_INFO_ERROR.reason());
					requestContext.abortWith(errorMessage.buildUnauthorizedResponse());
					return ;

				}
				logger.info("WxResource.queryWxUser user:" + wxUserMap);
				//如果没有授权,那么返回未授权code
				Boolean subscribe = MapUtils.getBoolean(wxUserMap,"subscribe");
				if(BooleanUtils.isFalse(subscribe)){
					ErrorMessage errorMessage = ErrorCodeMessageUtil.buildErrorMessage(RestErrorCode.WX_AUTH_USER_NO_SUBSCRIBE.code(),RestErrorCode.WX_AUTH_USER_NO_SUBSCRIBE.reason());
					requestContext.abortWith(errorMessage.buildUnauthorizedResponse());
					return ;
				}
			}else{
				ErrorMessage errorMessage = ErrorCodeMessageUtil.buildErrorMessage(RestErrorCode.WX_AUTH_USER_INFO_ERROR.code(),RestErrorCode.WX_AUTH_USER_INFO_ERROR.reason());
				requestContext.abortWith(errorMessage.buildUnauthorizedResponse());
				return ;
			}
			requestContext.setProperty(CommonConstants.WX_OPEN_ID_COOKIE, cookieFromOpenId.getValue());
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

	}

}
