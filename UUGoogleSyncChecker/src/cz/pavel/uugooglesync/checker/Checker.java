package cz.pavel.uugooglesync.checker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Checker
 */
public class Checker extends HttpServlet {

	private static final long serialVersionUID = -4674722662144232234L;
	
	private static final String SUPPORTED_IDS = "/SupportedIds.txt";
	private static final String PARAMETER_NAME = "GOOGLE_ID";
	
	private static final int MAXIMUM_MAP_SIZE = 100;
	
	private static final Map<String, Integer> requestCounts = new HashMap<String, Integer>();

	
	@Override
	public void destroy() {
		super.destroy();
		
		log("Terminating servlet, following users were verified: ");
		for (Map.Entry<String, Integer> entry : requestCounts.entrySet()) {
			log(" " + entry.getKey() + " - " + entry.getValue());
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException  {
		
		String inputId = request.getParameter(PARAMETER_NAME);
		log("Checking user " + inputId);
		
		// increase request count
		synchronized (requestCounts) {
			Integer requestCount = requestCounts.get(inputId);
			if (requestCount == null) {
				// if the map is too big, simply remove any element
				if (requestCounts.size() >= MAXIMUM_MAP_SIZE) {
					requestCounts.remove(requestCounts.keySet().iterator().next());
				}
				requestCount = Integer.valueOf(0);
			}
			requestCounts.put(inputId, Integer.valueOf(requestCount.intValue() + 1));
		}

		
		URL url = Checker.class.getResource(SUPPORTED_IDS);
		InputStream is = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.trim().equals(inputId)) {
					response.getWriter().append('1');
					return;
				}
			}
			response.getWriter().append('0');
		} finally {
			is.close();
		}
	}

}
