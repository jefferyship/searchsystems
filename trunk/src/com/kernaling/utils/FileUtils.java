package com.kernaling.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FileUtils {
	final private static SimpleDateFormat DayFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static Map<String,String> loadProperties(String filePath,String charset){
		Properties pro = new Properties();
		try {
			pro.load(new InputStreamReader(new FileInputStream(filePath),charset));
			Map<String ,String> map = new HashMap<String, String>();
			for(Map.Entry<Object,Object> entry:pro.entrySet()){
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				map.put(key, value);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String readMsg(String filePath,String charset){
		InputStreamReader isr = null;
		try{
			char buf[] = new char[1024];
			int len = 0;
			isr = new InputStreamReader(new FileInputStream(filePath),charset);
			StringBuffer sb = new StringBuffer();
			while((len=isr.read(buf))!=-1){
				sb.append(new String(buf,0,len));
			}
			return sb.toString();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(isr != null){
				try{
					isr.close();
				}catch(Exception ex){}
			}
		}
		return null;
	}
	
	public static ArrayList<String> readBufferLine(String filePath , String charset){
		BufferedReader isr = null;
		ArrayList<String> t = new ArrayList<String>();
		try{
			isr =new BufferedReader(new InputStreamReader(new FileInputStream(filePath),charset));
			String tmp = null;
			while((tmp=isr.readLine())!=null){
				if(tmp.trim().equals("") || tmp.startsWith("#")){
					continue;
				}
				t.add(tmp);
			}
			return t;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(isr != null){
				try{
					isr.close();
				}catch(Exception ex){}
			}
		}
		return t;
	}
	
	public static void writeMsg(String path,String ms,String charset){
		
		String dirPath = path.substring(0,path.lastIndexOf("/"));
		File dirFile = new File(dirPath);
		dirFile.mkdirs();
		
		OutputStreamWriter osw  = null;
		try{
			osw = new OutputStreamWriter(new FileOutputStream(path),charset);
			osw.write(ms);
			osw.flush();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(osw!=null){
				try {
					osw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void writeMsg(String path,String ms,String charset , boolean append){
		
		String dirPath = path.substring(0,path.lastIndexOf("/"));
		File dirFile = new File(dirPath);
		dirFile.mkdirs();
		
		OutputStreamWriter osw  = null;
		try{
			osw = new OutputStreamWriter(new FileOutputStream(path , append),charset);
			osw.write(ms);
			osw.flush();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(osw!=null){
				try {
					osw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void writeMsg(String path,Reader reader,String charset){
		
		String dirPath = path.substring(0,path.lastIndexOf("/"));
		File dirFile = new File(dirPath);
		dirFile.mkdirs();
		
		char buf[] = new char[1024];
		int len = 0;
		
		OutputStreamWriter osw = null;
		try{
			osw = new OutputStreamWriter(new FileOutputStream(path),charset);
			while((reader.read(buf))!=-1){
				osw.write(new String(buf,0,len));
				osw.flush();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(osw != null){
				try {
					osw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void makeLog(String path,String ms,String charset){
		writeMsg(path,new Date() + "\t"+ ms + "\n",charset);
	}
	
	public static void makeLog(String ms,String charset){
		String tDate = DayFormat.format(new Date());
		String tPath = "./log/" + tDate +"/log.txt";
		writeMsg(tPath,new Date() + "\t"+ ms + "\n",charset);
	}
	
	public static void makeErrorLog(String ms,String charset){
		String tDate = DayFormat.format(new Date());
		String tPath = "./log/" + tDate +"/errLog.txt";
		writeMsg(tPath,new Date() + "\t"+ ms + "\n",charset);
	}
	

	
}
