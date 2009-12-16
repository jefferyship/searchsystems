package com.baicai.handle;

import java.util.Map;

public abstract class RequestProcess {
	public RequestProcess(){
		
	}
	/**
	 * 
	 * @param map
	 * @return
	 * 			从Mina传入的参数后,则需要分配给此方法来做搜索功能,返回就是搜索结果了
	 */
	public abstract String execute(Map<String,String> map);
}
