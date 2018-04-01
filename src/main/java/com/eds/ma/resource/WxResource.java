package com.eds.ma.resource;

import com.eds.ma.bis.wx.JsAPISignature;
import com.eds.ma.bis.wx.WxTokenTypeEnum;
import com.eds.ma.bis.wx.entity.AccessToken;
import com.eds.ma.bis.wx.service.IWxService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.BizErrorConstants;
import com.eds.ma.rest.common.CommonConstants;
import com.eds.ma.util.CookieUtils;
import com.eds.ma.util.HTTPUtil;
import com.xcrm.log.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信处理
 * @author gaoyan
 * @Date: 2018/2/11
 */
@Path("/wx")
public class WxResource {
	
	private static Logger logger = Logger.getLogger(WxResource.class);

	@Autowired
	private IWxService wxService;

	@Autowired
	private SysConfig sysConfig;


	/**
	 * 微信授权回调
	 * @param code
	 * @param state
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@GET
	@Path("/auth/callback")
	@Produces(MediaType.APPLICATION_JSON)
	public void wxRedirect(@QueryParam("code") String code, @QueryParam("state") String state, @Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
		logger.info("WxResource.wxRedirect({},{})",code,state);
		if(StringUtils.isNotBlank(code) && StringUtils.isNotBlank(state)) {
			String redirectUrl = String.valueOf(state);
			//通过回调获取的code,获取授权的accessToken和openId
			Map<String,Object> outhTokenParaMap = new HashMap<>();
			outhTokenParaMap.put("appid",sysConfig.getWxAppId());
			outhTokenParaMap.put("code",code);
			outhTokenParaMap.put("grant_type","authorization_code");
			outhTokenParaMap.put("secret",sysConfig.getWxAppSecret());
			Map<String,Object> resultMap = HTTPUtil.sendGet(sysConfig.getWxAuthTokenUrl(),outhTokenParaMap);

			if(MapUtils.isNotEmpty(resultMap)){
				logger.info("WxResource.wxRedirect resultMap:" + resultMap);
				if(StringUtils.isNotEmpty(MapUtils.getString(resultMap,"errmsg"))){
					String errmsg = MapUtils.getString(resultMap,"errmsg","获取授权信息失败");
					logger.error("WxResource.wxRedirect get token error :" + errmsg);
					throw new BizCoreRuntimeException(BizErrorConstants.WX_AUTH_TOKEN_ERROR);
				}else{
					String openId = MapUtils.getString(resultMap,"openid");

					if(StringUtils.isNotBlank(openId)) {
						logger.info("get openId from wx and save it to cookie, openId is ={}" ,openId);
						CookieUtils.addCookie(request, response, CommonConstants.WX_OPEN_ID_COOKIE
								, openId, null, sysConfig.getEdsCookieHost());
						logger.info("WxResource reirect url:" + redirectUrl);
						response.sendRedirect(response.encodeRedirectURL(redirectUrl));
					}
				}
			}
		}
	}

	/**
	 * 获取微信jssdk配置信息
	 * @param currentUrl
	 * @return
	 */
	@GET
	@Path("/jssdk_config")
	@Produces(MediaType.APPLICATION_JSON)
	public JsAPISignature queryJsSdkConfig(@QueryParam("currentUrl") String currentUrl) {
		logger.info("WxResource.queryJsSdkConfig({})",currentUrl);
		try {
			return wxService.getShareConfigInfo(currentUrl);
		} catch (Exception e) {
			logger.error("queryJsSdkConfig occurs exception ",e);
			throw new BizCoreRuntimeException(BizErrorConstants.WX_JSDK_CONFIG_ERROR);
		}
	}
}