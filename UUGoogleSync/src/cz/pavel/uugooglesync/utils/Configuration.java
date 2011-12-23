package cz.pavel.uugooglesync.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Configuration {
	
	private static final Logger log = LogUtils.getLogger();
	
	private static final String CONFIGURATION_FILE = "/UUGoogleSync.properties";
	
	private static Properties properties;
	
	private static final byte [] KEY = { 72, 1, 64, 7, 38, 1 };
	private static final String CHARACTER_ENCODING = "UTF-8";
	
	public static class Parameters {
		public static final String SYNC_INTERVAL = "syncInterval";
		public static final String SYNC_DAYS_BEFORE = "syncDaysBefore";
		public static final String SYNC_DAYS_AFTER = "syncDaysAfter";
		
		public static final String GOOGLE_CLIENT_ID = "google.clientId";
		public static final String GOOGLE_CLIENT_SECRET = "google.clientSecret";
		public static final String GOOGLE_ACCESS_TOKEN = "google.accessToken";
		public static final String GOOGLE_REFRESH_TOKEN = "google.refreshToken";
		public static final String GOOGLE_EMAIL = "google.refreshToken";
		
		public static final String UU_ACCESS_CODE1 = "uu.accessCode1";
		public static final String UU_ACCESS_CODE2 = "uu.accessCode2";
	}
	
	
	public static void readProperties() throws IOException {
		InputStream is = Configuration.class.getResourceAsStream(CONFIGURATION_FILE);
		URL url = Configuration.class.getResource(CONFIGURATION_FILE); // just for logging purposes
		properties = new Properties();
		log.info("Loading properties from " + url);
		properties.load(is);
	}
	
	public static void storeProperties() throws IOException {
		URL url = Configuration.class.getResource(CONFIGURATION_FILE); 
		log.info("Storing properties to " + url);
		FileOutputStream fos = new FileOutputStream(url.getFile()); 
		properties.store(fos, "UUGoogleSync configuration");
		fos.close();
	}
	
	public static void setString(String parameter, String value) {
		properties.setProperty(parameter, value);
	}
	
	public static void setEncryptedString(String parameter, String value) {
		setString(parameter, encryptData(value));
	}
	
	public static void setInt(String parameter, int value) {
		setString(parameter, String.valueOf(value));
	}
	
	public static String getString(String parameter) {
		return getString(parameter, "");
	}
	
	public static String getString(String parameter, String defaultValue) {
		return properties.getProperty(parameter, defaultValue);
	}
	
	public static String getEncryptedString(String parameter) {
		return getEncryptedString(parameter, "");
	}
	
	public static String getEncryptedString(String parameter, String defaultValue) {
		String data = getString(parameter, defaultValue);
		if (data.length() > 0) {
			try {
				data = decryptData(data);
			} catch (IOException e) {
				log.error("Error when decrypting paramter " + parameter, e);
			}
		}
		return data;
	}
	
	public static String decryptData(String data) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		byte [] decoded = decoder.decodeBuffer(data);
		byte [] decrypted = new byte [decoded.length];
		for (int i=0;i<decoded.length;i++) {
			decrypted[i] = (byte)(decoded[i] ^ KEY[i % KEY.length]);
		}
		return new String(decrypted, CHARACTER_ENCODING);
	}
	
	public static String encryptData(String data)  {
		byte[] decoded = new byte [] {};
		try {
			decoded = data.getBytes(CHARACTER_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("Error getting bytes from string", e);
		}
		byte [] encrypted = new byte [decoded.length];
		for (int i=0;i<decoded.length;i++) {
			encrypted[i] = (byte)(decoded[i] ^ KEY[i % KEY.length]);
		}
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(encrypted);
	}
	
	public static int getInt(String parameter) {
		return getInt(parameter, 0);
	}
	
	public static int getInt(String parameter, int defaultValue) {
		String result = getString(parameter);
		try {
			return Integer.parseInt(result);
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}

}
