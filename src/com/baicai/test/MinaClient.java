package com.baicai.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MinaClient {
	public static void main(String[] args) {
		String address = "192.168.1.111";
		int port = 1985;
		
		try{
			
			int i = 0;
			while(true){

				Socket socket = new Socket(address,port);
				
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				BufferedReader dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				String msg = Math.random() +" " +Math.random();
				
				msg = (i++) + "In the late 1970s data-flow diagrams (DFDs) were introduced and popularized for structured analysis and design (Gane and Sarson 1979). DFDs show the flow of  www.agilemodeling.com/artifacts/dataFlowDiagram.htm - 网页快照 - 类似结果 ";
				msg += "dfd - FACE妆点论坛-国内最大的时尚社区新概念社区" ;
				
				dos.writeUTF(msg);
				dos.flush();
//				System.out.println("write:" + msg);
				String c = "";
				StringBuffer sb = new StringBuffer();
				while((c=dis.readLine())!=null){
					sb.append(c);
				}
				
				System.out.println(sb.toString());
				
				if(false){
					break;
				}
				
				dos.close();
				dis.close();
				socket.close();
				
			}
			

			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
