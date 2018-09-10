package com.eds.ma.resource;

import com.eds.ma.bis.user.service.IUserReserveService;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.resource.request.SendReserveSmsCodeRequest;
import com.eds.ma.resource.request.UserReserveRequest;
import com.eds.ma.rest.integration.annotation.NoAuth;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 用户资源
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Path("/user")
public class UserReserveResource extends BaseAuthedResource{
	
	private static Logger logger = Logger.getLogger(UserReserveResource.class);

	@Autowired
	private IUserReserveService userReserveService;

    /**
     * 发送用户设备预约短信验证码
     * @param request
     * @return
     */
    @POST
    @Path("/reserve/sms_code")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NoAuth
    public Response sendUserReserveSmsCode(@Valid SendReserveSmsCodeRequest request){
        logger.debug("UserReserveResource.sendUserReserveSmsCode({})",request);
        userReserveService.sendUserReserveSmsCode(request.getMobile(),request.getSpId());
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * 用户设备预约
     * @param request
     * @return
     */
    @POST
    @Path("/reserve/confirm")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NoAuth
    public Response userReserveConfirm(@Valid UserReserveRequest request){
        logger.debug("UserReserveResource.userReserveConfirm({})",request);
        userReserveService.userReserveConfirm(request.getSpId(),request.getMobile(),request.getSmsCode());
        return Response.status(Response.Status.CREATED).build();
    }


}