package com.kernaling.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	public static long nowTimeInSec(){
		return TimeInMills(0)/1000;
	}
	
	public static long TimeInMills(int day){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, day);
		return cal.getTimeInMillis();
	}
	
	public static String TimeInFormate(String timeFormat , long timeInLong){
		if(timeFormat == null || timeFormat.trim().equals("")){
			timeFormat = "YYYY-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
		return sdf.format(new Date(timeInLong));
	}
	
	public static boolean isTimeAfter(long firstTime , long afterTime){
		return firstTime > afterTime;
	}
	
	public static boolean isTimeAfter(Date firstDate , Date afterDate){
		return isTimeAfter(firstDate.getTime(), afterDate.getTime());
	}
}
