package com.eds.ma.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringUtils implements ApplicationContextAware
{
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
	{
		SpringUtils.applicationContext = applicationContext;
	}

	public static <T> T getBean(String name)
	{
		checkApplicationContext();
		return (T)applicationContext.getBean(name);
	}

	public static <T> T getBean(Class<T> clazz)
	{
		checkApplicationContext();
		return (T)applicationContext.getBeansOfType(clazz);
	}

	private static void checkApplicationContext() {
		if (applicationContext == null) {
			throw new IllegalStateException("applicaitonContext未注入");
		}
	}
}
