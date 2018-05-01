package com.eds.ma.rest.integration;

import com.eds.ma.config.SysConfig;
import com.eds.ma.rest.integration.annotation.NoAuth;
import com.eds.ma.util.SystemProfileEnum;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.util.Objects;


/**
 * 启动时确定哪些资源需要授权访问
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public class AuthoricationFilterFeature implements DynamicFeature {

	@Autowired
	private SysConfig sysConfig;

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {

			NoAuth isNoAuth = resourceInfo.getResourceMethod().getAnnotation(NoAuth.class);
			if(Objects.isNull(isNoAuth)){
				if(Objects.equals(sysConfig.getProjectProfile(),SystemProfileEnum.PRODUCT.value())){
					context.register(AuthoricationFilter.class);
				}else{
					context.register(AuthoricationDevFilter.class);
				}
			}
	}
}
