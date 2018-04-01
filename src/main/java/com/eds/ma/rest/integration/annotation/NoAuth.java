package com.eds.ma.rest.integration.annotation;

import java.lang.annotation.*;

/**
 * 资源方法不需要授权访问
 * @Author gaoyan
 * @Date: 2018/3/31
 */
@Inherited
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoAuth {
	
}
