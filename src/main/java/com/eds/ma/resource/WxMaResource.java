package com.eds.ma.resource;

import com.eds.ma.bis.user.vo.UserInfoVo;
import com.eds.ma.bis.wx.service.IWxMaService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.resource.request.UserPhoneRequest;
import com.eds.ma.resource.request.WxAuthorizationRequest;
import com.eds.ma.rest.common.CommonConstants;
import com.eds.ma.rest.integration.annotation.NoAuth;
import com.eds.ma.util.CookieUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序处理
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Path("/wx/ma")
public class WxMaResource extends BaseAuthedResource {
	
	private static Logger logger = Logger.getLogger(WxMaResource.class);

	@Autowired
	private IWxMaService wxMaService;

	@Autowired
	private SysConfig sysConfig;

	/**
	 * 获取小程序用户信息
	 * @param code 登陆code
	 */
	@GET
	@Path("/session/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	@NoAuth
	public UserInfoVo queryMaUserInfo(@NotNull(message = "登录code不允许为空") @PathParam("code") String code,
			@NotNull(message = "加密数据不允许为空") @QueryParam("encryptedData") String encryptedData,
			@NotNull(message = "加密算法的初始向量不允许为空") @QueryParam("iv") String iv,
			@Context HttpServletRequest request, @Context HttpServletResponse response) {

		logger.debug("----WxMaResource.queryMaSession({},{},{})",code,encryptedData,iv);
		UserInfoVo userInfoVo = wxMaService.queryMaUserInfo(code,encryptedData,iv);
		CookieUtils.addCookie(request,response,  CommonConstants.WX_OPEN_ID_COOKIE, userInfoVo.getOpenId(),
				null, sysConfig.getEdsCookieHost());
		return userInfoVo;
	}

	/**
	 * 微信小程序登陆
	 * @param req
	 * @param request
	 * @param response
	 * @return
	 */
	@POST
	@Path("/authorization")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NoAuth
	public Response authorization(@Valid WxAuthorizationRequest req,
								  @Context HttpServletRequest request,
								  @Context HttpServletResponse response) {

		logger.debug("WxMaResource.authorization.params:{}", req);
		wxMaService.wxMaLogin(req.getOpenId(),req.getMobile(),req.getSmsCode());
		CookieUtils.addCookie(request,response,  CommonConstants.WX_OPEN_ID_COOKIE, req.getOpenId(),
				null, sysConfig.getEdsAliCookieHost());
		Map<String,String> resultMap = new HashMap<>();
		resultMap.put("openId",req.getOpenId());
		return Response.status(Response.Status.CREATED).entity(resultMap).build();
	}


	/**
	 * 保存用户手机号
	 */
	@Deprecated
	@POST
	@Path("/user/phone")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveUserPhone(@Valid UserPhoneRequest request){
		logger.debug("WxMaResource.saveUserPhone({},{},{})",super.getOpenId(),super.getUser(),request);
		wxMaService.saveUserPhone(super.getUserId(),request.getCode(),request.getEncryptedData(),request.getIv());
		return Response.status(Response.Status.CREATED).build();
	}



}