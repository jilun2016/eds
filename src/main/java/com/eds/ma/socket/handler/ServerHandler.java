package com.eds.ma.socket.handler;

import com.eds.ma.socket.SessionMap;
import com.xcrm.log.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.net.InetSocketAddress;


/**
 * @Description: mina服务端业务处理类
 * @author whl
 * @date 2014-9-30 下午12:36:28
 *
 */
public class ServerHandler extends IoHandlerAdapter {


	private static Logger logger = Logger.getLogger(ServerHandler.class);
	
	
	 public ServerHandler() {
	  // TODO Auto-generated constructor stub
	 }
	 @Override
	 public void exceptionCaught(IoSession session, Throwable cause)
	   throws Exception {
	 }
	 
	 
	 @Override
	 public void messageReceived(IoSession session, Object message)
	   throws Exception {
		 logger.debug("messageReceived({})",message);
		
		  //获取客户端发过来的key
		  String key = message.toString();
		  String carPark_id = key.substring(key.indexOf("=") + 1);
		 logger.debug("messageReceived({})",message);


		  //保存客户端的会话session
		  SessionMap sessionMap = SessionMap.newInstance();
		  sessionMap.addSession(String.valueOf(((InetSocketAddress)session.getLocalAddress()).getPort()), session);
	  
	 }
	 @Override
	 public void messageSent(IoSession session, Object message) throws Exception {
		 logger.debug("------------服务端发消息到客户端---");
	 }
	 @Override
	 public void sessionClosed(IoSession session) throws Exception {
	  // TODO Auto-generated method stub
		 logger.debug("远程session关闭了一个..." + session.getRemoteAddress().toString());
	 }
	 @Override
	 public void sessionCreated(IoSession session) throws Exception {
		 logger.debug(session.getRemoteAddress().toString() +"----------------------create");
	 }
	 @Override
	 public void sessionIdle(IoSession session, IdleStatus status)
	   throws Exception {
		 logger.debug(session.getServiceAddress() +"IDS");
	 }
	 @Override
	 public void sessionOpened(IoSession session) throws Exception {
		 logger.debug("连接打开："+session.getLocalAddress());
	 }

	}