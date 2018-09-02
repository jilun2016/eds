package com.eds.ma.resource;

import com.eds.ma.bis.common.EdsAppId;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.user.vo.AliUserInfoVo;
import com.eds.ma.bis.wx.service.IAliMaService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.resource.request.SendSmsCodeRequest;
import com.eds.ma.rest.common.BizErrorConstants;
import com.eds.ma.rest.integration.annotation.NoAuth;
import com.xcrm.log.Logger;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

/**
 * 支付宝小程序处理
 * @Author gaoyan
 * @Date: 2018/7/17
 */
@Path("/oauth")
public class MaAuthResource extends BaseAuthedResource {
	
	private static Logger logger = Logger.getLogger(MaAuthResource.class);

	@Autowired
	private IUserService userService;

	/**
	 * 发送短信验证码
	 *
	 * @param req
	 * @return
	 */
	@POST
	@Path("/sms_code")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NoAuth
	public Response sendSmsCode(@Valid SendSmsCodeRequest req, @Context HttpServletRequest request) {
		logger.info("MaAuthResource.sendSmsCode,params:{})", req);
		if(!Objects.equals(EdsAppId.eds_wx.value(),req.getAppId())
				&& !Objects.equals(EdsAppId.eds_ali.value(),req.getAppId())){
			throw new BadRequestException("appId无效");
		}
		userService.saveUserForRegist(req.getAppId(),req.getMobile());
		return Response.status(Response.Status.CREATED).build();
	}


}