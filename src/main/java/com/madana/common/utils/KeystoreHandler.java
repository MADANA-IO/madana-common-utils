package com.madana.common.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import com.madana.common.datastructures.MDN_Certificate;
import com.madana.common.restclient.RequestRestClient;
import com.madana.common.security.HashHandler;
import com.madana.common.security.certficate.CertificateHandler;
import com.madana.common.security.crypto.AsymmetricCryptography;


public class KeystoreHandler 
{
	public static String masterFingerprint;
	static 	KeyStore ks;
	private static AsymmetricCryptography keypair;

	public static void init(RequestRestClient oClient, String username, char[] pwd)
	{
		final String masterAlias ="master";
		if(exists(username))
		{
			System.out.println("Keystore exists.. Loading");

			ks = loadKeystore(username,pwd);
			System.out.println("		... loaded");
		}
		else
		{
			System.out.println("No Keystore found.. Creating");
			ks = createKeystore(username,pwd);
			System.out.println("		... created");
		}
		if(! containsAlias(masterAlias))
		{
			System.out.println("No master keypair found.. Creating");

			createMasterKey(username, pwd, oClient);
		}
		else
		{
			System.out.println("Master keypair found.. loading");
			setKeypair(getKeypair(masterAlias,pwd));



		}
		masterFingerprint=getFingerPrint("master");
		System.out.println("Master keypair Fingerprint.. "+masterFingerprint);
	}
	private static String getFingerPrint(String string) {
	
			Map<String, Certificate[]> test;
			try {
				test = getDetailedEntries();
				for (Map.Entry<String, Certificate[]> entry : test.entrySet()) 
				{
					
					if(entry.getKey().contentEquals(string))
					{
						X509Certificate currentCert = (X509Certificate) entry.getValue()[0];
						String fingerprint =HashHandler.generateHash(currentCert.getEncoded());
						return fingerprint;
					}

			
			}
			} catch (KeyStoreException | CertificateEncodingException | NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return "NOT SET";
	}
	public static AsymmetricCryptography getKeypair(String alias,char[] pwd)
	{
		Key key = loadPrivateKey(alias,pwd);
		AsymmetricCryptography keypair = null;
		try 
		{
			keypair = new AsymmetricCryptography();
			System.out.println("		... loaded: "+alias+" : " +key.getFormat() +" "+ key.getAlgorithm() + " "+ HashHandler.generateHash(key.getEncoded()));


			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key.getEncoded());
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privkey = kf.generatePrivate(spec);

			PublicKey pubkey =   loadPublicKey(alias);
			keypair = new AsymmetricCryptography(pubkey,privkey);;
			System.out.println("		... loaded: "+alias+" PUBLIC : " +keypair.getPublicKey().getFormat() +" "+ keypair.getPublicKey().getAlgorithm() + " "+ HashHandler.generateHash(keypair.getPublicKey().getEncoded()));
			System.out.println("		... loaded: "+alias+" PRIVATE : " +keypair.getPrivateKey().getFormat() +" "+ keypair.getPrivateKey().getAlgorithm() + " "+ HashHandler.generateHash(keypair.getPrivateKey().getEncoded()));

		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keypair;
	}
	public static void createMasterKey(String username, char[] pwd, RequestRestClient client)
	{

		AsymmetricCryptography newKeyPair;


		System.out.println("	... created");
		try 
		{
			newKeyPair = new AsymmetricCryptography();
			newKeyPair.createKeys();
			System.out.println("Requesting root certificate");
			MDN_Certificate rootCertificateEncoded = client.getRootCertificate();
			String decodedCert = org.glassfish.jersey.internal.util.Base64.decodeAsString(rootCertificateEncoded.getPem());
			System.out.println("	... root certificate received");
			System.out.println("	... "+decodedCert);
			X509Certificate rootCertificateDecoded = CertificateHandler.convertPEMToCertifcate(decodedCert);	
			String certificateContent="CN="+username;
			System.out.println("Creating PKCS10CertificationRequest signing request using " +certificateContent+ " and publickey " +newKeyPair.getPublicKeyAsString());
			PKCS10CertificationRequest csr = CertificateHandler.createCSR(newKeyPair.getKeypair(), certificateContent);
			System.out.println("	... created");
			System.out.println("Asking server to sign request");
			MDN_Certificate cert = client.sendCertificateSigningRequest(csr);
			String decodedPEM = org.glassfish.jersey.internal.util.Base64.decodeAsString(cert.getPem());
			System.out.println("	... signed");
			System.out.println("Trying to store master");
			storePrivateKey(username, "master", newKeyPair.getPrivateKey(), pwd, rootCertificateDecoded, CertificateHandler.convertPEMToCertifcate(decodedPEM));
			System.out.println("	... stored");

		} catch (OperatorCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	public static void storeSymmetricKey(String username, String alias, String key ,char[] keyPwd)
	{
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(key);
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
		KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(originalKey);
		KeyStore.ProtectionParameter password  = new KeyStore.PasswordProtection(keyPwd);
		try {
			ks.setEntry(alias, secret, password);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		save(username,keyPwd);
	}
	public static boolean containsAlias(String alias)
	{
		try {
			if (ks.containsAlias(alias))
				return true;
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public static PublicKey loadPublicKey(String alias)
	{
		try {
			return ks.getCertificateChain(alias)[0].getPublicKey();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Key loadPrivateKey(String alias, char[] pwd)
	{
		try {
			return ks.getKey(alias, pwd);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static List<String> getEntries() 
	{
		Enumeration<String> aliases;
		List<String> keys = new ArrayList<String>();
		try {
			aliases = ks.aliases();
			while (aliases.hasMoreElements()) {
				keys.add(aliases.nextElement());

			}
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return keys;

	}
	public static List<String> getSymmetricEntries() 
	{
		Enumeration<String> aliases;
		List<String> keys = new ArrayList<String>();
		try {
			aliases = ks.aliases();
			while (aliases.hasMoreElements())
			{
				String entry = aliases.nextElement();
				if(! ks.isCertificateEntry(entry))
				keys.add(entry);

			}
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return keys;

	}
	public static Map<String, Certificate[]> getDetailedEntries() throws KeyStoreException
	{
		List<String> aliases = getEntries();
		Map<String, Certificate[]> certs= new HashMap<String,Certificate[]>();
		for(int i=0 ; i < aliases.size(); i++)
		{
			if(ks.isKeyEntry(aliases.get(i)))
			{
				String alias = aliases.get(i);
				Certificate[] chain = ks.getCertificateChain(alias);
				certs.put(alias,chain);
			}
		}
		return certs;
	}
	public static void printEntries()
	{
		List<String> keys = getEntries();
		for(int i=0; i < keys.size(); i++)
		{
			System.out.println(keys.get(i));
		}

	}
	public Certificate loadCertifiacte(String alias)
	{
		try {
			return ks.getCertificate(alias);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void storeCertificate(String alias, Certificate cert )
	{
		try {
			ks.setCertificateEntry(alias, cert);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void storePrivateKey(String username, String alias, PrivateKey privateKey, char[] pwd, X509Certificate caCert, X509Certificate clientCert)
	{
		X509Certificate[] certificateChain = new X509Certificate[2];
		certificateChain[0] = clientCert;
		certificateChain[1] = caCert;
		try
		{
			ks.setKeyEntry(alias, privateKey, pwd, certificateChain);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		save(username,pwd);

	}
	public static void storePassword(String username, String alias, String password, char[] keystorePassword) throws InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException
	{
        KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(keystorePassword);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        SecretKey generatedSecret =
                factory.generateSecret(new PBEKeySpec(password.toCharArray()));

        ks.setEntry(alias, new KeyStore.SecretKeyEntry(
                generatedSecret), keyStorePP);

		save(username,keystorePassword);

	}
    public static String getPassword(String passwordPassword, String passwordAlias) throws Exception {

        KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(passwordPassword.toCharArray());

        KeyStore.SecretKeyEntry ske =
                (KeyStore.SecretKeyEntry)ks.getEntry(passwordAlias, keyStorePP);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        PBEKeySpec keySpec = (PBEKeySpec)factory.getKeySpec(
                ske.getSecretKey(),
                PBEKeySpec.class);
        return new String(keySpec.getPassword());
    }
	private static void save(String username, char[] pwd)
	{
		try (FileOutputStream fos = new FileOutputStream(new File(MDNApplication.getConfigFolder(),username+".jks")))
		{
			ks.store(fos, pwd);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static KeyStore loadKeystore(String username, char[] pwd)
	{
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance("PKCS12");
			ks.load(new FileInputStream(new File(MDNApplication.getConfigFolder(),username+".jks")), pwd);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ks;
	}
	private static KeyStore createKeystore(String username, char[] pwd) 
	{
		KeyStore ks = null;
		try 
		{

			ks = KeyStore.getInstance("PKCS12");
			ks.load(null, pwd);
			try (FileOutputStream fos = new FileOutputStream(new File(MDNApplication.getConfigFolder(),username+".jks")))
			{
				ks.store(fos, pwd);
			}

		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ks;


	}
	public static boolean exists(String username)
	{
		if (new File(MDNApplication.getConfigFolder(),username+".jks").exists())
			return true;
		return false;
	}
	public static AsymmetricCryptography getKeypair() {
		return keypair;
	}
	public static void setKeypair(AsymmetricCryptography keypair) {
		KeystoreHandler.keypair = keypair;
	}
	public static boolean delete(String alias)
	{
		try {
			ks.deleteEntry(alias);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}


}
