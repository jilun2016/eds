package com.eds.ma.resource;

import com.eds.ma.bis.common.service.IEdsConfigService;
import com.eds.ma.bis.coupon.service.ICouponService;
import com.eds.ma.bis.device.service.IDeviceService;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.resource.request.DeviceRentRequest;
import com.eds.ma.resource.request.DeviceReturnRequest;
import com.eds.ma.resource.request.PageRequest;
import com.eds.ma.rest.integration.annotation.NoAuth;
import com.xcrm.common.page.Pagination;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备资源
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Path("/user/dist")
public class UserDistResource extends BaseAuthedResource{
	
	private static Logger logger = Logger.getLogger(UserDistResource.class);

	@Autowired
	private IUserService userService;

	@Autowired
	private ICouponService couponService;

	/**
	 * 发起分销
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveUserDist() {
		logger.debug("UserDistResource.saveUserDist({})",super.getOpenId());
        Long distId = userService.saveUserDist(super.getOpenId());
        Map<String,Long> distMap = new HashMap<>();
        distMap.put("distId",distId);
		return Response.status(Response.Status.CREATED).entity(distMap).build();
	}

	/**
	 * 分销绑定
	 */
	@POST
	@Path("/{distId}/bind")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response shareBindUserDist(@NotNull(message = "分享ID不允许为空") @PathParam("distId") Long distId) {
		logger.debug("UserDistResource.shareBindUserDist({},{})",super.getOpenId(),distId);
		userService.shareBindUserDist(distId,super.getOpenId());
		return Response.status(Response.Status.CREATED).build();
	}

	/**
	 * 分销的优惠券
	 */
	@POST
	@Path("/coupon")
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveUserDistCoupon() {
		logger.debug("UserResource.saveUserDistCoupon({},{})",super.getOpenId());
		couponService.saveUserDistCoupon(super.getUser().getId(),super.getOpenId());
		return Response.status(Response.Status.CREATED).build();
	}

	/**
	 * 查询分享的接收用户的信息
	 */
	@GET
	@Path("/detail")
	@Produces(MediaType.APPLICATION_JSON)
	public Pagination queryUserShareCouponDetail(@Valid @BeanParam PageRequest pageRequest) {
		logger.debug("UserResource.saveUserDistCoupon({},{})",super.getOpenId());
		return couponService.queryUserShareCouponDetail(super.getOpenId(),pageRequest.getPageNo(),pageRequest.getPageSize());
	}

}