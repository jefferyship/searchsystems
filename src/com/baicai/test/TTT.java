package com.baicai.test;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kernaling.utils.FileUtils;

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
		int lastIndex = 0;
		boolean isFirst = true;
		int beforIndex = -1 ;
		int totalLen = 0;

		while(matcher.find()){
			String tKeyword = matcher.group(1).trim();
			beforIndex++;
			beforIndex = content.indexOf(tKeyword,beforIndex);

			int afterIndex = beforIndex+tKeyword.length();
			String tMsg = content.substring(lastIndex, beforIndex);
			
			int tLen = tMsg.length();
	
			if(tLen > between){	//相隔字符过长
				String tPrefix = tMsg.substring(0, between/2);
				String tPostfix = tMsg.substring(tLen - between/2 , tLen);

				if(totalLen > maxTextLen){	//如果是最大值
					
					if(tLen == 0){	//如果是紧跟的分词,则寻找下一个
						continue;
					}
					
					al.add( (tLen < between)?tMsg:tMsg.substring(0, between) + " ... ");

					break;
				}else{
					al.add( (isFirst?"":tPrefix) + " ... " + tPostfix + tKeyword);					
				}
				totalLen+=between;
				
			}else{
				if(totalLen > maxTextLen){	//如果是最大值
					
					if(tLen == 0){	//如果是紧跟的分词,则寻找下一个
						continue;
					}
					
					al.add( (tLen < between)?tMsg:tMsg.substring(0, between) + " ... ");
					
					break;
				}else{
					al.add( tMsg + tKeyword);
				}
				totalLen+=tLen;
			}
			
			isFirst = false;
			lastIndex = afterIndex;
		}
		
		if(al.isEmpty()){	//如果没有任何匹配的话,则直接取最大值就可以了
			al.add( (content.length() < maxTextLen) ? content : content.substring(0, maxTextLen) + " ..." );			
		}
		
		StringBuffer sb = new StringBuffer();
		for(String s:al){
			sb.append(s);
		}
		
		return sb.toString();
	}
	
	public static String formatKeyWord_1(String content,String[] keywords,int between,int maxTextLen , String prefix,String posfix){
		if(content == null){
			return null;
		}

		for(int i=0;i<keywords.length;i++)
		{
			content = content.replace(keywords[i], prefix + keywords[i] + posfix);
		}
		
		Matcher matcher = Pattern.compile("("+prefix + ".+?" + posfix + ")" , Pattern.DOTALL).matcher(content);
		
		ArrayList<String> keyWordsArray = new ArrayList<String>();
		while(matcher.find()){
			keyWordsArray.add(matcher.group(1).trim());
		}
		
		
		String[] segText = content.split(prefix + ".+?" + posfix);
		int totalLen = 0;

		ArrayList<String> al = new ArrayList<String>();
		for(int i=0;i<segText.length;i++){
			String tmpText = segText[i].trim();
			if(tmpText.equals("")){
				continue;
			}
			
			int tmpLen = tmpText.length();
			
			if(totalLen < maxTextLen){	//长度还没有到达最大值了			
				if(tmpLen > between){	//如果是超过距离了
					String prefixText = tmpText.substring(0 , between/2 );
					String postfixText = tmpText.substring(tmpLen - between/2 , tmpLen);
					al.add( (i==0?"":prefixText) + " ... " + ((i==segText.length-1)?(tmpText.length()>between ? tmpText.substring(0, between) +"..." : tmpText ):postfixText + keyWordsArray.get(i)));
				}else{		//如果是不足的长度,则直接加入
					al.add(tmpText + keyWordsArray.get(i));
				}
			}else{	//长度已经达到最大值了
				if(tmpLen > between){	//拿这个断点的文本长度判断是不否已经超过between值
					al.add(tmpText.substring(0, between) + "...");
				}else{
					al.add(tmpText);
				}
				break;
			}
			
			totalLen+=tmpLen;
		}
		
		
		if(al.isEmpty()){
			al.add( (content.length() > maxTextLen)?content.substring(0, maxTextLen):content);
		}
		
		StringBuffer sb = new StringBuffer();
		
		for(String s:al){
			sb.append(s);
		}
		
		return sb.toString();
	}
	
	
	public static void main(String[] args) {
		String s = FileUtils.readMsg("E:/dddd.txt", "GBK");
//		String s = "他们生活在日新月异的信息时代，他们承载着家人的期望，他们的生活比父辈们优越，他们对新事物有很强烈的好奇心，他们的成长与互联网息息相关。他们正在迅速成长，他们被称为“90后”。对于尚未成人的“90后”子女，家长们怎么看呢？这里有5位家长的话，他们表达了成人世界对“90后”的“不完全评价”。最不能理解现在的孩子，简直没法沟通，她写的日记想偷看都没用，根本看不懂。“我”不说“我”，偏偏说“莪”，“谢谢”说“3Q”，这还是我最近才弄明白的。(陈女士，45岁，孩子读高一)";
		
		long l = System.currentTimeMillis();
		//formatKeyWord(EB_Content, keywords, 15 , 50 , prefix , postfix);
		String t = formatKeyWord_1(s,new String[]{"90","后"}, 12, 50, "<B>", "</B>");
		l = System.currentTimeMillis() - l;
		System.out.println("Time:" + l + " ms");
		System.out.println(s);
		System.out.println(t);
		
//		String s = "hello google";
//		int fromIndex = 0;
//		while(fromIndex != -1){
//			fromIndex++;
//			fromIndex = s.indexOf("g", fromIndex);
//			System.out.println("fromIndex:" + fromIndex);
//		}
	}
}
