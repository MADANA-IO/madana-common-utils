package com.madana.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class IPUtils {
	public static String getExternalIP() throws IOException
	{
		String ip =  System.getenv("HOST_IP");
		if(ip==null)
			ip="";
		if(ip.length()<4)
		{
			BufferedReader in = null;
			try {
				URL whatismyip = new URL("http://checkip.amazonaws.com");
				in = new BufferedReader(new InputStreamReader(
						whatismyip.openStream()));
				ip = in.readLine();
	
			} 
			finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(ip.contains(" "))
			ip=ip.substring(0,ip.indexOf(" "));
		return ip;
	}

}
