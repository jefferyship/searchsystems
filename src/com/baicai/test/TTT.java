package com.baicai.test;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TTT {
	
	public static String formatKeyWord(String content,String[] keywords,int between,int maxTextLen , String prefix,String posfix){
		if(content == null){
			return null;
		}

		for(int i=0;i<keywords.length;i++)
		{
			content = content.replace(keywords[i], prefix + keywords[i] + posfix);
		}

		
		Matcher matcher = Pattern.compile(("(" + prefix+".+?"+posfix+")"), Pattern.DOTALL).matcher(content);

		ArrayList<String> al = new ArrayList<String>();
		int lastIndex = 1;
		boolean isFirst = true;
		int beforIndex = 0 ;
		while(matcher.find()){
			String tKeyword = matcher.group(1).trim();
			beforIndex++;
			beforIndex = content.indexOf(tKeyword,beforIndex);
			int afterIndex = beforIndex+tKeyword.length();
			
			String tMsg = content.substring(lastIndex, beforIndex);
			
			int tLen = tMsg.length();
			if(tLen > between){				
				String tPrefix = tMsg.substring(0, between/2);
				String tPostfix = tMsg.substring(tLen - between/2 , tLen);
				al.add( (isFirst?"":tPrefix) + " ... " + tPostfix + tKeyword);
				
			}else{
				al.add( tMsg + tKeyword);
			}
			
			isFirst = false;
			lastIndex = afterIndex;
		}
		
		String endContent = content.substring(lastIndex, content.length());
		int endLen = endContent.length();
		if(endLen > between){
			al.add(content.substring(0, between) + " ... ");
		}else{
			al.add(endContent);
		}
		
		StringBuffer sb = new StringBuffer();
		for(String s:al){
			sb.append(s);
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String s = "lksjflks你好jsl你好kjflsfds";
		
		long l = System.currentTimeMillis();
		String t = formatKeyWord(s,new String[]{"你好"}, 6, 3, "<B>", "</B>");
		l = System.currentTimeMillis() - l;
		System.out.println("Time:" + l + " ms");
		System.out.println(t);
	}
}
