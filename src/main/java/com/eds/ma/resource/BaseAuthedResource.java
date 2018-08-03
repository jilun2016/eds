package com.eds.ma.resource;


import com.eds.ma.bis.user.vo.ContextUser;
import com.eds.ma.rest.common.CommonConstants;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;

/**
 * 授权登录后 资源基类
 * @author Brian
 *
 */
public class BaseAuthedResource {
	
	@Context
    private ContainerRequestContext containerRequestContext;
	
	public String getOpenId() {
		return (String) containerRequestContext.getProperty(CommonConstants.WX_OPEN_ID_COOKIE);
	}

	public String getAliUid() {
		return (String) containerRequestContext.getProperty(CommonConstants.ALI_UID_COOKIE);
	}

	public Long getUserId() {
		return getUser().getUserId();
	}

	public String getAppId() {
		return (String) containerRequestContext.getProperty(CommonConstants.HTTP_HEADER_APP_ID);
	}

	public ContextUser getUser() {
		return (ContextUser) containerRequestContext.getProperty(CommonConstants.EDS_USER);
	}

}
