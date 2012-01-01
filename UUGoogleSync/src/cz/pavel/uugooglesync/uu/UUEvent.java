package cz.pavel.uugooglesync.uu;

import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import org.apache.log4j.Logger;

import cz.pavel.uugooglesync.utils.LogUtils;

public class UUEvent {
	
	/** Name of file containing mapping of UU status images to application statuses */ 
	private static final String MAPPING_CONFIGURATION_FILE = "/UUStatusMapping.properties";
	
	/** Mapping of UU status images to application statuses */
	private static Properties statusMapping;
	
	/** Logger */
	private static final Logger log = LogUtils.getLogger();
	
	public enum EventStatus {ACCEPTED, INFORMATION, REJECTED, PARTICIPATED, NOT_PARTICIPATED, PROPOSED, PROBLEM};
	
	
	private EventStatus status;
	private String id;
	private String summary;
	private String place;
	private Calendar start;
	private Calendar end;
	
	public EventStatus getStatus() {
		return status;
	}
	
	public void setStatus(EventStatus status) {
		this.status = status;
	}
	
	public void setStatusImg(String statusImg) {
		// initialize mapping
		if (statusMapping == null) {
			statusMapping = new Properties();
			try {
				statusMapping.load(UUEvent.class.getResourceAsStream(MAPPING_CONFIGURATION_FILE));
			} catch (IOException e) {
				log.error("Error when loading UU status mapping from file", e);
				throw new RuntimeException("Cannot load status mapping from file", e);
			}
		}
		String uuStatus = statusMapping.getProperty(statusImg);
		if (uuStatus == null) {
			throw new RuntimeException("Invalid status: " + statusImg);
		}
		setStatus(EventStatus.valueOf(uuStatus));
	}
	
	public boolean isConfirmed() {
		return getStatus() == EventStatus.PARTICIPATED || getStatus() == EventStatus.ACCEPTED;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getPlace() {
		return place;
	}
	
	public void setPlace(String place) {
		this.place = place;
	}
	
	public Calendar getStart() {
		return start;
	}
	
	public void setStart(Calendar start) {
		this.start = start;
	}
	
	public Calendar getEnd() {
		return end;
	}
	
	public void setEnd(Calendar end) {
		this.end = end;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("UUEvent ");
		result.append("Status: " + status + " ");
		result.append("ID: " + id + " ");
		result.append("Summary: " + summary + " ");
		result.append("Place: " + place + " ");
		result.append("Date: " + start.getTime() + " - " + end.getTime());
		return result.toString();
	}
	
	
}
