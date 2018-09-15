package com.eds.ma.socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @Description: 单例工具类，保存所有mina客户端连接
 * @author whl
 * @date 2014-9-29 上午10:09:15
 *
 */
public class SessionClient {
	
	private final static Log log = LogFactory.getLog(SessionClient.class);
	
	private static Map<Long, IoSession> sessionMap = new HashMap<>();

	/**
	 * @Description: 保存session会话
	 * @author whl
	 * @date 2014-9-29 下午1:31:05
	 */
	public static void addSession(Long key, IoSession session){
		log.debug("保存会话到SessionMap单例---key=" + key);
		sessionMap.put(key, session);
	}
	
	/**
	 * @Description: 根据key查找缓存的session
	 * @author whl
	 * @date 2014-9-29 下午1:31:55
	 */
	public static IoSession getSession(Long key){
		log.debug("获取会话从SessionMap单例---key=" + key);
		return sessionMap.get(key);
	}

    /**
     * 发送消息到客户端
     * @param messageBytes
     */
    public static void test(byte[] messageBytes){
        Object[] longSet = sessionMap.keySet().toArray();
        IoSession session = getSession((Long) longSet[0]);
        log.debug("反向发送消息到客户端Session---key=" + "----------消息=" + messageBytes);
        log.debug("反向发送消息到客户端长度" + messageBytes.length);
        if(session == null){
            return;
        }
        IoBuffer buffer = IoBuffer.allocate(messageBytes.length);
//		// 自动扩容
//		buffer.setAutoExpand(true);
//		// 自动收缩
//		buffer.setAutoShrink(true);
        buffer.put(messageBytes);
        buffer.flip();
        session.write(buffer);
    }

	/**
	 * 发送消息到客户端
	 * @param key
	 * @param messageBytes
	 */
	public static void sendMessage(Long key, byte[] messageBytes){
		IoSession session = getSession(key);
		log.debug("反向发送消息到客户端Session---key=" + key + "----------消息=" + messageBytes);
        log.debug("反向发送消息到客户端长度" + messageBytes.length);
		if(session == null){
			return;
		}
		IoBuffer buffer = IoBuffer.allocate(messageBytes.length);
//		// 自动扩容
//		buffer.setAutoExpand(true);
//		// 自动收缩
//		buffer.setAutoShrink(true);
		buffer.put(messageBytes);
		buffer.flip();
		session.write(buffer);
	}

	/**
	 * 发送消息到客户端
	 * @param key
	 */
	public static void sendMessage(Long key, IoBuffer buffer){
		IoSession session = getSession(key);
		session.write(buffer);
	}

}
