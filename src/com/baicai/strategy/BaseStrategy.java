package com.baicai.strategy;

import java.util.List;
import java.util.Map;

import com.baicai.SysConstants;
import com.baicai.bean.ParamBean;
import com.kernaling.utils.MemcacheUtils;
import com.kernaling.utils.SphinxUtils;
import com.kernaling.utils.TimeUtils;

/**
 * 
 * @author	kwok 
 * 		处理类
 *		接收到请求后,DefaultRequestProcess会分发相应的任务给DefaultStrategy类去处理
 *		如:组装参数,如何make搜索结果的xml,和统计统结果
 */
public abstract class BaseStrategy {
	
	protected SphinxUtils searchUtils = null;
	
	protected Map<String,Object> getDataMap(String keyID){
		if(keyID == null){
			return null;
		}
		
		Map<String,Object> tMap = (Map<String,Object>)SysConstants.GrobleCache.get(keyID);
		
		if(tMap == null){
			tMap = getFromDB(keyID);
			
			if(tMap != null){
				SysConstants.GrobleCache.put(keyID, tMap);
			}
		}
		return tMap;
	}
	
	protected String getStaticDataMap(String keyID){
		if(keyID == null){
			return null;
		}
		
		String tMap = (String)SysConstants.StaticDataCache.get(keyID);
		
		if(tMap == null){
			tMap = getFromStaticDB(keyID);
			
			if(tMap != null){
				SysConstants.StaticDataCache.put(keyID, tMap);
			}
		}
		return tMap;
	}
	
	protected String getFromStaticDB (String keyID) {
		return null;
	}
	
	protected Map<String,Object> getFromDB(String keyID){
		return null;
	}
	
	protected String formatKeyWord(String content,String[] keywords,int between,String prefix,String posfix){
		if(content == null){
			return null;
		}
		for(int i=0;i<keywords.length;i++)
		{
			content = content.replace(keywords[i], prefix + keywords[i] + posfix);
		}
		
		int maxLen = content.length();
		int min = maxLen;
		int max = 0;
		
		for(int i=0;i<keywords.length;i++)
		{
			int t = content.indexOf(keywords[i]);
			int n = content.lastIndexOf(keywords[i]);
			int tmpLen = keywords[i].length();
			
			if(t-between < min){
				min = t-between;
				min=min<0?0:min;
			}
			
			if(n+between+tmpLen>max){
				max = n+between+tmpLen;
				max=max>maxLen?maxLen:max;
			}
			
		}
		
		content = content.substring(min , max);
		if(min > 0){
			content = "..." + content;
		}
		
		if(content.length() > 200 ){			
			content = content.substring(0, 200) +"...";
		}
		return content;
	}
	
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
	public abstract String makeResultXML(List<Map<String,Object>> resultList , long startTime ,ParamBean pb,Map<String,Object> infoMap);
	/**
	 * 
	 * @param resultList
	 * @param startTime
	 * @param pb
	 * @return
	 * 			产生基本的统计结果
	 */
	public abstract String makeStaticXML(List<Map<String,Object>> resultList , long startTime ,ParamBean pb,String tStaticQuery);	
	public abstract ParamBean preParm(Map<String, String> pramMap);
	
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
		int PerPage = pb.perPage();
		int nowPage = pb.nowPage();
		long PageInTotal = (TotalFound%PerPage==0 ? TotalFound%PerPage : TotalFound/PerPage + 1);
		float time = (Float)infoMap.get("TimeInUsed");
		String keyWords = (String)infoMap.get("KeyWords");
		
		StringBuffer sb  = new StringBuffer();
		sb.append("<Info><TimeInUsed>");
		sb.append(timeInUsed);
		sb.append("</TimeInUsed>");
		sb.append("<TotalFound>");
		sb.append(TotalFound);
		sb.append("</TotalFound>");
		sb.append("<PageInTotal>");
		sb.append(PageInTotal);
		sb.append("</PageInTotal>");
		sb.append("<NowPage>");
		sb.append(nowPage);
		sb.append("</NowPage>");
		sb.append("<PerPage>");
		sb.append(PerPage);
		sb.append("</PerPage>");
		sb.append("<Total>");
		sb.append(Total);
		sb.append("</Total>");
		sb.append("<TimeInSearch>");
		sb.append(time);
		sb.append("</TimeInSearch>");
		sb.append("<Keywords>");
		sb.append(keyWords);
		sb.append("</Keywords>");
		sb.append("</Info>");
		
		return sb.toString();
	}
	
}
