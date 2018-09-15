package com.eds.ma.socket.util;

import com.eds.ma.socket.SocketConstants;
import com.xcrm.common.util.ListUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

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

	public static byte[] L2Bytes(long x,int size) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(0, x);
		byte[] bytes =  buffer.array();
		byte[] bts  = new byte[size];
		for (int i = size - 1; i >= 0; i--) {
			bts[i] = bytes[bytes.length - size + i];
		}
		return bts;
	}

	public static long Bytes2L(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();//need flip
		return buffer.getLong();
	}

    public static byte[] HBytes(String... hexStringArray) {
	    byte[] resultByteArray = null;
	    if(hexStringArray !=null && hexStringArray.length > 0){
            for (String hexString : hexStringArray) {
                hexString = hexString.replaceAll("^0[x|X]", "");
                int len = hexString.length();
                byte[] data = new byte[len / 2];
                try {
                    for (int i = 0; i < len; i += 2) {
                        data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                                + Character.digit(hexString.charAt(i+1), 16));
                    }
                } catch (Exception e) {
                    //Log.d("", "Argument(s) for hexStringToByteArray(String s)"+ "was not a hex string");
                }
                resultByteArray = combineBytes(resultByteArray,data);
            }
        }

        return resultByteArray;
    }

	public static byte[] buildZeroBytes(int size) {
		byte[] emptyBytes = new byte[size];
		for (int i = 0; i < emptyBytes.length; i++) {
			emptyBytes[i] = 0;
		}
		return emptyBytes;
	}

	public static byte[] combineBytes(byte[]... bytes){

		int combineByteLength = 0;
		for (byte[] aByte : bytes) {
		    if(aByte != null){
                combineByteLength += aByte.length;
            }

		}
		byte[] combineBytes = new byte[combineByteLength];
		int stepByteLength = 0;
		for (byte[] aByte : bytes) {
            if(aByte != null){
                for (int i = 0; i < aByte.length; i++) {
                    combineBytes[i+stepByteLength] = aByte[i];
                }
                stepByteLength += aByte.length;
            }
		}
		return combineBytes;
	}

	public static byte[] buildMessageCheckByte(Long headSum,Long... messageValues){
		Long checkByteSum = headSum;
		for (Long messageValue : messageValues) {
			checkByteSum +=messageValue;
		}
		Long  xorValue = checkByteSum^SocketConstants.XOR_CHECK_CODE;
		//将异或值转换成1个字节
		return SocketMessageUtils.L2Bytes(xorValue,1);
	}

	public static void main(String[] args) {
		System.out.println(L2Bytes(14131905958052100L,0));

        HBytes("A8986043323182000347");
	}
}
