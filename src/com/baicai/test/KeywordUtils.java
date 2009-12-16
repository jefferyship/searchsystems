package com.baicai.test;

/**
 * 
 * @author kwok
 * 		一个高亮的关键字的工具
 *
 */
public class KeywordUtils {
	public static String formatKeyWord(String content,String[] keywords,int between,String prefix,String posfix){
		if(content == null){
			return null;
		}
		for(int i=0;i<keywords.length;i++)
		{
			content = content.replace(keywords[i], prefix + keywords[i] + posfix);
		}
		
		int maxLen = content.length();
		int min = maxLen;
		int max = 0;
		
		for(int i=0;i<keywords.length;i++)
		{
			int t = content.indexOf(keywords[i]);
			int n = content.lastIndexOf(keywords[i]);
			int tmpLen = keywords[i].length();
			
			if(t-between < min){
				min = t-between;
			}
			
			if(n+between+tmpLen>max){
				max = n+between+tmpLen;
			}
			
		}
		
		content = content.substring(min , max);
		if(min > 0){
			content = "..." + content;
		}
		
		if(max < maxLen){
			content = content +"...";
		}

		return content;
	}
	public static void main(String[] args) {
		String content = "乱收费离开破伤风上的浪费是的啊的发的看法啊地方阿勒他个通过来，犯嘀咕个是的分斯洛伐克诶是发生的方式额  斯蒂芬是地方我反倒是";
		String keywords[] = {"看法","是的"};
		String result = formatKeyWord(content, keywords, 10, "<B>", "</B>");
		System.out.println(result);
	}
}
