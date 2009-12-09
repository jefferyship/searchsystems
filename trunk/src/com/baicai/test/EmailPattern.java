package com.baicai.test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kernaling.utils.FileUtils;

public class EmailPattern {
	public static void main(String[] args) {
		String filePath = "d:/teamtop.txt";
		String charset = "UTF-8";
		Pattern p = Pattern.compile("'(.+?@.+?)'(?:.*?)'([5|6|8])'", Pattern.DOTALL);
		
		ArrayList<String> result = FileUtils.readBufferLine(filePath, charset);
		HashMap<Integer, ArrayList<String>> classfy = new HashMap<Integer, ArrayList<String>>();
		
		classfy.put(5, new ArrayList<String>());
		classfy.put(6, new ArrayList<String>());
		classfy.put(8, new ArrayList<String>());
		
		for(String msg:result){
			Matcher m = p.matcher(msg);
			if(m.find()){
				String email = m.group(1).trim();
				String template = m.group(2).trim();
				ArrayList<String> col = classfy.get(Integer.parseInt(template));
				col.add(email);
				System.out.println("Email:" + email + "\tTemplate:" + template);
			}
		}
		
		for(Map.Entry<Integer,ArrayList<String>> entry:classfy.entrySet()){
			ArrayList<String> t = entry.getValue();
			int key  = entry.getKey();
			for(String emails:t){
				FileUtils.writeMsg("d:/" + key , emails + "\n", charset , true);
			}
		}
	}
}
