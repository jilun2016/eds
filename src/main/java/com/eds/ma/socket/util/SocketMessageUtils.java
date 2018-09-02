package com.eds.ma.socket.util;

import java.nio.ByteBuffer;

public class SocketMessageUtils {

	public static Long B2L(String binaryMessage){
		return Long.parseLong(binaryMessage,2);
	}

	public static Long B2L(String binaryMessage,int fromIndex,int offset){
		if(binaryMessage.length() >= (fromIndex + offset)){
			String subMessage = binaryMessage.substring(fromIndex,fromIndex+offset);
			return B2L(subMessage);
		}
		return null;
	}

	public static Long H2L(String hexMessage){
		return Long.parseLong(hexMessage.replaceAll("^0[x|X]", ""),16);
	}

	public static String H2S(String[] mesasge,int fromIndex,int offset){
		if(mesasge.length >= (fromIndex + offset)){
			StringBuilder hexMessage = new StringBuilder();
			for (int i = fromIndex; i < (fromIndex + offset); i++) {
				hexMessage.append(mesasge[i]);
			}
			return hexMessage.toString();
		}
		return null;
	}

	public static Long H2L(String[] mesasge,int fromIndex,int offset){
		if(mesasge.length >= (fromIndex + offset)){
			StringBuilder hexMessage = new StringBuilder();
			for (int i = fromIndex; i < (fromIndex + offset); i++) {
				hexMessage.append(mesasge[i]);
			}
			return H2L(hexMessage.toString());
		}
		return null;
	}

	public static String H2C(String hexMessage){
		int messageInt = Integer.parseInt(hexMessage.replaceAll("^0[x|X]", ""),16);
		return String.valueOf((char)messageInt);
	}

	/**
	 * 将十六进制的字符串转换成二进制的字符串
	 *
	 * @param hexMessage
	 * @return
	 */
	public static String H2B(String hexMessage) {

		if (hexMessage == null || "".equals(hexMessage)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		// 将每一个十六进制字符分别转换成一个四位的二进制字符
		for (int i = 0; i < hexMessage.length(); i++) {
			String indexStr = hexMessage.substring(i, i + 1);
			StringBuilder binaryStr = new StringBuilder(Long.toBinaryString(H2L(indexStr)));
			while (binaryStr.length() < 4) {
				binaryStr.insert(0, "0");
			}
			sb.append(binaryStr);
		}
		return sb.toString();
	}

	/**
	 * 十进制数字转换16进制字符串
	 * @param decMessage
	 * @return
	 */
	public static String D2H(Long decMessage){
		return Long.toHexString(decMessage);
	}

	public static byte[] L2Bytes(long x,int capacity) {
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		buffer.putLong(0, x);
		return buffer.array();
	}

	public static long Bytes2L(byte[] bytes,int capacity) {
		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();//need flip
		return buffer.getLong();
	}
}
