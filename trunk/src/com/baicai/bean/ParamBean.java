package com.baicai.bean;

public abstract class ParamBean {
	
	public abstract String resultUniKey();
	public abstract String staticUniKey();
	
	public int nowPage(){
		return 1;
	}
	
	public int perPage(){
		return 20;
	}
	
	public abstract String querys();
	
	public String[] staticQuery(){
		return null;
	}
	
	public String sorts(){
		return null;
	}
	
	public int staticCount(){
		return 10;
	}
}
