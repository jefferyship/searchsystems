package com.kernaling.mysql;

import com.kernaling.utils.ThreadUtils;
import com.mysql.jdbc.Connection;

public class MySQLDeamon extends Thread{
	
	private MySQLPool pool = null;
	private int maxConns = 0;
	public MySQLDeamon(int maxConns,MySQLPool pool){
		this.pool = pool;
		this.maxConns = maxConns;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(true){
			while(pool.size() < maxConns){
				try {
					Connection conn = pool.newConn();
					if(conn != null){
						pool.add(conn);
					}						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(pool.size() >= maxConns){
				ThreadUtils.sleepInSec(2);
			}
		}
		
	
	}
}
