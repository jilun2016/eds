package com.eds.ma.netty;

import com.eds.ma.bis.user.service.UserServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientHandler extends SimpleChannelInboundHandler<Object> {

    protected Logger log = LoggerFactory.getLogger(TcpClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("client接收到服务器返回的消息:" + msg);
    }
}