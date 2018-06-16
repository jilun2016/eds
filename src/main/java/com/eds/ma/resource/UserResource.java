package com.eds.ma.resource;

import com.eds.ma.bis.device.service.IDeviceService;
import com.eds.ma.bis.device.vo.UserDeviceVo;
import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.user.vo.UserInfoVo;
import com.eds.ma.bis.user.vo.UserWalletVo;
import com.eds.ma.config.SysConfig;
import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.redis.RedisDaoSupport;
import com.eds.ma.resource.request.SendSmsCodeRequest;
import com.eds.ma.resource.request.UserWithdrawRequest;
import com.eds.ma.rest.common.BizErrorConstants;
import com.eds.ma.rest.common.CommonConstants;
import com.eds.ma.rest.integration.annotation.NoAuth;
import com.eds.ma.socket.SessionMap;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private RedisDaoSupport redisDaoSupport;


	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
    @NoAuth
	public Response test(@QueryParam("message") String  message) {
//        SessionMap.newInstance().sendMessage(new String[]{"9000"},message);
        System.out.println(redisDaoSupport.get("ca_9150344668"));
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
     * 校验用户的押金是否满足租借条件
     */
    @GET
    @Path("/deposit/valid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkUserDeposit() {
        logger.debug("UserResource.checkUserDeposit({},{})", super.getOpenId(),super.getUser());
        Boolean isRentDepositValid = userService.checkUserRentDepositValid(super.getUser().getId());
        Map<String,Boolean> resultMap = new HashMap<>(1);
        resultMap.put("isRentDepositValid",isRentDepositValid);
        return Response.ok(resultMap).build();
    }

    /**
     * 查询用户租借次数
     */
    @GET
    @Path("/rent/times")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryUserRentTimes() {
        logger.debug("UserResource.queryUserRentTimes({},{})", super.getOpenId(),super.getUser());
        Integer rentTimes = userService.queryUserRentTimes(super.getUser().getId());
        Map<String,Integer> rentResult = new HashMap<>(1);
        rentResult.put("rentTimes",rentTimes);
        return Response.ok(rentResult).build();
    }

	/**
	 * 查询用户的租借中的设备信息
     * 店铺id
	 */
	@GET
	@Path("/devices")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserDeviceVo> queryUserDeviceList(@QueryParam("spId") Long spId) {
		logger.debug("UserResource.queryUserDeviceList({},{},{})",spId, super.getOpenId(),super.getUser());
        User user = super.getUser();
		return deviceService.queryUserDeviceList(user.getId(),null);
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
     * 查询用户是否已经提现认证过
     */
    @GET
    @Path("/wallet/withdraw/auth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryUserWalletAuth() {
        logger.debug("UserResource.queryUserWalletAuth({},{})", super.getOpenId(),super.getUser());
        Map<String,Boolean> authResult = new HashMap<>(1);
        authResult.put("isAuth",Objects.nonNull(super.getUser().getMobile()));
        return Response.ok(authResult).build();
    }

    /**
     * 用户提现
     * 提现金额 = 余额+押金
     */
    @POST
    @Path("/wallet/sms/withdraw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response walletSmsWithdraw(@Valid UserWithdrawRequest request){
        logger.debug("UserResource.walletWithdraw({},{},{})",super.getOpenId(),super.getUser(),request);
        int result = userService.walletWithdraw(super.getUser(),true,request.getSmsCode());
        if(result == 0){
            throw new BizCoreRuntimeException(BizErrorConstants.WALLET_WITHDRAW_PART_ERROR);
        }
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * 用户提现(使用微信一键认证方式|已经认证过)
     * 提现金额 = 余额+押金
     */
    @POST
    @Path("/wallet/auth/withdraw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response walletAuthWithdraw(){
        logger.debug("UserResource.walletAuthWithdraw({},{})",super.getOpenId(),super.getUser());
        int result = userService.walletWithdraw(super.getUser(),false,null);
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