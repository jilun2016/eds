package com.eds.ma.config;

import com.eds.ma.socket.ByteArrayCodecFactory;
import com.eds.ma.socket.HCoderFactory;
import com.eds.ma.socket.handler.ServerHandler;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.integration.spring.InetSocketAddressEditor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 内置服务server的相关配置
 *
 * @Author gaoyan
 * @Date: 2017/5/18
 */
@Configuration
public class SocketConfig {

    protected Logger log = LoggerFactory.getLogger(SocketConfig.class);

    @Bean
    public CustomEditorConfigurer customEditorConfigurer() {
        CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
        Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap<>();
        customEditors.put(SocketAddress.class,InetSocketAddressEditor.class);
        customEditorConfigurer.setCustomEditors(customEditors);
        return customEditorConfigurer;
    }

    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    @Bean
    public ExecutorFilter executorFilter() {
        return new ExecutorFilter();
    }

    @Bean
    public MdcInjectionFilter mdcInjectionFilter() {
        return new MdcInjectionFilter(MdcInjectionFilter.MdcKey.remoteAddress);
    }

    @Bean
    public ProtocolCodecFilter protocolCodecFilter() {
        return new ProtocolCodecFilter(new ByteArrayCodecFactory());
    }

    @Bean
    public ServerHandler serviceHandler() {
        return new ServerHandler();
    }

    @Bean
    public DefaultIoFilterChainBuilder filterChainBuilder() {
        DefaultIoFilterChainBuilder defaultIoFilterChainBuilder = new DefaultIoFilterChainBuilder();
        Map<String,IoFilter> filters = new LinkedHashMap<>();
        filters.put("codec",protocolCodecFilter());
        filters.put("logger",loggingFilter());
        filters.put("executor",executorFilter());
        filters.put("mdcInjectionFilter",mdcInjectionFilter());
        defaultIoFilterChainBuilder.setFilters(filters);
        return defaultIoFilterChainBuilder;
    }

    @Bean(initMethod = "bind",destroyMethod = "unbind")
    public NioSocketAcceptor nioSocketAcceptor() throws IOException {
        NioSocketAcceptor nioSocketAcceptor = new NioSocketAcceptor();
        nioSocketAcceptor.setReuseAddress(true);
        nioSocketAcceptor.setHandler(serviceHandler());
        List<InetSocketAddress> localAddresses = new ArrayList<>();
        localAddresses.add(new InetSocketAddress(9000));
        localAddresses.add(new InetSocketAddress(9001));
        localAddresses.add(new InetSocketAddress(9002));
        localAddresses.add(new InetSocketAddress(9003));
        nioSocketAcceptor.setDefaultLocalAddresses(localAddresses);
        nioSocketAcceptor.setFilterChainBuilder(filterChainBuilder());
        return nioSocketAcceptor;
    }





}