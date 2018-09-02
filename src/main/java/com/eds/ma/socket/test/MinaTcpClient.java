package com.eds.ma.socket.test;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;

import com.eds.ma.socket.util.SocketMessageUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
/**
 * @说明 Mina TCP客户端
 * @author 崔素强
 * @version 1.0
 * @since
 */
public class MinaTcpClient extends IoHandlerAdapter {
	private IoConnector connector;
	private static IoSession session;
	public MinaTcpClient() {
		connector = new NioSocketConnector();
		connector.setHandler(this);
		ConnectFuture connFuture = connector.connect(new InetSocketAddress("localhost", 9000));
//		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(),LineDelimiter.WINDOWS.getValue())));
		connFuture.awaitUninterruptibly();
		session = connFuture.getSession();
		System.out.println("TCP 客户端启动");
	}
	public static void main(String[] args) throws Exception {
		MinaTcpClient client = new MinaTcpClient();
		int b = 15;
		if(b == 1){
			heartBeat();
		}
		if(b == 2){
			register();
		}

		if(b == 3){
			report();
		}

		if(b == 4){
			gps();
		}

		if(b == 5){
			control();
		}

		byte[] bts = SocketMessageUtils.L2Bytes(14131905958052100L,8);
		IoBuffer buffer = IoBuffer.allocate(bts.length);
		// 自动扩容
		buffer.setAutoExpand(true);
		// 自动收缩
		buffer.setAutoShrink(true);
		buffer.put(bts);
		buffer.flip();
		session.write(buffer);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private static void heartBeat(){
		byte[] bts = new byte[12];
		bts[0] =  (byte) 0X21;
		bts[1] =  (byte) 0x32;
		bts[2] =  (byte) 0x34;
		bts[3] =  (byte) 0xe4;
		bts[4] =  (byte) 0xc2;
		bts[5] =  (byte) 0xa1;
		bts[6] =  (byte) 0x01;
		bts[7] =  (byte) 0x04;
		bts[8] =  (byte) 0xf2;
		bts[9] =  (byte) 0xff;
		bts[10] =  (byte) 0x04;
		bts[11] =  (byte) 0xf4;
		IoBuffer buffer = IoBuffer.allocate(12);
		// 自动扩容
		buffer.setAutoExpand(true);
		// 自动收缩
		buffer.setAutoShrink(true);
		buffer.put(bts);
		buffer.flip();
		session.write(buffer);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void register(){
		byte[] bts = new byte[24];
		bts[0] =  (byte) 0X21;
		bts[1] =  (byte) 0x32;
		bts[2] =  (byte) 0x34;
		bts[3] =  (byte) 0xe4;
		bts[4] =  (byte) 0xc2;
		bts[5] =  (byte) 0xa1;
		bts[6] =  (byte) 0x01;
		bts[7] =  (byte) 0x04;
		bts[8] =  (byte) 0x01;
		bts[9] =  (byte) 0x01;
		bts[10] =  (byte) 0x01;
		bts[11] =  (byte) 0x01;
		bts[12] =  (byte) 0xf1;
		bts[13] =  (byte) 0xff;
		bts[14] =  (byte) 0x04;
		bts[15] =  (byte) 0xf4;
		bts[16] =  (byte) 0xff;
		bts[17] =  (byte) 0x04;
		bts[18] =  (byte) 0xf4;
		bts[19] =  (byte) 0xff;
		bts[20] =  (byte) 0x04;
		bts[21] =  (byte) 0xf4;
		bts[22] =  (byte) 0x01;
		bts[23] =  (byte) 0x00;

		IoBuffer buffer = IoBuffer.allocate(24);
		// 自动扩容
		buffer.setAutoExpand(true);
		// 自动收缩
		buffer.setAutoShrink(true);
		buffer.put(bts);
		buffer.flip();
		session.write(buffer);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void report(){
		byte[] bts = new byte[35];
		bts[0] =  (byte) 0X21;
		bts[1] =  (byte) 0x32;
		bts[2] =  (byte) 0x34;
		bts[3] =  (byte) 0xe4;
		bts[4] =  (byte) 0xc2;
		bts[5] =  (byte) 0xa1;
		bts[6] =  (byte) 0x01;
		bts[7] =  (byte) 0x04;
		bts[8] =  (byte) 0x00;
		bts[9] =  (byte) 0x00;
		bts[10] =  (byte) 0x00;
		bts[11] =  (byte) 0x01;
		bts[12] =  (byte) 0xb3;
		bts[13] =  (byte) 0xbb;
		bts[14] =  (byte) 0xaa;
		bts[15] =  (byte) 0xdd;
		bts[16] =  (byte) 0xcc;
		bts[17] =  (byte) 0x1c;
		bts[18] =  (byte) 0x16;
		bts[19] =  (byte) 0x06;
		bts[20] =  (byte) 0x18;
		bts[21] =  (byte) 0x64;
		bts[22] =  (byte) 0x00;
		bts[23] =  (byte) 0x3f;

		bts[24] =  (byte) 0x02;
		bts[25] =  (byte) 0x01;
		bts[26] =  (byte) 0x32;
		bts[27] =  (byte) 0;
		bts[28] =  (byte) 0;
		bts[29] =  (byte) 0;
		bts[30] =  (byte) 0;
		bts[31] =  (byte) 0;
		bts[32] =  (byte) 0;
		bts[33] =  (byte) 0;
		bts[34] =  (byte) 0;

		IoBuffer buffer = IoBuffer.allocate(35);
		// 自动扩容
		buffer.setAutoExpand(true);
		// 自动收缩
		buffer.setAutoShrink(true);
		buffer.put(bts);
		buffer.flip();
		session.write(buffer);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void gps(){
		byte[] bts = new byte[35];
		bts[0] =  (byte) 0X21;
		bts[1] =  (byte) 0x32;
		bts[2] =  (byte) 0x34;
		bts[3] =  (byte) 0xe4;
		bts[4] =  (byte) 0xc2;
		bts[5] =  (byte) 0xa1;
		bts[6] =  (byte) 0x01;
		bts[7] =  (byte) 0x04;
		bts[8] =  (byte) 0x00;
		bts[9] =  (byte) 0x00;
		bts[10] =  (byte) 0x00;
		bts[11] =  (byte) 0x01;
		bts[12] =  (byte) 0xb2;
		bts[13] =  (byte) 0x32;
		bts[14] =  (byte) 0x34;
		bts[15] =  (byte) 0x32;
		bts[16] =  (byte) 0x36;
		bts[17] =  (byte) 0x2E;
		bts[18] =  (byte) 0x30;
		bts[19] =  (byte) 0x30;
		bts[20] =  (byte) 0x30;
		bts[21] =  (byte) 0x30;
		bts[22] =  (byte) 0x4E;
		bts[23] =  (byte) 0x31;

		bts[24] =  (byte) 0x31;
		bts[25] =  (byte) 0x38;
		bts[26] =  (byte) 0x30;
		bts[27] =  (byte) 0x34;
		bts[28] =  (byte) 0x2E;
		bts[29] =  (byte) 0x30;
		bts[30] =  (byte) 0x30;
		bts[31] =  (byte) 0x30;
		bts[32] =  (byte) 0x30;
		bts[33] =  (byte) 0x45;
		bts[34] =  (byte) 0;

		IoBuffer buffer = IoBuffer.allocate(35);
		// 自动扩容
		buffer.setAutoExpand(true);
		// 自动收缩
		buffer.setAutoShrink(true);
		buffer.put(bts);
		buffer.flip();
		session.write(buffer);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void control(){
		byte[] bts = new byte[35];
		bts[0] =  (byte) 0X21;
		bts[1] =  (byte) 0x32;
		bts[2] =  (byte) 0x34;
		bts[3] =  (byte) 0xe4;
		bts[4] =  (byte) 0xc2;
		bts[5] =  (byte) 0xa1;
		bts[6] =  (byte) 0x01;
		bts[7] =  (byte) 0x04;
		bts[8] =  (byte) 0x00;
		bts[9] =  (byte) 0x00;
		bts[10] =  (byte) 0x00;
		bts[11] =  (byte) 0x01;
		bts[12] =  (byte) 0xB1;
		bts[13] =  (byte) 0xaa;
		bts[14] =  (byte) 0xbb;
		bts[15] =  (byte) 0xcc;
		bts[16] =  (byte) 0xdd;
		bts[17] =  (byte) 0x00;
		bts[18] =  (byte) 0x32;
		bts[19] =  (byte) 0x12;
		bts[20] =  (byte) 0x07;
		bts[21] =  (byte) 0x10;
		bts[22] =  (byte) 0x12;
		bts[23] =  (byte) 0x1c;

		bts[24] =  (byte) 0;
		bts[25] =  (byte) 0;
		bts[26] =  (byte) 0;
		bts[27] =  (byte) 0;
		bts[28] =  (byte) 0;
		bts[29] =  (byte) 0;
		bts[30] =  (byte) 0;
		bts[31] =  (byte) 0;
		bts[32] =  (byte) 0;
		bts[33] =  (byte) 0;
		bts[34] =  (byte) 0;

		IoBuffer buffer = IoBuffer.allocate(35);
		// 自动扩容
		buffer.setAutoExpand(true);
		// 自动收缩
		buffer.setAutoShrink(true);
		buffer.put(bts);
		buffer.flip();
		session.write(buffer);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void messageReceived(IoSession iosession, Object message)
			throws Exception {
		IoBuffer bbuf = (IoBuffer) message;
		byte[] byten = new byte[bbuf.limit()];
		bbuf.get(byten, bbuf.position(), bbuf.limit());
		System.out.println("客户端收到消息" + ByteAndStr16.Bytes2HexString(byten));
	}
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		System.out.println("客户端异常");
		super.exceptionCaught(session, cause);
	}
	@Override
	public void messageSent(IoSession iosession, Object obj) throws Exception {
		System.out.println("客户端消息发送");
		super.messageSent(iosession, obj);
	}
	@Override
	public void sessionClosed(IoSession iosession) throws Exception {
		System.out.println("客户端会话关闭");
		super.sessionClosed(iosession);
	}
	@Override
	public void sessionCreated(IoSession iosession) throws Exception {
		System.out.println("客户端会话创建");
		super.sessionCreated(iosession);
	}
	@Override
	public void sessionIdle(IoSession iosession, IdleStatus idlestatus)
			throws Exception {
		System.out.println("客户端会话休眠");
		super.sessionIdle(iosession, idlestatus);
	}
	@Override
	public void sessionOpened(IoSession iosession) throws Exception {
		System.out.println("客户端会话打开");
		super.sessionOpened(iosession);
	}
}