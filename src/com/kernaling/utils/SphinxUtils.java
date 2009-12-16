package com.kernaling.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sphx.api.SphinxClient;
import org.sphx.api.SphinxMatch;
import org.sphx.api.SphinxResult;
import org.sphx.api.SphinxWordInfo;

/**
 * 
 * @author kwok(kernaling.wong@gmail.com)
 * 			2009-12-06
 * 	对于sphinx的基本类,提供几个查询函数,注册,此类用于单例模式
 *
 */

public class SphinxUtils {
	
	private String host;
	private int port;
	private String index = "main";
	private static SphinxUtils instance = null;
	private SphinxUtils(String host,int port,String index){
		this.host = host;
		this.port = port;
		this.index = index;
	}
	
	public static SphinxUtils getInstance(String host,int port){
		return getInstance(host,port,"main");
	}
	
	public static SphinxUtils getInstance(String host,int port,String index){
		if(instance == null){
			instance = new SphinxUtils(host, port , index);
		}
		return instance;
	}
	
	public void ResultInfo(List<Map<String,Object>> list){
		if(list == null){
			return;
		}
		boolean a = true;
		for(Map<String,Object> m:list){
			if(a){
				System.out.println("Return Msg:" + m);
				a = false;
				continue;
			}
			System.out.println(m);
		}
	}
	
	public List<Map<String,Object>> search(String index,int  limit,String keywords,int offset,String SortBy,int sortByMode,String groupBy,String groupSort){
		SphinxClient cl = new SphinxClient();
		try{
			cl.SetServer ( host, port );
			cl.SetWeights ( new int[] { 100, 1 } );
			cl.SetMatchMode ( SphinxClient.SPH_MATCH_EXTENDED);	//全部都为扩展模式
			cl.SetLimits ( offset, limit );
			cl.SetSortMode ( sortByMode, SortBy );
			groupBy = groupBy == null ? "":groupBy;
			if ( groupBy.length()>0 ){
				cl.SetGroupBy ( groupBy, SphinxClient.SPH_GROUPBY_ATTR, groupSort );
			}
			
			SphinxResult res = cl.Query(keywords, index);
			
			if(res == null){
				return null;
			}
			
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			
			String tKeyWords = "";
			for(int i=0;i<res.words.length;i++){
				SphinxWordInfo wordInfo = res.words[i];
				tKeyWords+=wordInfo.word + (i==res.words.length-1?"":"|");
			}
			Map<String, Object> recordMap = new HashMap<String, Object>();
			
			recordMap.put("Total", res.total);
			recordMap.put("TotalFound", res.totalFound);
			recordMap.put("TimeInUsed", res.time);
			recordMap.put("KeyWords", tKeyWords);
			list.add(recordMap);		//返回的第一条记录包含了所有的返回信息
			for ( int i=0; i<res.matches.length; i++ )
			{
				recordMap = new HashMap<String, Object>();
				SphinxMatch info = res.matches[i];
//				System.out.print ( (i+1) + ". id=" + info.docId + ", weight=" + info.weight );
				recordMap.put("KeyID", info.docId);
				recordMap.put("Weight", info.weight);
				if ( res.attrNames==null || res.attrTypes==null ){					
					continue;
				}

				for ( int a=0; a<res.attrNames.length; a++ )
				{
//					System.out.print ( ", " + res.attrNames[a] + "=" );
					if ( ( res.attrTypes[a] & SphinxClient.SPH_ATTR_MULTI )!=0 )
					{
//						System.out.print ( "(" );
						long[] attrM = (long[]) info.attrValues.get(a);
						if ( attrM!=null )
							for ( int j=0; j<attrM.length; j++ )
						{
							if ( j!=0 )
								System.out.print ( "," );
//							System.out.print ( attrM[j] );
							recordMap.put(res.attrNames[a],attrM[j]);
						}
//						System.out.print ( ")" );

					} else
					{
						switch ( res.attrTypes[a] )
						{
							case SphinxClient.SPH_ATTR_INTEGER:
							case SphinxClient.SPH_ATTR_ORDINAL:
							case SphinxClient.SPH_ATTR_FLOAT:
//							case SphinxClient.SPH_ATTR_BIGINT:
								/* longs or floats; print as is */
//								System.out.print ( info.attrValues.get(a) );
								recordMap.put(res.attrNames[a],info.attrValues.get(a));
								break;

							case SphinxClient.SPH_ATTR_TIMESTAMP:
								Long iStamp = (Long) info.attrValues.get(a);
								Date date = new Date ( iStamp.longValue()*1000 );
//								System.out.print ( date.toString() );
								recordMap.put(res.attrNames[a],date.toString());
								break;

							default:
//								System.out.print ( "(unknown-attr-type=" + res.attrTypes[a] + ")" );
//								recordMap.put(res.attrNames[a],date.toString());
						}
					}
				}
				list.add(recordMap);
			}
			
			return list;
				
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
	}
	
	
	public List<Map<String,Object>> search(String index,int limit,String keywords,int offset){
		return search(index,limit,keywords,offset,null,SphinxClient.SPH_SORT_RELEVANCE,null,null);
	}
	
	public List<Map<String,Object>> search(String index,int limit,String keywords,int offset,String sortBy,int sortMode){
		return search(index,limit,keywords,offset,sortBy,sortMode,null,null);
	}
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param index
	 * @param attr
	 * @param docID
	 * @param value
	 * @return		属性的更新
	 */
	public int UpdateAttribute(String index , String attr ,long docID , long value){
		try{			
			SphinxClient cl = new SphinxClient();
			cl.SetServer ( host, port );
			String key[] = new String[]{attr};
			long values[][] = new long[1][2];
			values[0][0] = docID;
			values[0][1] = value;
			return cl.UpdateAttributes(index, key , values);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return -1;
	}
	
	
	public int UpdateAttribute(String attr ,long docID , long value){
		try{			
			SphinxClient cl = new SphinxClient();
			cl.SetServer ( host, port );
			String key[] = new String[]{attr};
			long values[][] = new long[1][2];
			values[0][0] = docID;
			values[0][1] = value;
			return cl.UpdateAttributes(index, key , values);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return -1;
	}
	
	
	public static void main(String args[]){
		String host = "192.168.1.2";
		int port = 3333;
		
		SphinxUtils utils = SphinxUtils.getInstance(host, port);
		List<Map<String,Object>> l = utils.search("main", 10 , "教育", 0 , "@relevance desc" , SphinxClient.SPH_SORT_EXTENDED , "ci_typeid" , "@count desc");
//		List<Map<String,Object>> l = utils.search("main", 50, "@MB_Title 武大解聘", 0 );

		utils.ResultInfo(l);
	}
}
