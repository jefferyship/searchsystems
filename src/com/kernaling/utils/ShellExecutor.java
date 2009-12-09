package com.kernaling.utils;

import java.io.InputStreamReader;

public class ShellExecutor {
	public static String shellExe(String shellPath,String charset){
		InputStreamReader isr = null;
		try {
			Process pro = Runtime.getRuntime().exec(shellPath);
//			pro.waitFor();
			isr = new InputStreamReader(pro.getInputStream(),charset);
			char buf[] = new char[1024];
			int len = 0;
			StringBuffer sb = new StringBuffer();
			while((len=isr.read(buf))!=-1){
				sb.append(new String(buf,0,len));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(isr!=null){
				try{					
					isr.close();
				}catch(Exception ex){}
			}
		}
		return null;
	
	}
}
