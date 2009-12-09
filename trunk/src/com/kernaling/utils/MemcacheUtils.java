package com.kernaling.utils;

import java.util.Date;

import com.baicai.SysConstants;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/**
 * 
 * @author kwok (kernaling.wong@gmail.com)
 * 			2009-12-06
 * 
 * 		Memcahce 的简单工具类,如果config.txt传入的memcache主机出问题,则不使用
 *
 */
public class MemcacheUtils {
	final private static MemCachedClient mcc = new MemCachedClient();
	private static boolean inUsed = false;
	static{
		String servers[] = null;
		try{			
			servers = SysConstants.memcacheHost.split(",");
			for(int i=0;i<servers.length;i++){
				String t = servers[i];
				if(t == null || t.trim().equals("")){
					throw new Exception("Memcach路径:" +servers +" 出错了...." );
				}
			}
			inUsed = true;
		}catch(Exception ex){
			ex.printStackTrace();
			inUsed = false;
		}
		Integer[] weight = {100};
		SockIOPool pool =  SockIOPool.getInstance();
		
		pool.setServers(servers);
		pool.setWeights(weight);
		pool.setInitConn( 5 );
		pool.setMinConn( 5 );
		pool.setMaxConn( 250 );
		pool.setMaxIdle( 1000 * 60 * 60 * 6 );

		pool.setMaintSleep( 30 );
		pool.setHashingAlg( SockIOPool.NEW_COMPAT_HASH );
		
		pool.setNagle( false );
		pool.setSocketTO( 3000 );
		pool.setSocketConnectTO( 0 );
		pool.initialize();
//		mcc.setPrimitiveAsString( true );
		mcc.setDefaultEncoding("UTF-8");
		mcc.setCompressEnable(false);
		mcc.setCompressThreshold( 64 * 1024 );
	}
	
	public static void set(String key,Object value,long time){
		if(!inUsed){
			return;
		}
		mcc.set(key, value , new Date(time));
	}
	
	public static void set(String key,Object value){
		set(key, value, 0);
	}
	
	
	public static Object get(String key){
		if(!inUsed){
			return null;
		}
		
		return mcc.get(key);
	}
	
	public static Object[] get(String[] keys){
		if(!inUsed || keys == null){
			return null;
		}
		
		return mcc.getMultiArray(keys);
	}
	
	public static void remove(String key){
		if(!inUsed){
			return;
		}
		
		mcc.delete(key);
	}
	
	public static void clearAll(){
		if(!inUsed){
			return;
		}
		mcc.flushAll();
	}
	
	public static void main(String args[]){
		
		
	}
}
