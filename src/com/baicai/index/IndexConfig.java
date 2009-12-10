package com.baicai.index;

import com.baicai.SysConstants;
import com.kernaling.utils.SphinxUtils;
import com.kernaling.utils.TimeUtils;

/**
 * 
 * @author 配置索引时需要预告加载的东西,同时可以实现需要删除什么的时候
 *
 */
public class IndexConfig {
	protected SphinxUtils su = SphinxUtils.getInstance(SysConstants.SphinxHost,SysConstants.SphinxPort);
	protected String nowDay = "";
	private String timeFormat = "yyyy-MM-dd";
	public IndexConfig(){
		long timeInLong = TimeUtils.TimeInMills(0);
		nowDay = TimeUtils.TimeInFormate(timeFormat, timeInLong);
	}
	
	public void preLoadData(){
	}
	
	/**
	 * 用于更新或者删除指定的数据
	 * 	SphinxUtils.UpdateAttribute(index, attr, docID, value) 方法来实现在合并索引的时候删除索引
	 */
	public void updateSpecial(long startTime , long updateTime){
		
	}
	
	/**
	 * 		默认表示每一天的凌晨 0 点开始重做索引
	 * @return
	 */
	public boolean canResetIndex(){
		long timeInLong = TimeUtils.TimeInMills(0);
		String tNowDay = TimeUtils.TimeInFormate(timeFormat, timeInLong);
		return !nowDay.equals(tNowDay);
	}
}
