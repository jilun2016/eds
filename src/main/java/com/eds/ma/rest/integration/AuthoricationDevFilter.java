package com.eds.ma.rest.integration;

import com.eds.ma.bis.common.EdsAppId;
import com.eds.ma.bis.user.service.IUserService;
import com.eds.ma.bis.user.vo.ContextUser;
import com.eds.ma.rest.common.CommonConstants;
import com.eds.ma.rest.common.ErrorMessage;
import com.eds.ma.rest.common.RestErrorCode;
import com.xcrm.log.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
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
        //获取业务的appid
        String appId = requestContext.getHeaderString(CommonConstants.HTTP_HEADER_APP_ID);
        if(StringUtils.isBlank(appId) ||
                !(Objects.equals(EdsAppId.eds_wx.value(),appId) || Objects.equals(EdsAppId.eds_ali.value(),appId))) {
            requestContext.abortWith(buildErrorMessageResponse(RestErrorCode.HTTP_HEADER_FIELD_INVALID));
            return;
        }
        //如果是微信访问,那么读取openId
        if(Objects.equals(EdsAppId.eds_wx.value(),appId)){
            String openId = "oiyZc5Y2cmsHPUeOBUufSrFSBn9E";
            ContextUser contextUser = userService.queryUserByOpenId(openId);
            logger.debug("AuthoricationFilter.contextUser({})",contextUser);
            if (Objects.isNull(contextUser) || Objects.isNull(contextUser.getUserId())) {
                requestContext.abortWith(buildErrorMessageResponse(RestErrorCode.WX_AUTH_USER_INFO_ERROR));
                return;
            }
            requestContext.setProperty(CommonConstants.EDS_USER, contextUser);
            requestContext.setProperty(CommonConstants.WX_OPEN_ID_COOKIE, openId);
        }else{
            String aliUid = "2088812431136221";
            ContextUser contextUser = userService.queryUserByAliUid(aliUid);
            logger.debug("AuthoricationFilter.contextUser({})",contextUser);
            if (Objects.isNull(contextUser) || Objects.isNull(contextUser.getUserId())) {
                requestContext.abortWith(buildErrorMessageResponse(RestErrorCode.WX_AUTH_USER_INFO_ERROR));
                return;
            }
            requestContext.setProperty(CommonConstants.EDS_USER, contextUser);
            requestContext.setProperty(CommonConstants.ALI_UID_COOKIE, aliUid);
        }
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext){

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
