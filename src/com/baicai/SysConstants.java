package com.baicai;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.kernaling.mysql.MySQLConnect;
import com.kernaling.mysql.MySQLPool;
import com.kernaling.utils.FileUtils;
import com.kernaling.utils.SphinxUtils;

public class SysConstants {
	public static MySQLConnect mysqlconnect = null;
	public static int SERVER_PORT = 0;
	public static String memcacheHost;
	public static String SphinxHost;
	public static int SphinxPort;
	public static String SphinxTable;
	public static String RunMainIndexPath;
	public static String RunDeltaIndexPath;
	public static String RunxMergeIndexPath;
	public static int dayBefor;
	final public static ConcurrentHashMap<String, Object> GrobleCache = new ConcurrentHashMap<String, Object>();
	
	static{
		String charset = "utf-8";
		String configPath = "./config/config.txt";
		Map<String,String> cMap = FileUtils.loadProperties(configPath, charset);
		
		String Host = cMap.get("Host");
		String DB = cMap.get("DB");
		String Usr = cMap.get("Usr");
		String Passwd = cMap.get("Passwd");
		int Port = Integer.parseInt(cMap.get("Port").trim());
		int MaxSize = Integer.parseInt(cMap.get("MaxConn").trim());
		MySQLPool pool = new MySQLPool(Host,DB,Port,Usr,Passwd,MaxSize);
		mysqlconnect = new MySQLConnect(pool);
		
		SERVER_PORT = Integer.parseInt(cMap.get("SERVER_PORT").trim());
		memcacheHost = cMap.get("MemcachedHost");
		
		SphinxHost = cMap.get("SphinxHost").trim();
		SphinxPort = Integer.parseInt(cMap.get("SphinxPort").trim());
		SphinxTable	=	cMap.get("SphinxTable").toString();
		
		RunMainIndexPath = cMap.get("RunMainIndexPath").toString();
		RunDeltaIndexPath = cMap.get("RunDeltaIndexPath").toString();
		RunxMergeIndexPath = cMap.get("RunxMergeIndexPath").toString();
		
		dayBefor = new Integer(cMap.get("dayBefor").toString());
	}
}
