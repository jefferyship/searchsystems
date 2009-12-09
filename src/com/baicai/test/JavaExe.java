package com.baicai.test;

import java.io.IOException;
import java.io.InputStreamReader;

public class JavaExe {
	public static void main(String args[]){
		try {
			Process pro = Runtime.getRuntime().exec("e:/dd.cmd");
//			pro.waitFor();
			InputStreamReader isr = new InputStreamReader(pro.getInputStream(),"GBK");
			char buf[] = new char[1024];
			int len = 0;
			StringBuffer sb = new StringBuffer();
			while((len=isr.read(buf))!=-1){
				sb.append(new String(buf,0,len));
			}
			isr.close();
			System.out.println(sb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
