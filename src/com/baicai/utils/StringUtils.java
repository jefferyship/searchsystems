package com.baicai.utils;

import java.security.MessageDigest;

/**
 * 
 * @author kwok
 * 		字符串处理类
 *
 */
public class StringUtils {
	public static String MD5(String textBytes){
		if(textBytes == null || textBytes.trim().equals("")){
			return null;
		}
		try{
			MessageDigest ms = MessageDigest.getInstance("MD5");
			ms.update(textBytes.getBytes());
			return new String(ms.digest());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}	
