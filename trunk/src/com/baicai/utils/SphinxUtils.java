package com.baicai.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sphx.api.SphinxClient;
import org.sphx.api.SphinxMatch;
import org.sphx.api.SphinxResult;


public class SphinxUtils {
	
	private String host;
	private int port;
	private String index = "*";
	private static SphinxUtils instance = null;
	private SphinxUtils(String host,int port,String index){
		this.host = host;
		this.port = port;
		this.index = index;
	}
	
	public static SphinxUtils getInstance(String host,int port){
		return getInstance(host,port,"*");
	}
	
	public static SphinxUtils getInstance(String host,int port,String index){
		if(instance == null){
			instance = new SphinxUtils(host, port , index);
		}
		return instance;
	}
	
	public List<Map<String,Object>> search(String index,int limit,String keywords,int offset,String SortBy,String groupBy,String groupByMode){
		SphinxClient cl = new SphinxClient();
		try{
			cl.SetServer ( host, port );
			cl.SetWeights ( new int[] { 100, 1 } );
			cl.SetMatchMode ( SphinxClient.SPH_MATCH_EXTENDED);	//全部都为扩展模式
			cl.SetLimits ( offset, limit );
			cl.SetSortMode ( SphinxClient.SPH_SORT_RELEVANCE, SortBy );
			if ( groupBy.length()>0 ){
				cl.SetGroupBy ( groupBy, SphinxClient.SPH_GROUPBY_ATTR, groupBy );
			}
			
			SphinxResult res = cl.Query(keywords, index);
			
			if(res == null){
				return null;
			}
			
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			for ( int i=0; i<res.matches.length; i++ )
			{
				Map<String, Object> recordMap = new HashMap<String, Object>();
				SphinxMatch info = res.matches[i];
				System.out.print ( (i+1) + ". id=" + info.docId + ", weight=" + info.weight );
				recordMap.put("KeyID", info.docId);
				recordMap.put("Weight", info.weight);
				if ( res.attrNames==null || res.attrTypes==null ){					
					continue;
				}

				for ( int a=0; a<res.attrNames.length; a++ )
				{
					System.out.print ( ", " + res.attrNames[a] + "=" );
					
					
					
					if ( ( res.attrTypes[a] & SphinxClient.SPH_ATTR_MULTI )!=0 )
					{
						System.out.print ( "(" );
						long[] attrM = (long[]) info.attrValues.get(a);
						if ( attrM!=null )
							for ( int j=0; j<attrM.length; j++ )
						{
							if ( j!=0 )
								System.out.print ( "," );
							System.out.print ( attrM[j] );
//							recordMap.put(res.attrNames[a],attrM[j]);
						}
						System.out.print ( ")" );

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
								System.out.print ( "(unknown-attr-type=" + res.attrTypes[a] + ")" );
						}
					}
				}
			}

				
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
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
	
	public static void main(String args[]){
		String host = "192.168.1.5";
		int port = 3312;
		
		SphinxUtils utils = SphinxUtils.getInstance(host, port);
	}
}
