package com.eds.ma.socket;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.eds.ma.socket.vo.DeviceDataVo;
import com.xcrm.common.util.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.springframework.scheduling.annotation.Async;


/**
 * @Description: 单例工具类，保存所有mina客户端连接
 * @author whl
 * @date 2014-9-29 上午10:09:15
 *
 */
public class SessionMap {
	
	private final static Log log = LogFactory.getLog(SessionMap.class);
	
	private static SessionMap sessionMap = null;
			
	private Map<String, IoSession>map = new HashMap<String, IoSession>();
	
	
	//构造私有化 单例
	private SessionMap(){}
	
	
	/**
	 * @Description: 获取唯一实例
	 * @author whl
	 * @date 2014-9-29 下午1:29:33
	 */
	public static SessionMap newInstance(){
		log.debug("SessionMap单例获取---");
		if(sessionMap == null){
			sessionMap = new SessionMap();
		}
		return sessionMap;
	}
	
	
	/**
	 * @Description: 保存session会话
	 * @author whl
	 * @date 2014-9-29 下午1:31:05
	 */
	public void addSession(String key, IoSession session){
		log.debug("保存会话到SessionMap单例---key=" + key);
		this.map.put(key, session);
	}
	
	/**
	 * @Description: 根据key查找缓存的session
	 * @author whl
	 * @date 2014-9-29 下午1:31:55
	 */
	public IoSession getSession(String key){
		log.debug("获取会话从SessionMap单例---key=" + key);
		return this.map.get(key);
	}
	
	/**
	 * @Description: 发送消息到客户端
	 * @author whl
	 * @date 2014-9-29 下午1:57:51
	 */
	public void sendMessage(String[] keys, Object message){
		for(String key : keys){
			IoSession session = getSession(key);
			
			log.debug("反向发送消息到客户端Session---key=" + key + "----------消息=" + message);
			if(session == null){
				return;
			}
			session.write(message);
			
		}
	}

	/**
	 * @Description: 发送消息到客户端
	 * @author whl
	 * @date 2014-9-29 下午1:57:51
	 */
	public DeviceDataVo sendDevcieStatusMessage(String key,Long originDeviceId,Integer lockStatus){
		IoSession session = getSession(key);

		log.debug("反向发送消息到客户端Session---key=" + key);
		if(session == null){
			return null;
		}
		String deviceBinaryValue = Long.toBinaryString(originDeviceId);
		if(deviceBinaryValue.length()<32){
			int length = deviceBinaryValue.length();
			for(int i=0;i<length;i++){
				deviceBinaryValue = "0" + deviceBinaryValue;
			}
		}
		byte[] deviceBytes = new byte[4];
		String deviceValueStr = deviceBinaryValue.substring(0,8);
		String deviceHexValue = Integer.toString (Integer.parseInt (deviceValueStr, 2), 16);
		deviceBytes[0] = (byte) Integer.valueOf(deviceHexValue).intValue();

		deviceValueStr = deviceBinaryValue.substring(8,16);
		deviceHexValue = Integer.toString (Integer.parseInt (deviceValueStr, 2), 16);
		deviceBytes[1] = (byte) Integer.valueOf(deviceHexValue).intValue();

		deviceValueStr = deviceBinaryValue.substring(16,24);
		deviceHexValue = Integer.toString (Integer.parseInt (deviceValueStr, 2), 16);
		deviceBytes[2] = (byte) Integer.valueOf(deviceHexValue).intValue();

		deviceValueStr = deviceBinaryValue.substring(24,32);
		deviceHexValue = Integer.toString (Integer.parseInt (deviceValueStr, 2), 16);
		deviceBytes[3] = (byte) Integer.valueOf(deviceHexValue).intValue();

		byte[] temp = new byte[9];
		temp[0] =  18;
		temp[1] =  (byte) lockStatus.intValue();
		temp[2] =  4;
		temp[3] =  deviceBytes[0];
		temp[4] =  deviceBytes[1];
		temp[5] =  deviceBytes[2];
		temp[6] =  deviceBytes[3];
		//对字节求和
		Long byteSum =  Long.parseLong(String.valueOf(temp[1]),16);
		byteSum += Long.parseLong(String.valueOf(temp[2]),16);
		byteSum += Long.parseLong(String.valueOf(temp[3])+String.valueOf(temp[4])+String.valueOf(temp[5])+String.valueOf(temp[6]),16);
		String binaryValue = Long.toBinaryString(byteSum);
		//取最后八位作为校验码字节
		String checkTenValueStr = binaryValue.substring(binaryValue.length() - 8);
		String checkHexValue = Integer.toString (Integer.parseInt (checkTenValueStr, 2), 16);
		temp[7] = (byte) Integer.valueOf(checkHexValue).intValue();
		temp[8] =  38;

		DeviceDataVo deviceDataVo = new DeviceDataVo();
		deviceDataVo.setCreated(DateFormatUtils.getNow());
		deviceDataVo.setDeviceFlowType(SocketConstants.DEVICE_FLOW_SERVER_SEND);
		deviceDataVo.setDeviceType(Long.valueOf(lockStatus));
		deviceDataVo.setDeviceLength(4L);
		deviceDataVo.setDeviceId(originDeviceId);

		IoBuffer buffer = IoBuffer.allocate(9);
		// 自动扩容
		buffer.setAutoExpand(true);
		// 自动收缩
		buffer.setAutoShrink(true);
		buffer.put(temp);
		buffer.flip();
		session.write(buffer);
		return deviceDataVo;
	}

}
