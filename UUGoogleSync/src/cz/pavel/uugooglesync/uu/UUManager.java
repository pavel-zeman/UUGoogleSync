package cz.pavel.uugooglesync.uu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import cz.pavel.uugooglesync.utils.CalendarUtils;
import cz.pavel.uugooglesync.utils.Configuration;
import cz.pavel.uugooglesync.utils.HtmlParser;
import cz.pavel.uugooglesync.utils.LogUtils;

public class UUManager {
	
	private static Logger log = LogUtils.getLogger();
	private static final String UIS_BASE_URL = "https://uu.unicornuniverse.eu";
	
	private DefaultHttpClient httpclient;
	
	private static final Pattern DATE_TIME_PATTERN = Pattern.compile("([0-9]*)\\.([0-9]*)\\.([0-9]*) ([0-9]*):([0-9]*) - ([0-9]*)\\.([0-9]*)\\.([0-9]*) ([0-9]*):([0-9]*)");
	private static final Pattern TIME_PATTERN = Pattern.compile("([0-9]*):([0-9]*) - ([0-9]*):([0-9]*)");
	
	// total bytes read from server (it is not precise, it is just an estimate)
	private int totalBytes;
	
	private String doGet(String url) throws ClientProtocolException, IOException {
		url = url.replace("&amp;", "&");
		url = url.replace("|", "%7C");
		HttpGet httpGet = new HttpGet(UIS_BASE_URL + url);
        log.debug("Sending GET request to " + httpGet.getURI());
        HttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String data = HtmlParser.getContents(entity.getContent());
        totalBytes += data.length();
        return data;
	}
	
	private String clickLink(String id, String data) throws IOException {
		return doGet(HtmlParser.extractRegExp(data, "<A[^>]*id=\"" + id + "\"[^>]*href=\"([^\"]*)\""));
	}
	
	
	public Map<String, UUEvent> getEvents(Calendar startDate, Calendar endDate) throws ClientProtocolException, IOException {
		totalBytes = 0;
		log.debug("Loading events from " + CalendarUtils.calendarToGoogleString(startDate) + " to end of " + CalendarUtils.calendarToGoogleString(endDate));
		
		httpclient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(UIS_BASE_URL + "/ues/sesm");
        
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("UES_AccessCode1", Configuration.getEncryptedString(Configuration.Parameters.UU_ACCESS_CODE1)));
        nvps.add(new BasicNameValuePair("UES_AccessCode2", Configuration.getEncryptedString(Configuration.Parameters.UU_ACCESS_CODE2)));
        nvps.add(new BasicNameValuePair("UES_SecurityRealm", "unicornuniverse.eu"));
        nvps.add(new BasicNameValuePair("loginURL", "http://unicornuniverse.eu"));
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        
        HttpResponse response = httpclient.execute(httpost);
        HttpEntity entity = response.getEntity();
        
        log.debug("Sending POST request to " + httpost.getURI());
        String data = HtmlParser.getContents(entity.getContent());
        totalBytes += data.length();
        
        // go to calendar
        data = clickLink("a_toolBar-personal-calendar", data);
        
        // set calendar to week mode
        data = clickLink("dwdiary-switch-week", data);
        
        
        String currentDateString = HtmlParser.extractRegExp(data, "<SPAN class=\"diary-navigation-time-description\">[^<]*</SPAN><SPAN>, ([0-9.]*) ");
        Calendar currentDate = CalendarUtils.stringToDate(currentDateString);

        // go back to the start date
        while (startDate.before(currentDate)) {
	        // go to previous week
	        data = clickLink("dwdiary-link-previous", data);
	        currentDateString = HtmlParser.extractRegExp(data, "<SPAN class=\"diary-navigation-time-description\">[^<]*</SPAN><SPAN>, ([0-9.]*) ");
	        currentDate = CalendarUtils.stringToDate(currentDateString);
        }

        Map<String, UUEvent> result = new HashMap<String, UUEvent>();
        // for all dates
        while (!currentDate.after(endDate)) {
	        // find items
	        int nextIndex = 0;
	        while ((nextIndex = data.indexOf("<DIV class=\"normal-diary-item", nextIndex)) >= 0) {
	        	int itemEndIndex = data.indexOf("diary.addItem", nextIndex);
	        	itemEndIndex = data.indexOf("</SCRIPT>", itemEndIndex);
	        	String itemData = data.substring(nextIndex, itemEndIndex);
	        	String statusImg = HtmlParser.extractRegExp(itemData, "<img src=\".*/([^/]*).gif\"");
	        	String id = HtmlParser.extractRegExp(itemData, "<DIV class=\"normal-diary-item[^\"]*\" id=\"dwdiary_item_([0-9]*)_[0-9]*\"");
	        	String summary = HtmlParser.extractRegExp(itemData, "<img src=[^>]*><SPAN[^>]*>([^<]*)</SPAN");
	        	String place = HtmlParser.extractRegExp(itemData, "<DIV class=\"normal-diary-item-place\">(.*)</DIV><DIV class=\"normal-diary-item-time\"");
	        	if (place.endsWith(",") && place.length() > 1) {
	        		place = place.substring(0, place.length() - 1);
	        	}
	        	String time = HtmlParser.extractRegExp(itemData, "<DIV class=\"normal-diary-item-time\">([^<]*)</DIV>");
	        	String dateIndex = HtmlParser.extractRegExp(itemData, "diary.addItem\\(.*, ([0-9]*),[0-9 ]*\\)");
	        	Calendar itemDate = (Calendar)currentDate.clone();
	        	itemDate.add(Calendar.DATE, Integer.parseInt(dateIndex));
	        	
	        	UUEvent uuEvent = new UUEvent();
	        	uuEvent.setId(id);
	        	uuEvent.setPlace(place);
	        	uuEvent.setStatusImg(statusImg);
	        	uuEvent.setSummary(summary + " (UU)");
	        	
	        	if (time.indexOf(".") >= 0) {
	        		// time contains date
	        		Matcher matcher = DATE_TIME_PATTERN.matcher(time);
	        		matcher.find();
	        		Calendar start = Calendar.getInstance();
	        		start.clear();
	        		start.set(
	        				Integer.parseInt(matcher.group(3)), 
	        				Integer.parseInt(matcher.group(2)) - 1, 
	        				Integer.parseInt(matcher.group(1)), 
	        				Integer.parseInt(matcher.group(4)), 
	        				Integer.parseInt(matcher.group(5)));
	        		uuEvent.setStart(start);
	        		
	        		Calendar end = Calendar.getInstance();
	        		end.clear();
	        		end.set(
	        				Integer.parseInt(matcher.group(8)), 
	        				Integer.parseInt(matcher.group(7)) - 1, 
	        				Integer.parseInt(matcher.group(6)), 
	        				Integer.parseInt(matcher.group(9)), 
	        				Integer.parseInt(matcher.group(10)));
	        		
	        		uuEvent.setEnd(end);
	        		
	        	} else {
	        		Matcher matcher = TIME_PATTERN.matcher(time);
	        		matcher.find();
	        		Calendar start = (Calendar)itemDate.clone();
	        		start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(1)));
	        		start.set(Calendar.MINUTE, Integer.parseInt(matcher.group(2)));
	        		uuEvent.setStart(start);
	        		
	        		Calendar end = (Calendar)itemDate.clone();
	        		end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(3)));
	        		end.set(Calendar.MINUTE, Integer.parseInt(matcher.group(4)));
	        		uuEvent.setEnd(end);
	        	}
	        	
	        	if (uuEvent.getStatus() != UUEvent.EventStatus.REJECTED && uuEvent.getStatus() != UUEvent.EventStatus.NOT_PARTICIPATED) {
	        		result.put(uuEvent.getId(), uuEvent);
		        	log.debug(uuEvent.toString());
	        	}
	        	
	        	nextIndex = itemEndIndex;
	        }
	        
	        // go to next week
	        data = clickLink("dwdiary-link-next", data);
	        currentDateString = HtmlParser.extractRegExp(data, "<SPAN class=\"diary-navigation-time-description\">[^<]*</SPAN><SPAN>, ([0-9.]*) ");
	        currentDate = CalendarUtils.stringToDate(currentDateString);
        }
        
        // logout
        data = doGet(HtmlParser.extractRegExp(data, "<LI id=\"li_menubar-system-logout\"><a href=\"([^\"]*)\""));
        
        log.info("Total KBs read: " + (totalBytes / 1024));
        
        log.info("Total events: " + result.size());
        return result;
	}

}
