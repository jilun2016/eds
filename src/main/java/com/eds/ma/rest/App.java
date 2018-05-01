package com.eds.ma.rest;

import com.eds.ma.rest.integration.AuthoricationFilterFeature;
import com.eds.ma.rest.integration.BizExceptionMapper;
import com.eds.ma.rest.integration.NotFoundExceptionMapper;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Set;

public class App extends ResourceConfig {

    public App() {
        //构造函数，在这里注册需要使用的内容，（过滤器，拦截器，API等）
        // 向jersey框架注册资源类，凡完全限定名是以指定字符串开头的类，都将包含  ,"com.xcrm.rest.integration"
        scan("com.eds.ma.resource");
        register(BizExceptionMapper.class);
        register(NotFoundExceptionMapper.class);
        register(JacksonFeature.class);
        register(AuthoricationFilterFeature.class);
        Set<Class<?>> resources = new HashSet<>();
        registerClasses(resources);
    }

    private void scan(String... packages) {
        for (String pack : packages) {
            Reflections reflections = new Reflections(pack);
            reflections.getTypesAnnotatedWith(Path.class).parallelStream().forEach(this::register);
        }
    }
}
