package com.eds.ma;

import com.eds.ma.rest.App;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.sql.DataSource;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.eds.ma", "com.xcrm.cloud.database"})
@EnableScheduling
@EnableAsync
public class EdsApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(EdsApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(EdsApplication.class);
	}

	/**
	 * 自定义异步线程池
	 * @return
	 */
	@Bean(name = "taskExecutor")
	public AsyncTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setThreadNamePrefix("eds_thread_pool");
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(100);
		executor.setKeepAliveSeconds(300);

		// 设置拒绝策略
		executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				// .....
			}
		});
		// 使用预定义的异常处理类
		// executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		return executor;
	}

	/**
	 * 指定事务处理
	 * @param dataSource
	 * @return
	 */
	@Bean
	public PlatformTransactionManager txManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	/**
	 * 初始化jersey配置
	 * @return
	 */
	@Bean
	public ServletRegistrationBean jersetServlet(){
		ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/v1/*");
		// our rest resources will be available in the path /v1/*
		registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, App.class.getName());
		registration.addInitParameter("jersey.config.server.provider.classnames",
				"org.glassfish.jersey.filter.LoggingFilter;org.glassfish.jersey.media.multipart.MultiPartFeature");
		return registration;
	}


	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("OPTIONS");
		config.addAllowedMethod("HEAD");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("DELETE");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

}
