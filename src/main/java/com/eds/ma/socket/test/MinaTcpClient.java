package com.eds.ma.socket.test;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

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
		for(int j=0;j<1;j++){ // 发送两遍
//			byte[] bts = new byte[11];
//			for (int i = 0; i < 11; i++) {
//				bts[i] = (byte) i;
//			}
			byte[] bts = new byte[7];
			bts[0] =  (byte) 0x18;
			bts[1] =  (byte) 0x01;
			bts[2] =  (byte) 0x11;
			bts[3] =  (byte) 0x00;
			bts[4] =  (byte) 0xbc;
			bts[5] =  (byte) 0x61;
			bts[6] =  (byte) 0x4e;
			IoBuffer buffer = IoBuffer.allocate(7);
			// 自动扩容
			buffer.setAutoExpand(true);
			// 自动收缩
			buffer.setAutoShrink(true);
			buffer.put(bts);
			buffer.flip();
			session.write(buffer);
			Thread.sleep(2000);
		}
		// 关闭会话，待所有线程处理结束后
//		client.connector.dispose(true);
	}

	public static void main22(String[] args) throws InterruptedException {
		MinaTcpClient client = new MinaTcpClient();
//		byte[] temp = new byte[] { (byte) 0x18, (byte) 0x01, (byte) 0x11 };
		byte[] temp = new byte[3];
		temp[0] =  (byte) 0x18;
		temp[1] =  (byte) 0x01;
		temp[2] =  (byte) 0x11;


		IoBuffer buffer = IoBuffer.allocate(3);
		// 自动扩容
		buffer.setAutoExpand(true);
		// 自动收缩
		buffer.setAutoShrink(true);
		buffer.put(temp);
		buffer.flip();
		session.write(buffer);
		Thread.sleep(2000);
		// 关闭会话，待所有线程处理结束后
		client.connector.dispose(true);
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