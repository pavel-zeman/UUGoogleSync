package cz.pavel.uugooglesync.utils;

import java.net.ProxySelector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpClientUtils {
	
	/** Http connection timeout (in ms) */
	private static final int HTTP_CONNECTION_TIMEOUT = 10000;
	/** Http socket read timeout (in ms) */
	private static final int HTTP_SO_TIMEOUT = 10000;
	
	
	/**
	 * Returns an initialized http client. The initialization includes:
	 * <ul>
	 * <li>Setting of timeouts</li>
	 * <li>Setting of proxy</li>
	 * </ul>
	 * @return Initialized http client
	 */
	public static DefaultHttpClient getHttpClient() {
		// set timeouts
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, HTTP_SO_TIMEOUT);
		
		// create the client
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		
		// set proxy
        ProxySelectorRoutePlanner routePlanner = 
        	new ProxySelectorRoutePlanner(httpClient.getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
        httpClient.setRoutePlanner(routePlanner);
        
        // and return the client
        return httpClient;
	}

}
