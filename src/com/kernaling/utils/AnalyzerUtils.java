package com.kernaling.utils;

import java.io.StringReader;
import java.util.ArrayList;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

public class AnalyzerUtils {
	
	
	/**
	 * 
	 * @param words
	 * @param isMaxWordLength
	 * @return
	 * 			分词器
	 */
	public static String[] tokenWords(String words,boolean isMaxWordLength){
		
		if(words == null){
			return null;
		}
		
		try{
			ArrayList<String> al = new ArrayList<String>();
			IKSegmentation seg = new IKSegmentation(new StringReader(words),isMaxWordLength);
			Lexeme le = null;
			while((le=seg.next())!=null){
				String leText = le.getLexemeText().trim();
				if(leText.equals("")){
					continue;
				}
				al.add(leText);
			}
			String[] tWords = new String[al.size()];
			al.toArray(tWords);
			return tWords;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}
