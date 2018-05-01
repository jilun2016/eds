package com.eds.ma.rest.integration;

import com.eds.ma.bis.user.entity.User;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.rest.common.CommonConstants;
import com.eds.ma.rest.common.ErrorMessage;
import com.eds.ma.rest.common.RestErrorCode;
import com.eds.ma.util.CookieUtils;
import com.xcrm.log.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Objects;


/**
 * 微信授权验证
 * @Author gaoyan
 * @Date: 2018/2/10
 */
public class AuthoricationDevFilter implements ContainerRequestFilter,ContainerResponseFilter {
	
	private static Logger logger = Logger.getLogger(AuthoricationDevFilter.class);

    @Autowired
    private IUserService userService;

	@Override
	public void filter(ContainerRequestContext requestContext) {
       String openId = "oiyZc5Qn8pe8wnO_BDl142Ozj6eE";
        User user = userService.queryUserByOpenId(openId);
        if (Objects.isNull(user) || Objects.isNull(user.getId())) {
            requestContext.abortWith(buildErrorMessageResponse(RestErrorCode.WX_AUTH_USER_INFO_ERROR));
            return;
        }
        requestContext.setProperty(CommonConstants.EDS_USER, user);
        requestContext.setProperty(CommonConstants.WX_OPEN_ID_COOKIE, openId);
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

	}

    /**
     * 构造错误信息
     * @param errorCode
     * @return
     */
    private Response buildErrorMessageResponse(RestErrorCode errorCode) {
        return new ErrorMessage(errorCode.code(), errorCode.reason()).buildResponse(Response.Status.UNAUTHORIZED);
    }

}
