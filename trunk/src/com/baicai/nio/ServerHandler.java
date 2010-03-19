package com.baicai.nio;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.baicai.handle.RequestProcess;
import com.kernaling.utils.StringUtils;

/**
 * 当客户端发送一行搜索请求至服务器端时,会交由此类进行处理。
 * 
 * @author kwok(kernaling.wong@gmail.com) 03.12.2009
 */
public class ServerHandler extends IoHandlerAdapter {

	private static final Charset CHARSET = Charset.forName("utf-8");
	private int threadNum = 10;
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(threadNum,
			threadNum * 2, 600000, TimeUnit.SECONDS,
			new ArrayBlockingQueue<Runnable>(1000),
			new ThreadPoolExecutor.DiscardPolicy());
	// 处理用户搜索的实例
	private RequestProcess ps = null;

	public ServerHandler(RequestProcess ps) {
		this.ps = ps;
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		// 未知异常产生时，关闭连接
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message) {
		IoBuffer rb = (IoBuffer) message;
		message = null;
		byte[] encode = null;
		String queryResult = null;
		String query = null;
		try {
			query = rb.getString(CHARSET.newDecoder());
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rb.clear();
		//	md5值 
		System.out.println(new Date() + "\t收到参数:" + query);
//		final String md5 =StringUtils.MD5(query);
		
//		queryResult = (String)MemcacheUtils.get(md5);
		queryResult = null;
		System.out.println("-===========");
		if (queryResult != null && !queryResult.equals("")) {
			try {

				// 发送搜索结果至socket客户端,并关闭会话
				encode = queryResult.getBytes("utf-8");
				IoBuffer resultBuffer = IoBuffer.wrap(encode);

				session.write(resultBuffer);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				session.close(true);
			}

		} else {
			final String query2 = query;
			final IoSession session2 = session;
			
			System.out.println("开始使用... 1");
			
			Runnable run = new Runnable() {
				public void run() {
					try {
						// TODO Auto-generated method stub
						// 处理搜索请求
						System.out.println("开始使用... 2");
						HashMap<String, String> map = getMap(query2);
//						long start = System.currentTimeMillis();
						String queryResult = "";
						if(ps != null){
							System.out.println("=====");
							queryResult = ps.execute(map);
							System.out.println("返回:" + queryResult);
						}
						if (queryResult != null && !queryResult.equals("")) {

							byte[] encode = queryResult.getBytes("utf-8");
							IoBuffer resultBuffer = IoBuffer.wrap(encode);
							session2.write(resultBuffer);
//							MemcacheUtils.set(md5, resultBuffer);
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						session2.close(true);
					}
				}

			};
			executor.execute(run);
		}
	}

	private static HashMap<String, String> getMap(String insert) {
		String[] lines = insert.split("\n");
		// TreeMap<查询属性, 查询值>,比如查询工作为java的人,则是 <"jobKey", "java">
		HashMap<String, String> queryMap = new HashMap<String, String>();
		String s[] = null;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i] != null && !lines[i].equals("")) {
				s = lines[i].split("=", 2);
				if (s.length == 2 && !s[0].replaceAll("\"", "").equals("")
						&& !s[1].replaceAll("\"", "").equals("")) {
					queryMap.put(s[0].replaceAll("\"", ""), s[1].replaceAll(
							"\"", ""));
					s = null;
				}
				lines[i] = null;
			} else {
				break;
			}
		}
		lines = null;
		insert = null;
		return queryMap;
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
//		SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
//		cfg.setReceiveBufferSize(2 * 1024 * 1024);
//		cfg.setReadBufferSize(2 * 1024 * 1024);

	}

}
