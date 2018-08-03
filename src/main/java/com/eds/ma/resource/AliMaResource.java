package com.eds.ma.resource;

import com.eds.ma.bis.common.EdsAppId;
import com.eds.ma.bis.user.vo.AliUserInfoVo;
import com.eds.ma.bis.user.vo.UserInfoVo;
import com.eds.ma.bis.wx.service.IAliMaService;
import com.eds.ma.bis.wx.service.IWxMaService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.resource.request.AuthorizationRequest;
import com.eds.ma.resource.request.UserPhoneRequest;
import com.eds.ma.rest.common.CommonConstants;
import com.eds.ma.rest.integration.annotation.NoAuth;
import com.eds.ma.util.CookieUtils;
import com.xcrm.log.Logger;
import org.apache.commons.lang.StringUtils;
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
 * 支付宝小程序处理
 * @Author gaoyan
 * @Date: 2018/7/17
 */
@Path("/ali/ma")
public class AliMaResource extends BaseAuthedResource {
	
	private static Logger logger = Logger.getLogger(AliMaResource.class);

	@Autowired
	private IAliMaService aliMaService;

	@Autowired
	private SysConfig sysConfig;


	/**
	 * 获取小程序用户信息
	 * @param code 登陆code
	 */
	@GET
	@Path("/auth/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	@NoAuth
	public AliUserInfoVo queryAliUserInfo(@NotNull(message = "授权code不允许为空") @PathParam("code") String code,
										  @Context HttpServletRequest request, @Context HttpServletResponse response) {

		logger.debug("----AliMaResource.queryAliUserInfo({})",code);
		return aliMaService.queryAliUserInfo(code);
	}

	/**
	 * 支付宝小程序登陆
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
	public Response authorization(@Valid AuthorizationRequest req,
								  @Context HttpServletRequest request,
								  @Context HttpServletResponse response) {

		logger.debug("AliMaResource.authorization.params:{}", req);
		aliMaService.aliMaLogin(EdsAppId.eds_ali.value(),req.getAliUid(),req.getMobile(),req.getSmsCode());
		CookieUtils.addCookie(request,response,  CommonConstants.ALI_UID_COOKIE, req.getAliUid(),
				null, sysConfig.getEdsAliCookieHost());
		Map<String,String> resultMap = new HashMap<>();
		resultMap.put("aliUid",req.getAliUid());
		return Response.status(Response.Status.CREATED).entity(resultMap).build();
	}



}