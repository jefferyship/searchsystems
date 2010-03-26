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
		int totalLen = 0;

		while(matcher.find()){
			String tKeyword = matcher.group(1).trim();
			beforIndex = content.indexOf(tKeyword,beforIndex);
			int afterIndex = beforIndex+tKeyword.length();
			
			String tMsg = content.substring(lastIndex, beforIndex);
			
			int tLen = tMsg.length();
	
			if(tLen > between){	//相隔字符过长
				String tPrefix = tMsg.substring(0, between/2);
				String tPostfix = tMsg.substring(tLen - between/2 , tLen);

				if(totalLen > maxTextLen){	//如果是最大值
					break;
				}else{
					al.add( (isFirst?"":tPrefix) + " ... " + tPostfix + tKeyword);					
				}
				totalLen+=between;
				
			}else{
				if(totalLen > maxTextLen){	//如果是最大值
					break;
				}else{
					al.add( tMsg + tKeyword);
				}
				totalLen+=tLen;
			}
			
			isFirst = false;
			lastIndex = afterIndex;
		}
		

		String endContent = content.substring(lastIndex, content.length());
		
		if(endContent.length() > between){
			al.add(endContent.substring(0, between) + " ... ");
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
		String s = "sdflksjflks你好jrhgv65411kgh不好fghddhjjjfghfghfdbsl你好kjdflksjslfsdfl";
		
		long l = System.currentTimeMillis();
		String t = formatKeyWord(s,new String[]{"你好","不好"}, 8, 20, "<B>", "</B>");
		l = System.currentTimeMillis() - l;
		System.out.println("Time:" + l + " ms");
		System.out.println(s);
		System.out.println(t);
	}
}
