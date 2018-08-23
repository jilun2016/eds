package com.eds.ma.socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

import java.util.HashMap;
import java.util.Map;


/**
 * @Description: 单例工具类，保存所有mina客户端连接
 * @author whl
 * @date 2014-9-29 上午10:09:15
 *
 */
public class SessionClient {
	
	private final static Log log = LogFactory.getLog(SessionClient.class);
	
	private static IoSession ioSession = null;

	/**
	 * @Description: 获取唯一实例
	 * @author whl
	 * @date 2014-9-29 下午1:29:33
	 */
	public static void init(IoSession session){
		SessionClient.ioSession = session;
	}
	
	/**
	 * @Description: 根据key查找缓存的session
	 * @author whl
	 * @date 2014-9-29 下午1:31:55
	 */
	public static IoSession getSession(){
		return ioSession;
	}
	
	/**
	 * @Description: 发送消息到客户端
	 * @author whl
	 * @date 2014-9-29 下午1:57:51
	 */
	public void sendMessage(Object message){
		IoSession session = getSession();
		log.debug("反向发送消息到客户端Session ---------消息=" + message);
		if(session == null){
			return;
		}
		session.write(message);
	}


}
