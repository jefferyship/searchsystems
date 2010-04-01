package com.baicai.test;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author kwok
 * 		一个高亮的关键字的工具
 *
 */
public class KeywordUtils {
	public static String formatKeyWord(String content,String[] keywords,int between,int maxTextLen ,String prefix,String posfix){
		if(content == null){
			return null;
		}

		content = content.replaceAll("\\s+", " ");
		for(int i=0;i<keywords.length;i++)
		{
			content = content.replace( keywords[i], " " + prefix + keywords[i] + posfix + " ");
		}
		
		Matcher matcher = Pattern.compile("("+prefix + ".+?" + posfix + ")" , Pattern.DOTALL).matcher(content);
		
		ArrayList<String> keyWordsArray = new ArrayList<String>();
		boolean keyWordMatch = false;
		while(matcher.find()){
			keyWordsArray.add(matcher.group(1).trim());
			keyWordMatch = true;
		}
		keyWordsArray.add("");

		String[] segText = content.split(prefix + ".+?" + posfix);
		int totalLen = 0;

		ArrayList<String> al = new ArrayList<String>();
		
		if(keyWordMatch){
			for(int i=0;i<segText.length;i++){
				String tmpText = segText[i].trim();
				tmpText = tmpText.replaceAll("\\s+", "");
				int tmpLen = tmpText.length();
				
				if(totalLen < maxTextLen){	//长度还没有到达最大值了			
					if(tmpLen > between){	//如果是超过距离了
						String prefixText = tmpText.substring(0 , between/2 );
						String postfixText = tmpText.substring(tmpLen - between/2 , tmpLen);
//						al.add( (i==0?"":prefixText) + " ... " + ((i==segText.length-1)?(tmpText.length()>between ? tmpText.substring(0, between) +"..." : tmpText ):postfixText + keyWordsArray.get(i)));
						al.add( (i==0?"":prefixText) + " ... " + ((i!=0&&i==segText.length-1)?"":postfixText + keyWordsArray.get(i)));
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
//		String content = "乱收费离开破伤风上的浪费是的啊的发的看法啊地方阿勒他个通过来，犯嘀咕个是的分斯洛伐克诶是发生的方式额  斯蒂芬是地方我反倒是";
		String content = "<DOE高级试验设计综合应用>招生简章_china-training.com";
		String keywords[] = {"设计"};
		long l = System.currentTimeMillis();
		String result = formatKeyWord(content, keywords, 10 , 60  , "<B>", "</B>");
		l = System.currentTimeMillis() - l;
		
		System.out.println(result);
		System.out.println("用时:" + l + "ms");
	}
}
