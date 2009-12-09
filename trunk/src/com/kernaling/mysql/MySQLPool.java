package com.kernaling.mysql;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import com.mysql.jdbc.Connection;

public class MySQLPool {
	private volatile Vector<Connection> conns = new Vector<Connection>();
	private String connectURL = null;
	private int total = 0;
	private MySQLDeamon mysqlDeamon = null;
	public MySQLPool(String server,String db,int port,String user,String password,int maxSize){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connectURL = "jdbc:mysql://"+server+":"+port+"/"+db+"?user="+user+"&password="+password+"&characterEncoding=utf8&autoReconnect=true";
			mysqlDeamon = new MySQLDeamon(maxSize,this);
			if(mysqlDeamon != null){
				mysqlDeamon.start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection newConn(){
		try {
			Connection tConn = (Connection)DriverManager.getConnection(connectURL);
			if(tConn != null){
				return tConn;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int total(){
		return total;
	}
	
	public Connection getConn(){
		while(true){			
			if(!conns.isEmpty()){
				Connection tConn = conns.remove(0);
				return tConn;
			}else{
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void add(Connection conn){
		if(conn != null){
			conns.add(conn);
			total++;
			synchronized (this) {
				notifyAll();
			}
		}
	}
	
	public int size(){
		return conns.size();
	}
	
	public void close(Connection conn){
		if(conn != null){
			try {
				total--;
				conn.close();
				conn = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}	
}
