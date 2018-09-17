package com.eds.ma.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerHandler extends SimpleChannelInboundHandler<Object> {

    protected Logger log = LoggerFactory.getLogger(TcpClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("SERVER接收到消息:" + msg);
        ChannelMap.addChannel("test",ctx.channel());
        ctx.channel().writeAndFlush("server accepted msg:" + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("exceptionCaught!", cause);
        ctx.close();
    }
}