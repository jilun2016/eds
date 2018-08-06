package com.eds.ma.rest.integration;

import com.eds.ma.bis.common.EdsAppId;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.user.vo.ContextUser;
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
public class AuthoricationFilter implements ContainerRequestFilter,ContainerResponseFilter {
	
	private static Logger logger = Logger.getLogger(AuthoricationFilter.class);

    @Autowired
    private IUserService userService;

	@Override
	public void filter(ContainerRequestContext requestContext) {
	    //获取业务的appid
        String appId = requestContext.getHeaderString(CommonConstants.HTTP_HEADER_APP_ID);
        if(StringUtils.isBlank(appId) ||
                !(Objects.equals(EdsAppId.eds_wx.value(),appId) || Objects.equals(EdsAppId.eds_ali.value(),appId))) {
            requestContext.abortWith(buildErrorMessageResponse(RestErrorCode.HTTP_HEADER_FIELD_INVALID));
            return;
        }
        //如果是微信访问,那么读取openId
        if(Objects.equals(EdsAppId.eds_wx.value(),appId)){
            Cookie cookieFromOpenId = CookieUtils.getCookie(requestContext.getCookies(), CommonConstants.WX_OPEN_ID_COOKIE);
            //cookie中没有openid,需要重新认证处理
            if (Objects.isNull(cookieFromOpenId) || StringUtils.isBlank(cookieFromOpenId.getValue())) {
                logger.debug("AuthoricationFilter.cookieFromOpenId is null");
                requestContext.abortWith(buildErrorMessageResponse(RestErrorCode.WX_AUTH_USER_INFO_ERROR));
                return;
            }
            logger.debug("AuthoricationFilter.cookieFromOpenId({})",cookieFromOpenId.getValue());
            ContextUser contextUser = userService.queryUserByOpenId(cookieFromOpenId.getValue());
            logger.debug("AuthoricationFilter.contextUser({})",contextUser);
            if (Objects.isNull(contextUser) || Objects.isNull(contextUser.getUserId()) || Objects.isNull(contextUser.getMobile())) {
                requestContext.abortWith(buildErrorMessageResponse(RestErrorCode.WX_AUTH_USER_INFO_ERROR));
                return;
            }
            requestContext.setProperty(CommonConstants.EDS_USER, contextUser);
            requestContext.setProperty(CommonConstants.WX_OPEN_ID_COOKIE, cookieFromOpenId.getValue());
        }else{
            Cookie cookieFromAliUId = CookieUtils.getCookie(requestContext.getCookies(), CommonConstants.ALI_UID_COOKIE);
            //cookie中没有openid,需要重新认证处理
            if (Objects.isNull(cookieFromAliUId) || StringUtils.isBlank(cookieFromAliUId.getValue())) {
                logger.debug("AuthoricationFilter.cookieFromAliUId is null");
                requestContext.abortWith(buildErrorMessageResponse(RestErrorCode.WX_AUTH_USER_INFO_ERROR));
                return;
            }
            logger.debug("AuthoricationFilter.cookieFromAliUId({})",cookieFromAliUId.getValue());
            ContextUser contextUser = userService.queryUserByAliUid(cookieFromAliUId.getValue());
            logger.debug("AuthoricationFilter.contextUser({})",contextUser);
            if (Objects.isNull(contextUser) || Objects.isNull(contextUser.getUserId()) || Objects.isNull(contextUser.getMobile())) {
                requestContext.abortWith(buildErrorMessageResponse(RestErrorCode.WX_AUTH_USER_INFO_ERROR));
                return;
            }
            requestContext.setProperty(CommonConstants.EDS_USER, contextUser);
            requestContext.setProperty(CommonConstants.ALI_UID_COOKIE, cookieFromAliUId.getValue());
        }

        requestContext.setProperty(CommonConstants.HTTP_HEADER_APP_ID, appId);
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
