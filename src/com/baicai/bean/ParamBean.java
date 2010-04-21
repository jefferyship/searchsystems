package com.baicai.bean;

import java.util.Map;

public abstract class ParamBean {
	//子类一定要实现这一个统计类的唯一条件
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
	
	public Map weightMap(){
		return null;
	}
	
	public String printInfo(){
		return "nowPage:" + nowPage() +"\tperPage:" + perPage() +"\tquerys():" + querys() +"\tstaticCount:" + staticCount();
	}
}
