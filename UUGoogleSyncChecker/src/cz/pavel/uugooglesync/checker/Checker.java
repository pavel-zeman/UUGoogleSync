package cz.pavel.uugooglesync.checker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException  {
		String inputId = request.getParameter(PARAMETER_NAME);
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
