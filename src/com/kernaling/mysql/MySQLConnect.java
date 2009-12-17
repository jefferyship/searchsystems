package com.kernaling.mysql;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetMetaData;

public class MySQLConnect {
	private MySQLPool pool = null;
	public MySQLConnect(MySQLPool pool){
		this.pool = pool;
	}
	
	/**
	 * 
	 * @param sql
	 * @return			数据库的查询操作
	 */
	public LinkedList<HashMap<String, Object>> executeQuery(String sql){
		
		Statement st = null;
		ResultSet rs = null;
		while(true){
			Connection conn = null;
			try{
				conn = pool.getConn();
				st = conn.createStatement();
				rs = st.executeQuery(sql);			
				LinkedList<HashMap<String, Object>> reList = new LinkedList<HashMap<String, Object>>();
				
				while(rs.next()){
					ResultSetMetaData  meta = (ResultSetMetaData)rs.getMetaData();
					int len = meta.getColumnCount();
					HashMap<String, Object> rowMap = new HashMap<String, Object>();
					for(int col=0;col<len;col++){
						String colName = meta.getColumnLabel(col+1);
						Object rowValue = rs.getObject(col+1);
						rowMap.put(colName, rowValue);
					}
					if(!rowMap.isEmpty()){
						reList.add(rowMap);
					}
				}
				if(pool != null){				
					pool.add(conn);
					conn = null;
				}
				return reList;
			}catch(Exception ex){
				ex.printStackTrace();
				if(conn != null){
					try{
						conn.clearWarnings();
						conn.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				continue;
			}finally{
				close(st,rs);
			}
		}
	}
	
	/**
	 * 
	 * @param sql
	 * @return			数据库的更新操作
	 */
	public long executeUpdate(String sql){
		Statement st = null;
		ResultSet rs = null;
		Connection conn = null;
		while(true){
			try{
				conn = pool.getConn();
				st = conn.createStatement();
				long returnKey = (long)st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				
				if(pool != null){				
					pool.add(conn);
					conn = null;
				}
				return returnKey;
			}catch(Exception ex){
				ex.printStackTrace();
				if(conn != null){
					try{
						conn.clearWarnings();
						conn.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				continue;
			}finally{
				close(st,rs);
			}
		}
	}
	
	/**
	 * 
	 * @param sql
	 * @return		数据库插入操作		
	 * 
	 */
	public long executeInsert(String sql){
		return executeUpdate(sql);
	}
	
	/**
	 * 
	 * @param sql
	 * @return		prepareSQL的执行操作
	 */
	
	public boolean executePrepareSQL(String sql,Object[] values){
		PreparedStatement st = null;
		ResultSet rs = null;
		try{			
			Connection conn = pool.getConn();
			st = (PreparedStatement)conn.prepareStatement(sql);
			
			int params = values.length;
			for(int i=0;i<params;i++){
				Object tObj = values[i];
				if(tObj == null){
					continue;
				}
				
				if(tObj instanceof String){
					st.setString(i, (String)tObj);
				}else
					if(tObj instanceof Integer){
						st.setInt(i, (Integer)tObj);
					}else
						if(tObj instanceof Long){
							st.setLong(i, (Long)tObj);
						}else
							if(tObj instanceof Date){
								st.setDate(i, (Date)tObj);
							}else{
								st.setObject(i , tObj);
							}
			}
			
			if(pool != null){
				pool.add(conn);
				conn = null;
			}
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			close(st,rs);
		}
		return false;
	}
	
	private void close(Statement st,ResultSet rs){
		
		try{			
			if(st != null){
				st.close();
			}
			
			if(rs != null){
				rs.close();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
