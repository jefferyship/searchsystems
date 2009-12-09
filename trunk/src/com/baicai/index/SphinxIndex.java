package com.baicai.index;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baicai.SysConstants;
import com.kernaling.utils.FileUtils;
import com.kernaling.utils.MemcacheUtils;
import com.kernaling.utils.ShellExecutor;
import com.kernaling.utils.ThreadUtils;
import com.kernaling.utils.TimeUtils;

/**
 * 
 * @author kwok	kernaling.wong@gmail.com
 * 			2009-12-07
 * 
 * 		此类是基本sphinx索引脚本的类,主要完成了索引的更新与及每一天索引重围等问题
 *
 */
public class SphinxIndex extends Thread{

	private long sleepTime = 20 * 60L;	//默认是 20分钟
	private String RunMainIndexPath = SysConstants.RunMainIndexPath;
	private String RunDeltaIndexPath = SysConstants.RunDeltaIndexPath;
	private String RunxMergeIndexPath = SysConstants.RunxMergeIndexPath;
	private String SphinxTable = SysConstants.SphinxTable;
	private int dayBefor = SysConstants.dayBefor;
	final private IndexConfig ic;
	public SphinxIndex(IndexConfig ic){
		this.ic = ic;
		if(ic == null){
			ic = new IndexConfig();	//创建一个默认的
		}
	}
	
	public void run(){
		init();
		System.out.println(new Date() + "开始索引");
		while(true){
			long start = TimeUtils.TimeInMills(0);
			long[] updateTime = updateTime();
			if(updateTime == null){	//如果为0表示MarkTimeTable并没有值,所以需要重新初始化之
				init();
				updateTime = updateTime();
			}
			ic.updateSpecial(updateTime[0] , updateTime[1]);
			//再重建增量索引
			ArrayList<String> shellList = FileUtils.readBufferLine(RunDeltaIndexPath, "utf-8");
			for(String tShell:shellList){				
				ShellExecutor.shellExe(tShell, "utf-8");
			}
			
			//合并并更新索引
			shellList = FileUtils.readBufferLine(RunxMergeIndexPath, "utf-8");
			for(String tShell:shellList){				
				ShellExecutor.shellExe(tShell, "utf-8");
			}
			
			start = TimeUtils.TimeInMills(0) - start;
			System.out.println("数据库索引完成! 用时.. " + start +" ms");
			ThreadUtils.sleepInSec(sleepTime);
			
			if(ic.canResetIndex()){
				init();	//每一天都应该重置一次,删除过期的索引,清空一天内产生的旧有的数据
			}
		}
	}
	
	/**
	 * 得到现在需要 timeLong[0]	表示索引时间最小的时间
	 * 更新的时间   timeLong[1]  表示索引现在的最大时间
	 * @return
	 */
	public long[] updateTime(){
		long[] timeLong = new long[2];
		
		List tList = SysConstants.mysqlconnect.executeQuery("SELECT MB_Time FROM " + SphinxTable +" WHERE MB_ID = 1");
		
		if(tList == null || tList.isEmpty()){
			return null;
		}
		Map tMap = (Map)tList.get(0);
		timeLong[0] = new Long(tMap.get("MB_Time").toString());
		
		tList = SysConstants.mysqlconnect.executeQuery("SELECT MB_Time FROM " + SphinxTable +" WHERE MB_ID = 2");
		
		if(tList == null || tList.isEmpty()){;
			return null;
		}
		tMap = (Map)tList.get(0);
		timeLong[1] = new Long(tMap.get("MB_Time").toString());
		
		return timeLong;
	}
	
	public void init(){
		//记录了索引的最前时间
		System.out.println(new Date() + "\t开始初始化....");
		if(ic.canResetIndex()){
			String delete = "DELETE FROM " + SphinxTable ;
			SysConstants.mysqlconnect.executeUpdate(delete);
		}
		
		List tList = SysConstants.mysqlconnect.executeQuery("SELECT * FROM " + SphinxTable +" WHERE MB_ID = 1 ");
		
		//最原始的时候
		if(tList.isEmpty()){
			long lastTime = (TimeUtils.TimeInMills(-dayBefor)/1000);
			String sql = "INSERT INTO " + SphinxTable + " SET MB_ID = 1 ,MB_Time =" + lastTime;
			SysConstants.mysqlconnect.executeInsert(sql);
		}
		
		//记录索引现在的时间
		tList = SysConstants.mysqlconnect.executeQuery("SELECT * FROM " + SphinxTable +" WHERE MB_ID = 2 ");
		if(tList.isEmpty()){
			long lastTime = TimeUtils.nowTimeInSec();
			String sql = "INSERT INTO " + SphinxTable + " SET MB_ID = 2 ,MB_Time =" + lastTime;
			SysConstants.mysqlconnect.executeInsert(sql);
		}
		
		MemcacheUtils.clearAll();	//先把内存全部清空
		SysConstants.GrobleCache.clear();//同时把一些辅助缓存也清除
		ic.preLoadData();		//加载一定的数据
		//首先重建旧索引
		ShellExecutor.shellExe(RunMainIndexPath, "utf-8");
		long updateTime[] = updateTime();
		
		if(updateTime == null){
			System.out.println("初始化时间失败!");
			System.exit(0);
		}
		
		ic.updateSpecial(updateTime[0] , updateTime[1]);
		//再重建增量索引
		ShellExecutor.shellExe(RunDeltaIndexPath, "utf-8");
		//合并并更新索引
		ShellExecutor.shellExe(RunxMergeIndexPath, "utf-8");
		
		System.out.println(new Date() + "\t初始化完成....");
	}
}
