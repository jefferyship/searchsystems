package com.baicai.handle;

import java.util.Map;

public abstract class RequestProcess {
	public RequestProcess(){
		
	}
	
	public abstract String execute(Map<String,String> map);
}
