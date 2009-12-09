package com.baicai.handle;

import java.util.List;
import java.util.Map;

import org.sphx.api.SphinxClient;

import com.baicai.SysConstants;
import com.baicai.bean.ParamBean;
import com.baicai.strategy.DefaultStrategy;
import com.baicai.utils.StringUtils;
import com.kernaling.utils.MemcacheUtils;
import com.kernaling.utils.SphinxUtils;
import com.kernaling.utils.TimeUtils;

public class DefaultRequestProcess extends RequestProcess {

	protected SphinxUtils sphinxSearch = SphinxUtils.getInstance(SysConstants.SphinxHost, SysConstants.SphinxPort);
	protected DefaultStrategy ds = null;
	public DefaultRequestProcess(DefaultStrategy ds){
		this.ds = ds;
		
		if(sphinxSearch != null && ds != null){
			ds.setSphinxSearch(sphinxSearch);
		}
	}
	
	@Override
	public String execute(Map<String, String> pramMap) {
		// TODO Auto-generated method stub
		long startTime = TimeUtils.TimeInMills(0);
		ParamBean pb = preParm(pramMap);
		
		if(pb == null){
			return null;
		}
		
		int nowPage = pb.nowPage();
		int perPage = pb.perPage();
		int sortCount = pb.staticCount();
		String querys = pb.querys();
		String staticQuerys[] = pb.staticQuery();
		String sorts = pb.sorts();
		String resultMD5 = StringUtils.MD5(pb.resultUniKey());
		String cacheResult = (String)MemcacheUtils.get(resultMD5);
		
		Map<String,Object> infoMap = null;
		//以下是搜索结果
		
		if(querys == null){
			return null;
		}
		
		if(sorts == null){	//默认是按相关度排序 
			sorts = " @relevance desc";
		}
		
		StringBuffer returnResult = new StringBuffer();
		if(cacheResult == null || cacheResult.trim().equals("")){	//如果是缓存中不存在
			List<Map<String,Object>> tList = sphinxSearch.search("main", perPage , querys ,  nowPage , sorts , SphinxClient.SPH_SORT_EXTENDED );
			cacheResult = ds.makeResultXML(tList, startTime, pb);
			infoMap = tList.get(0);
		}
		if(cacheResult != null && !cacheResult.trim().equals("")){
			MemcacheUtils.set(resultMD5, cacheResult);
			returnResult.append(cacheResult);			//把搜索结果加入
		}
		
		String staticMD5 = StringUtils.MD5(pb.staticUniKey());

		//统计结果
		if(staticQuerys != null){
			String staticResult = (String)MemcacheUtils.get(staticMD5);
			StringBuffer tmpStaticResult = new StringBuffer();
			if(staticResult == null || staticResult.trim().equals("")){	//如果是缓存中不存在
				tmpStaticResult.append("<Statics>");
				for(int i=0;i<staticQuerys.length;i++){
					String tStaticXML = staticQuerys[i];
					//List<Map<String,Object>> tList = sphinxSearch.search("main", nowPage , tStaticXML , perPage);
					List<Map<String,Object>> l = sphinxSearch.search("main", 0, querys , sortCount  , "@relevance desc" , SphinxClient.SPH_SORT_EXTENDED , tStaticXML , "@count desc");
					staticResult = ds.makeStaticXML(l, startTime, pb);
					tmpStaticResult.append(staticResult);
				}
				tmpStaticResult.append("</Statics>");
			}else{
				tmpStaticResult.append(staticResult);
			}
			if(tmpStaticResult.length() > 0){
				MemcacheUtils.set(staticMD5, staticResult);		//把统计结果加入
				returnResult.append(cacheResult);
			}
		}
		
		//基本信息,当前页,分页,搜索用时....
		String info = ds.makeInfoXML(infoMap, startTime, pb);
		
		if(info != null && !info.trim().equals("")){
			returnResult.append(info);
		}
		
		return returnResult.toString();
	}
	
	protected ParamBean preParm(Map<String, String> pramMap){
		return null;
	}
}
