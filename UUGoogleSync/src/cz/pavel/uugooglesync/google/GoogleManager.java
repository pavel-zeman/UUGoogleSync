package cz.pavel.uugooglesync.google;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventExtendedProperties;
import com.google.api.services.calendar.model.Events;

import cz.pavel.uugooglesync.utils.CalendarUtils;
import cz.pavel.uugooglesync.utils.Configuration;
import cz.pavel.uugooglesync.utils.LogUtils;

public class GoogleManager {
	
	private static final Logger log = LogUtils.getLogger();

	private static final String DEFAULT_CALENDAR_ID = "primary";
	private static final String UUID = "UUID";
	
	public static final String STATUS_CONFIRMED = "confirmed";
	public static final String STATUS_TENTATIVE = "tentative";
	public static final String TRANSPARENCY_OPAQUE = "opaque";
	public static final String TRANSPARENCY_TRANSPARENT = "transparent";
	
	private Calendar service;
	private String calendarId;
	
	public Map<String, Event> getEvents(java.util.Calendar startDate, java.util.Calendar endDate) throws IOException {
	    HttpTransport httpTransport = new NetHttpTransport();
	    JacksonFactory jsonFactory = new JacksonFactory();
	    
	    // The clientId and clientSecret are copied from the API Access tab on the Google APIs Console
	    String clientId = Configuration.getEncryptedString(Configuration.Parameters.GOOGLE_CLIENT_ID);  
	    String clientSecret = Configuration.getEncryptedString(Configuration.Parameters.GOOGLE_CLIENT_SECRET);
	    calendarId = Configuration.getString(Configuration.Parameters.GOOGLE_CALENDAR_ID, DEFAULT_CALENDAR_ID);
	    if (calendarId.length() == 0) {
	    	calendarId = DEFAULT_CALENDAR_ID;
	    }
	    

	    GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(
	    		Configuration.getEncryptedString(Configuration.Parameters.GOOGLE_ACCESS_TOKEN), httpTransport, jsonFactory, clientId, clientSecret,
	    		Configuration.getEncryptedString(Configuration.Parameters.GOOGLE_REFRESH_TOKEN));
	    

	    service = Calendar.builder(httpTransport, jsonFactory)
	        .setApplicationName("UISGoogleSync")
	        .setHttpRequestInitializer(accessProtectedResource)
	        .build();
	    
	    // get events
	    log.debug("Loading events from " + CalendarUtils.calendarToGoogleString(startDate) + " to " + CalendarUtils.calendarToGoogleString(endDate));
	    Calendar.Events.List eventList = service.events().list(calendarId); 
	    Events events2 = eventList.setTimeMin(CalendarUtils.calendarToGoogleString(startDate)).
	    	setTimeMax(CalendarUtils.calendarToGoogleString(endDate)).
	    	setMaxResults(Integer.valueOf(1000)).setOrderBy("startTime").setSingleEvents(Boolean.TRUE).execute();
	    
	    Map<String, Event> result = new HashMap<String, Event>();
	    // if there are no events, the getItems method returns null (not empty list!)
	    while (events2.getItems() != null) {
		    for (Event event : events2.getItems()) {
		    	if (getId(event) != null) {
			    	log.debug("Start: " + CalendarUtils.formatGoogleDate(event.getStart()) + " End: " + CalendarUtils.formatGoogleDate(event.getEnd()) + " Summary: " + event.getSummary());
		    		result.put(getId(event), event);
		    	}
		    }
		    String pageToken = events2.getNextPageToken();
		    if (pageToken != null && !pageToken.isEmpty()) {
		    	eventList.setPageToken(pageToken);
		    	events2 = eventList.execute();
		    } else {
		    	break;
		    }
	    }
	    
	    log.info("Total events (" + calendarId + "): " + result.size());
	    
	    return result;
	}
	
	public void updateEvent(Event event) throws IOException {
		service.events().update(calendarId, event.getId(), event).execute();
	}
	
	public void insertEvent(Event event) throws IOException {
		service.events().insert(calendarId, event).execute();
	}
	
	public void deleteEvent(String id) throws IOException {
		service.events().delete(calendarId, id).execute();
	}
	
	public void setId(Event event, String id) {
	    EventExtendedProperties p = new EventExtendedProperties();
	    p.setCalendarPrivate(new HashMap<String, String>());
	    p.getCalendarPrivate().put(UUID, id);
	    event.setExtendedProperties(p);
	}
	
	private static String getId(Event event) {
		return event.getExtendedProperties() != null && event.getExtendedProperties().getCalendarPrivate() != null ?  
		    event.getExtendedProperties().getCalendarPrivate().get(UUID) : null;
	}
}
