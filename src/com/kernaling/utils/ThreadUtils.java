package com.kernaling.utils;

public class ThreadUtils {
	
	public static void sleepInSec(long sec){
		sleepInMs(sec*1000L);
	}
	
	public static void sleepInMs(long sec){
		sec = sec > 0L ? sec : 1L;
		try{
			Thread.sleep(sec);
		}catch(Exception ex){}
	}
	
}
