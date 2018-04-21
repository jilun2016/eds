package com.eds.ma.rest.integration;

import com.eds.ma.config.SysConfig;
import com.eds.ma.rest.common.ErrorMessage;
import com.eds.ma.rest.common.RestErrorCode;
import com.eds.ma.util.ErrorCodeMessageUtil;
import com.xcrm.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;


/**
 * 微信授权验证
 * @Author gaoyan
 * @Date: 2018/2/10
 */
public class AuthoricationFilter implements ContainerRequestFilter,ContainerResponseFilter {
	
	private static Logger logger = Logger.getLogger(AuthoricationFilter.class);

	@Autowired
	private SysConfig sysConfig;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

	}

}
