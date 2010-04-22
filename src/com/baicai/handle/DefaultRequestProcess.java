package com.baicai.handle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sphx.api.SphinxClient;

import com.baicai.SysConstants;
import com.baicai.bean.ParamBean;
import com.baicai.strategy.BaseStrategy;
import com.baicai.utils.StringUtils;
import com.kernaling.utils.MemcacheUtils;
import com.kernaling.utils.SphinxUtils;
import com.kernaling.utils.TimeUtils;

/**
 * 
 * @author kwok
 *		只是处理接受,实现基本的处理流程,至于参数如何去组装,如何成功搜索条件,如何生成一个xml等
 *		邮件DefaultStrategy类及其子类去实现
 */
public class DefaultRequestProcess extends RequestProcess {

	final protected SphinxUtils sphinxSearch = SphinxUtils.getInstance(SysConstants.SphinxHost, SysConstants.SphinxPort);
	final protected BaseStrategy ds;
	public DefaultRequestProcess(BaseStrategy ds){
		this.ds = ds;
		
		if(sphinxSearch != null && ds != null){
			ds.setSphinxSearch(sphinxSearch);
		}
	}
	
	@Override
	public String execute(Map<String, String> pramMap) {
		// TODO Auto-generated method stub
		
		ParamBean pb = ds.preParm(pramMap);
		if(pb == null){
			return null;
		}
		long startTime = TimeUtils.TimeInMills(0);
		
		int nowPage = pb.nowPage();
		int perPage = pb.perPage();
		int sortCount = pb.staticCount();
		String querys = pb.querys();
		String staticQuerys[] = pb.staticQuery();
		String sorts = pb.sorts();
		
		if(nowPage < 1 || perPage < 1){
			perPage = 10;
			nowPage = 0;
		}
		
		Map<String,Object> infoMap = null;
		//以下是搜索结果
		
		if(querys == null){
			return null;
		}
		
		if(sorts == null){	//默认是按相关度排序 
			sorts = " @relevance desc";
		}
		
		StringBuffer returnResult = new StringBuffer();
		returnResult.append("<XML>");
		//如果是缓存中不存在
		HashMap<String,Integer> weightMap = (HashMap<String,Integer>)pb.weightMap();
		List<Map<String,Object>> tList = sphinxSearch.search("main", perPage , querys ,  (nowPage-1)*perPage , sorts , SphinxClient.SPH_SORT_RELEVANCE,weightMap);
		
		if(tList == null){
			System.out.println("搜索地址:"+SysConstants.SphinxHost+"\t端口:" + SysConstants.SphinxPort +"\t返回了空结果");
			return "";
		}
		
		if(tList.size() > 0){
			infoMap = tList.remove(0);			
		}

		String result = ds.makeResultXML(tList, startTime, pb , infoMap);	
		returnResult.append(result);			//把搜索结果加入
		
		String staticMD5 = StringUtils.MD5(pb.staticUniKey());

		//统计结果
		if(staticQuerys != null){
			String staticResult = null;
			if(staticMD5 != null && !staticMD5.equals("")){
				staticResult = (String)SysConstants.StaticDataCache.get(staticMD5);				
			}
			
			StringBuffer tmpStaticResult = new StringBuffer();
			if(staticResult == null || staticResult.trim().equals("")){	//如果是缓存中不存在
				tmpStaticResult.append("<Statics>");
				for(int i=0;i<staticQuerys.length;i++){
					String staticField = staticQuerys[i];
					List<Map<String,Object>> l = sphinxSearch.search("main", sortCount , querys , 0  , "@relevance desc" , SphinxClient.SPH_SORT_EXTENDED , staticField , "@count desc" , null);
					if(l.size()>1){
						l.remove(0);
					}
					staticResult = ds.makeStaticXML(l, startTime, pb , staticField);
					tmpStaticResult.append(staticResult);
				}
				tmpStaticResult.append("</Statics>");
			}else{
				tmpStaticResult.append(staticResult);
			}
			if(tmpStaticResult.length() > 0){
				if(staticMD5 != null && !staticMD5.equals("")){
					SysConstants.StaticDataCache.put(staticMD5, tmpStaticResult.toString());
				}
				returnResult.append(tmpStaticResult);
			}
		}
		//基本信息,当前页,分页,搜索用时....
		String info = ds.makeInfoXML(infoMap, startTime, pb);
		
		if(info != null && !info.trim().equals("")){
			returnResult.append(info);
		}
		returnResult.append("</XML>");
				
		return returnResult.toString();
	}
}
