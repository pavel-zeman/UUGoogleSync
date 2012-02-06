package cz.pavel.uugooglesync.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class HtmlParser {
	
	/** Default logger */
	private static Logger log = LogUtils.getLogger();
	
	/** Default string encoding */
	private static final String DEFAULT_ENCODING = "UTF-8";
	
	/**
	 * Cache of compiled regexp patterns.
	 */
	private static final Map<String, Pattern> compiledPatterns = new HashMap<String, Pattern>();
	
	public static String getContents(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, DEFAULT_ENCODING));
		StringBuilder result = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			// ignore empty lines
			if (line.trim().length() > 0) {
				result.append(line);
				log.trace(line);
			}
			line = reader.readLine();
		}
		return result.toString();
	}
	
	/**
	 * Just a wrapper for {@link #extractRegExp(String, String, boolean, boolean)}.
	 */
	public static String extractRegExp(String data, String regexp) {
		return extractRegExp(data, regexp, false);
		
	}

	/**
	 * Just a wrapper for {@link #extractRegExp(String, String, boolean, boolean)}.
	 */
	public static String extractRegExp(String data, String regexp, boolean decode) {
		return extractRegExp(data, regexp, decode, true);
		
	}

	
	/**
	 * Extracts value from <tt>data</tt> using a regular expression (<tt>regexp</tt>) and
	 * returns first match of the first group.
	 * 
	 * If there is no match found, it either returns empty string (if <tt>required</tt> is false) 
	 * or throws {@link RuntimeException} (if <tt>required</tt> is true).
	 * 
	 * @param data string to search
	 * @param regexp regular expression to search for 
	 * @param decode if true, replaces HTML entities in the resulting data
	 * @param required indicates, whether a match must be found
	 * @return First match of the first group of the input regular expression, or 
	 * empty string ("") if no match is found and <tt>required</tt> is false.
	 */
	public static String extractRegExp(String data, String regexp, boolean decode, boolean required) {
		Pattern pattern = compiledPatterns.get(regexp);
		if (pattern == null) {
			pattern = Pattern.compile("(?i)" + regexp);
			compiledPatterns.put(regexp, pattern);
		}
		Matcher matcher = pattern.matcher(data);
		String result = matcher.find() ? matcher.group(1).trim() : "";
		if (decode) {
			result = result.replace("&amp;", "&");
		}
		if (required && (result == null || result.length() == 0)) {
			throw new RuntimeException("Cannot extract regular expression " + regexp);
		}
		return result;
	}

}
