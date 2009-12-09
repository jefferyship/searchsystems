package com.baicai.nio;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.baicai.SysConstants;

public class SocketSearchServer extends Thread{
	/**
	 * 服务器接收端口
	 */
	private static final int SERVER_PORT = SysConstants.SERVER_PORT;
	private IoHandlerAdapter adapter = null;
	// 使用nio接受器
	private NioSocketAcceptor acceptor = null;

	public SocketSearchServer(IoHandlerAdapter adapter) {
		this.adapter = adapter;
	}

	public void start() {
		acceptor = new NioSocketAcceptor(10);
		acceptor.setReuseAddress(true);//设置的是主服务器监听的端口可以重用
		
		acceptor.getSessionConfig().setReuseAddress(true);//设置每一个非主监听连接的端口可以重用
		acceptor.getSessionConfig().setReceiveBufferSize(2 * 1024 * 1024);//设置输入缓冲区的大小
		acceptor.getSessionConfig().setReadBufferSize(2 *1024 * 1024);
		//设置为非延迟发送，为true则不组装成大包发送，受到东西马上发出
		acceptor.getSessionConfig().setTcpNoDelay(true);
		//设置主服务器监听端口队列的最大值为100，如果当前已经有100个连接，再新的连接来将被服务器拒绝
		acceptor.setBacklog(100);
		acceptor.setDefaultLocalAddress(new InetSocketAddress(SERVER_PORT));
		acceptor.setHandler(adapter);
		// 绑定到端口
		try {
			acceptor.bind(new InetSocketAddress(SERVER_PORT));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Listening on port " + SERVER_PORT);
	}

	/**
	 * 程序主入口
	 * 
	 * @param args
	 * @throws Throwable
	 *             如果端口被占用，会抛出异常
	 */
	public static void main(String[] args) throws Throwable {
//		new SocketSearchServer(null).start();
	}

}
