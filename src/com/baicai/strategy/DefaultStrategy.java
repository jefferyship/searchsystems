package com.baicai.strategy;

import java.util.List;
import java.util.Map;

import com.baicai.bean.ParamBean;
import com.kernaling.utils.SphinxUtils;
import com.kernaling.utils.TimeUtils;

public abstract class DefaultStrategy {
	
	protected SphinxUtils searchUtils = null;
	public void setSphinxSearch(SphinxUtils searchUtils){
		this.searchUtils = searchUtils;
	}
	public abstract String makeResultXML(List<Map<String,Object>> resultList , long startTime ,ParamBean pb);
	public abstract String makeStaticXML(List<Map<String,Object>> resultList , long startTime ,ParamBean pb);
	
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
		
		return sb.toString();
	}
}
