package com.madana.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.net.util.Base64;
import org.bouncycastle.util.io.TeeOutputStream;

import com.madana.common.utils.ApplicationOutputStream;
import com.madana.common.utils.handler.ConfigHandler;

public abstract class MDNApplication 
{
	public static ExecutorService systemService = Executors.newCachedThreadPool();
	public static String username ="";
	protected static String password;
	static String restURI;
	private static RequestRestClient apiClient;
	public static  ConfigHandler oConfig;
	private static File configFolder;
	static File logfile = new File("log.txt");
	public  abstract String getApplicationName();
	public abstract List<SettingsWrapper> getSettingsWrapper();
	public MDNApplication() 
	{

		try {
			init();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected boolean initConfigFolder()
	{
		File configFolder = new File(getConfigFolderPath());
		if(!configFolder.exists())
		{
			if(!configFolder.mkdirs())
			{
				return false;
			}	
		}
			if(!configFolder.canWrite())
			{
				System.err.println("Got no write privileges on "+configFolder.getPath());
			}
		MDNApplication.configFolder=configFolder;
		return true;
	}
	public static File getConfigFolder()
	{
		return configFolder;
	}
	protected String getConfigFolderPath()
	{
		try 
		{
			return new File("").getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void init() throws FileNotFoundException
	{
		Runtime.getRuntime().addShutdownHook(new Thread() 
		{ 
			public void run() 
			{ 
				System.out.println("Shutting down application"); 
			} 
		}); 
		initConfigFolder();
		try {
			System.out.println("Storing configuration in "+configFolder.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileOutputStream fos = new FileOutputStream(new File(configFolder,logfile.getName()));
		TeeOutputStream myOut=new TeeOutputStream(System.out, fos);
		PrintStream errps = new ApplicationOutputStream(getApplicationName()+" ERROR", System.err, true); 
		PrintStream ps = new ApplicationOutputStream(getApplicationName(),myOut, true); 
		System.setOut(ps);
		System.setErr(errps);

		System.out.println("Initializing ...");
		//Skipping Randmon Key Creation due to usage of keystorehandler upon login
		//		keyPair= new AsymmetricCryptography();
		//		System.out.println("Creating RSA Keypair for identification purposes ...");
		//		keyPair.createKeys();
		//		System.out.println("Identifying as RSA PublicKey "+keyPair.getPublicKeyAsString());
		System.out.println("Reading Configuration ...");
		readConfig();
		if(!hasCredentialsInitialzed())
		{
			System.out.println("Credentials not found in configuration file. Trying environment variables ...");
			loadCredentialsFromEnvironment();

			if(!hasCredentialsInitialzed())
			{
				System.out.println("Credentials not provided as env variables. Starting up unclaimed...");

			}
			else
			{
				System.out.println("Credentials read from environment...");
			}
		}
		else
		{
			System.out.println("Credentials read from configuration...");

		}
	}
	protected ExecutorService getSystemExecutor()
	{
		return systemService;
	}
	public static void reauthenticate()
	{
		try
		{


			System.out.println("Couldn't validate client session");
			System.out.println(" Re-authenticating");
			initClient(restURI, username, password);

			System.out.println(" Re-authenticated!");
		}
		catch(Exception ex1)
		{
			System.out.println("Re-authentication failed "+ex1.getMessage());
		}
	}
	public static List<String> getLog()
	{
		List<String> logEntries = new ArrayList<String>();
		Scanner s;
		try {
			s = new Scanner(new File(configFolder,logfile.getName()));
			s.useDelimiter("\r\n");
			while (s.hasNext()) {
				logEntries.add(s.next());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> sortedLogEntries = new ArrayList<String>();
		for(int i=logEntries.size();i>0;i--)
		{
			sortedLogEntries.add(logEntries.get(i-1));
		}

		return sortedLogEntries;

	}

	public static boolean initClient( String resturi, String usernameToBeUsed, String passwordToBeUsed) throws Exception
	{
		System.out.println("Initializing Client Connection...");
		if(resturi==null)
		{
			resturi=getProperty("RESTURI");
			if(resturi.length()<1)
			{
				resturi="https://staging.api.madana.io/rest";
			}
		}

		System.out.println("Connecting to: "+resturi);
		apiClient=new RequestRestClient(resturi);
		apiClient.logon(usernameToBeUsed,passwordToBeUsed);
		restURI=resturi;
		username=usernameToBeUsed;
		password=passwordToBeUsed;
		System.out.println("User "+ username +" successfully authenticated!");
		KeystoreHandler.init(apiClient, username, password.toCharArray());

		return true;

	}


	public  static RequestRestClient getApiClient() {
		return apiClient;
	}
	protected static void loadCredentialsFromEnvironment() 
	{
		if(password == null  )
		{
			password=getProperty("MDNPASSWORD");
			System.out.println("Loaded password from systemenv");
		}
		if(username == null  )
		{
			username=getProperty("MDNUSERNAME");
			System.out.println("Loaded username("+username+") from systemenv");
		}

	}
	protected static boolean hasCredentialsInitialzed()
	{
		return (password!=null && username!=null);
	}
	protected static boolean storeConfig() throws IOException
	{
		oConfig.setProperty("PASSWORD", Base64.encodeBase64String(password.getBytes()));
		oConfig.setProperty("USERNAME", username);
		oConfig.saveProperties();
		System.out.println("Stored username("+username+") as owner in config");
		return true;
	}
	protected  static void readConfig() 
	{
		try
		{
			System.out.println("Loading Config File");
			oConfig = new ConfigHandler(new File(configFolder,"init.cfg"));

			if( oConfig.getProperty("USERNAME").length()>1)
			{
				username=oConfig.getProperty("USERNAME");
				System.out.println("Username("+username+") has been read from config");
			}
			if( oConfig.getProperty("PASSWORD").length()>1)
			{
				password =new String (Base64.decodeBase64(oConfig.getProperty("PASSWORD").getBytes()));

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static ConfigHandler getConfig()
	{
		return oConfig;
	}
	public static String getProperty (String strKey)
	{
		try
		{
			if (System.getProperty(strKey).length() > 0 && System.getProperty(strKey)!=null)
			{
				return System.getProperty(strKey);
			}
		}
		catch(Exception ex)
		{
		}
		try
		{
			if(System.getenv(strKey).length()>0 && System.getenv(strKey)!= null)
			{
				return System.getenv(strKey);
			}
		}
		catch(Exception ex)
		{

		}

		System.err.println("############################################################");
		System.err.println("UNKNOWN PROPERTY: "+strKey);
		System.err.println("############################################################");
		return "";

	}
}
