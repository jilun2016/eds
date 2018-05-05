package com.eds.ma.resource;

import com.eds.ma.bis.common.service.IEdsConfigService;
import com.eds.ma.bis.device.service.IDeviceService;
import com.eds.ma.bis.device.vo.UserDeviceVo;
import com.eds.ma.bis.order.TransTypeEnum;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.user.vo.UserInfoVo;
import com.eds.ma.bis.user.vo.UserWalletVo;
import com.eds.ma.bis.wx.service.IWxPayService;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.resource.request.BalancePayRequest;
import com.eds.ma.resource.request.SendSmsCodeRequest;
import com.eds.ma.resource.request.UserWithdrawRequest;
import com.eds.ma.rest.common.BizErrorConstants;
import com.eds.ma.rest.common.CommonConstants;
import com.eds.ma.rest.integration.annotation.NoAuth;
import com.eds.ma.util.CookieUtils;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 用户资源
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Path("/user")
public class UserResource extends BaseAuthedResource{
	
	private static Logger logger = Logger.getLogger(UserResource.class);

	@Autowired
	private IUserService userService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IWxPayService wxPayService;

    @Autowired
    private IEdsConfigService edsConfigService;

    @Autowired
    private SysConfig sysConfig;


	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
    @NoAuth
	public Response test(@Context HttpServletRequest request, @Context HttpServletResponse response) throws InterruptedException, ExecutionException {
	    UserInfoVo userInfoVo = new UserInfoVo();
	    userInfoVo.setHeadimgurl("https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83epwsY5aWbnrFNJh7JZNLG9KGyRgYczicuiavQaU6BkkpdKm5lEb3MHiarUQDnGGZdyrgj94tdJ8EtwLA/0");
        userInfoVo.setNickName("高岩");
        userInfoVo.setOpenId("oiyZc5Qn8pe8wnO_BDl142Ozj6eE");
        CookieUtils.addCookie(request,response,  CommonConstants.WX_OPEN_ID_COOKIE, userInfoVo.getOpenId(),
                null, sysConfig.getEdsCookieHost());
        return Response.ok().build();
	}

    /**
     * 查询用户基础信息
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfoVo queryUserInfo() {
        logger.debug("UserResource.queryUserInfo({},{})", super.getOpenId(),super.getUser());
        User user = super.getUser();
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setHeadimgurl(user.getHeadimgurl());
        userInfoVo.setNickName(user.getNickname());
        userInfoVo.setOpenId(super.getOpenId());
        return userInfoVo;
    }

	/**
	 * 查询用户的租借中的设备信息
	 */
	@GET
	@Path("/devices")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserDeviceVo> queryUserDeviceList() {
		logger.debug("UserResource.queryUserWallet({},{})", super.getOpenId(),super.getUser());
        User user = super.getUser();
		return deviceService.queryUserDeviceList(user.getId());
	}

    /**
     * 查询用户钱包信息
     */
    @GET
    @Path("/wallet")
    @Produces(MediaType.APPLICATION_JSON)
    public UserWalletVo queryUserWallet() {
        logger.debug("UserResource.queryUserWallet({},{})", super.getOpenId(),super.getUser());
        return userService.queryUserWallet(super.getUser());
    }

    /**
     * 用户提现
     * 提现金额 = 余额+押金
     */
    @POST
    @Path("/wallet/withdraw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response walletWithdraw(@Valid UserWithdrawRequest request){
        logger.debug("UserResource.walletWithdraw({},{},{})",super.getOpenId(),super.getUser(),request);
        int result = userService.walletWithdraw(super.getUser(),request.getSmsCode());
        if(result == 0){
            throw new BizCoreRuntimeException(BizErrorConstants.WALLET_WITHDRAW_PART_ERROR);
        }
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * 发送用户提现短信验证码
     * @param request
     * @return
     */
    @POST
    @Path("/wallet/withdraw/sms_code")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendWithdrawSmsCode(@Valid SendSmsCodeRequest request){
        logger.debug("UserResource.sendWithdrawSmsCode({},{},{})",super.getOpenId(),super.getUser(),request);
        userService.sendWithdrawSmsCode(super.getUser(),request.getMobile());
        return Response.status(Response.Status.CREATED).build();
    }
}