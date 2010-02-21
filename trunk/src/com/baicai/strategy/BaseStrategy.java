package com.baicai.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baicai.SysConstants;
import com.baicai.bean.ParamBean;
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
		int exLen_1 = prefix.length();
		int exLen_2 = posfix.length();
		
		int maxLen = content.length();
		int min = maxLen;
		int max = 0;
		for(int i=0;i<keywords.length;i++)
		{
			int t = content.indexOf(keywords[i]);
			int n = content.lastIndexOf(keywords[i]);
			int tmpLen = keywords[i].length();
			
			if(t-between-exLen_1 < min){
				min = t-between-exLen_1;
				min=min<0?0:min;
			}
			
			if(n+between+tmpLen+exLen_2>max){
				max = n+between+tmpLen+exLen_2;
				max=max>maxLen?maxLen:max;
			}
		}
		
		Pattern pattern = Pattern.compile(("(" + prefix+".+?"+posfix+")"), Pattern.DOTALL);
		ArrayList<String> keyArray = new ArrayList<String>();
		
		
		content = content.substring(min, max);
		
		
		Matcher m = pattern.matcher(content);
		while(m.find()){
			keyArray.add(m.group(1).trim());
		}
		
		String s[] = content.split(prefix+".*?"+posfix);
		StringBuffer result = new StringBuffer();
		if(min > 0){
			result.append(" ... ");
		}
		for(int t=0;t<s.length;t++){
			String tm = s[t];
			
			if(tm.length() > 30){	//如果每一段长度大于30则应该需要省略显示
				result.append(tm.substring(0, 15) +" ... "+ tm.substring(tm.length()-15, tm.length()));
			}else{
				result.append(tm);
			}
			
			if(t > 10){
				break;
			}
			
			if(t<s.length-1){				
				result.append(keyArray.get(t));
			}
		}
		if(maxLen>max){
			result.append(" ... ");			
		}
		return result.toString();
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
	public String makeStaticXML(List<Map<String,Object>> resultList , long startTime ,ParamBean pb,String tStaticQuery,String tStaticXML){
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("<"+tStaticXML+">");
		for(Map<String, Object> tMap:resultList){
			String staticKey = tMap.get(tStaticXML).toString();
			
			if(staticKey == null){
				System.out.println("tStaticXML:" + tStaticXML +"\t并没有找到...");
				continue;
			}
			String tValue = getStaticDataMap(staticKey);
			Long count = (Long)tMap.get("@count");
			sb.append(tValue);
			sb.append("=");
			sb.append(count);
			sb.append("\n\r");
			
			System.out.println("统计数据:"+tStaticXML +"\tName:"+tValue+"\tCount:" + count);
		}
		sb.append("</"+tStaticXML+">");
		return sb.toString();
	}
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
		long PageInTotal = (TotalFound%PerPage==0 ? TotalFound/PerPage : TotalFound/PerPage + 1);
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
