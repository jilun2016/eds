package com.eds.ma.resource;

import com.eds.ma.bis.user.vo.UserInfoVo;
import com.eds.ma.bis.wx.service.IWxMaService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.rest.integration.annotation.NoAuth;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
			@NotNull(message = "加密算法的初始向量不允许为空") @QueryParam("iv") String iv) {

		logger.debug("----WxMaResource.queryMaSession({},{},{})",code,encryptedData,iv);
		return wxMaService.queryMaUserInfo(code,encryptedData,iv);
	}



}