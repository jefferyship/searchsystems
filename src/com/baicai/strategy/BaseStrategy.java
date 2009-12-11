package com.baicai.strategy;

import java.util.List;
import java.util.Map;

import com.baicai.bean.ParamBean;
import com.kernaling.utils.SphinxUtils;
import com.kernaling.utils.TimeUtils;

/**
 * 
 * @author 处理类
 *		接收到请求后,DefaultRequestProcess会分发相应的任务给DefaultStrategy类去处理
 *		如:组装参数,如何make搜索结果的xml,和统计统结果
 */
public abstract class BaseStrategy {
	
	protected SphinxUtils searchUtils = null;
	public void setSphinxSearch(SphinxUtils searchUtils){
		this.searchUtils = searchUtils;
	}
	
	/**
	 * 
	 * @param resultList
	 * @param startTime
	 * @param pb
	 * @return
	 * 			产生搜索的结果
	 */
	public abstract String makeResultXML(List<Map<String,Object>> resultList , long startTime ,ParamBean pb);
	/**
	 * 
	 * @param resultList
	 * @param startTime
	 * @param pb
	 * @return
	 * 			产生基本的统计结果
	 */
	public abstract String makeStaticXML(List<Map<String,Object>> resultList , long startTime ,ParamBean pb);
	
	/**
	 * 
	 * @param infoMap
	 * @param startTime
	 * @param pb
	 * @return
	 * 			组成一个页面返回的信息,已经默认实现了
	 */
	public String makeInfoXML(Map<String,Object> infoMap , long startTime ,ParamBean pb){
		long timeInUsed = TimeUtils.TimeInMills(0) - startTime;
		long Total = (Integer)infoMap.get("Total");
		long TotalFound = (Integer)infoMap.get("TotalFound");
		float time = (Float)infoMap.get("TimeInUsed");
		String keyWords = (String)infoMap.get("KeyWords");
		
		StringBuffer sb  = new StringBuffer();
		sb.append("<Info><TimeInUsed>");
		sb.append(timeInUsed);
		sb.append("</TimeInUsed>");
		sb.append("<TotalFound>");
		sb.append(TotalFound);
		sb.append("</TotalFound>");
		sb.append("<Total>");
		sb.append(Total);
		sb.append("</Total>");
		sb.append("<TimeInUsed>");
		sb.append(time);
		sb.append("</TimeInUsed>");
		sb.append(keyWords);
		sb.append("</TimeInUsed>");
		sb.append("</Info>");
		
		return sb.toString();
	}
	
	public abstract ParamBean preParm(Map<String, String> pramMap);
}
