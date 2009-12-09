package com.baicai.test;


import java.util.Date;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcacheTest {
	public static void main(String args[]){
		
		MemCachedClient mcc = new MemCachedClient();

		
		String servers[] = {"192.168.1.111:3335"};
		Integer[] weight = {100};
		SockIOPool pool =  SockIOPool.getInstance();
		
		pool.setServers(servers);
		pool.setWeights(weight);
		pool.setInitConn( 5 );
		pool.setMinConn( 5 );
		pool.setMaxConn( 250 );
		pool.setMaxIdle( 1000 * 60 * 60 * 6 );

		pool.setMaintSleep( 30 );
		
//		pool.setNagle( false );
//		pool.setSocketTO( 3000 );
//		pool.setSocketConnectTO( 0 );
		pool.initialize();
		
		mcc.setPrimitiveAsString( true );
		mcc.setCompressEnable(false);
		mcc.setCompressThreshold( 64 * 1024 );
		pool.setHashingAlg( SockIOPool.NEW_COMPAT_HASH );

		mcc.set("www", "www.it.com.cn",new Date(1000));
//		mcc.add("www", "www.pconoine.com.cn");
		String v = (String)mcc.get("www");
		
//		System.out.println(mcc.keyExists("www"));
		
		System.out.println(v);

	}
}
