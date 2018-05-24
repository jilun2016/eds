package com.eds.ma.socket;

import com.eds.ma.socket.handler.ServerHandler;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class MinaTcpServer {

    protected Logger log = LoggerFactory.getLogger(MinaTcpServer.class);


    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    @Bean
    public ServerHandler serviceHandler() {
        return new ServerHandler();
    }

    @Bean(initMethod = "bind",destroyMethod = "unbind")
    public NioSocketAcceptor nioSocketAcceptor() {
        NioSocketAcceptor nioSocketAcceptor = new NioSocketAcceptor();
        nioSocketAcceptor.setDefaultLocalAddress(new InetSocketAddress(9000));
        nioSocketAcceptor.setReuseAddress(true);
        nioSocketAcceptor.setHandler(serviceHandler());
        nioSocketAcceptor.setFilterChainBuilder(filterChainBuilder());
        return nioSocketAcceptor;
    }

    @Bean
    public ProtocolCodecFilter protocolCodecFilter() {
        return new ProtocolCodecFilter(new HCoderFactory());
    }

    @Bean
    public DefaultIoFilterChainBuilder filterChainBuilder() {
        DefaultIoFilterChainBuilder defaultIoFilterChainBuilder = new DefaultIoFilterChainBuilder();
        Map<String,IoFilter> filters = new LinkedHashMap<>();
        filters.put("codec",protocolCodecFilter());
        filters.put("logger",loggingFilter());
        defaultIoFilterChainBuilder.setFilters(filters);
        return defaultIoFilterChainBuilder;
    }

}