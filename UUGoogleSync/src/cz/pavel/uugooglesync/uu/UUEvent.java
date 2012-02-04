package cz.pavel.uugooglesync.uu;

import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import cz.pavel.uugooglesync.utils.LogUtils;

public class UUEvent {
	
	/** Name of file containing mapping of UU status images to application statuses */ 
	private static final String MAPPING_CONFIGURATION_FILE = "/UUStatusMapping.properties";
	
	/** Mapping of UU status images to application statuses */
	private static Properties statusMapping;
	
	/** Logger */
	private static final Logger log = LogUtils.getLogger();
	
	public enum EventStatus {ACCEPTED, INFORMATION, REJECTED, PARTICIPATED, NOT_PARTICIPATED, PROPOSED, PROBLEM, ATTENTION};
	
	/** Regular expression used to parse time and date of the start and end of the event */
	private static final Pattern DATE_TIME_PATTERN = Pattern.compile("([0-9]*)\\.([0-9]*)\\.([0-9]*) ([0-9]*):([0-9]*) - ([0-9]*)\\.([0-9]*)\\.([0-9]*) ([0-9]*):([0-9]*)");
	
	/** Regular expression used to parse time of the start and end of the event */
	private static final Pattern TIME_PATTERN = Pattern.compile("([0-9]*):([0-9]*) - ([0-9]*):([0-9]*)");

	
	
	private EventStatus status;
	private String id;
	private String summary;
	private String place;
	private Calendar start;
	private Calendar end;
	private boolean blocksTime;
	
	
	
	public UUEvent(String id, String summary, String place, boolean blocksTime) {
		this.id = id;
		this.summary = summary;
		this.place = place;
		this.blocksTime = blocksTime;
	}
	
	
	public boolean getBlocksTime() {
		return blocksTime;
	}

	public void setBlocksTime(boolean blocksTime) {
		this.blocksTime = blocksTime;
	}

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
	
	/**
	 * Sets start and end date based on the input string and optionally date
	 * (if there is no date in the input string).
	 * @param time input time string to parse
	 * @param itemDate input date (used only when the string contains no date)
	 */
	public void setTime(String time, Calendar itemDate) {
		if (time.indexOf(".") >= 0) {
    		// time contains date, it is an event spanning multiple days
    		Matcher matcher = DATE_TIME_PATTERN.matcher(time);
    		matcher.find();
    		Calendar start = Calendar.getInstance();
    		start.clear();
    		start.set(Integer.parseInt(matcher.group(3)), 
    				  Integer.parseInt(matcher.group(2)) - 1, 
    				  Integer.parseInt(matcher.group(1)), 
    				  Integer.parseInt(matcher.group(4)), 
    				  Integer.parseInt(matcher.group(5)));
    		setStart(start);
    		
    		Calendar end = Calendar.getInstance();
    		end.clear();
    		end.set(Integer.parseInt(matcher.group(8)), 
    				Integer.parseInt(matcher.group(7)) - 1, 
    				Integer.parseInt(matcher.group(6)), 
    				Integer.parseInt(matcher.group(9)), 
    				Integer.parseInt(matcher.group(10)));
    		
    		setEnd(end);
    	} else {
    		// there is no date, just parse the time
    		Matcher matcher = TIME_PATTERN.matcher(time);
    		matcher.find();
    		Calendar start = (Calendar)itemDate.clone();
    		start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(1)));
    		start.set(Calendar.MINUTE, Integer.parseInt(matcher.group(2)));
    		setStart(start);
    		
    		Calendar end = (Calendar)itemDate.clone();
    		end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(3)));
    		end.set(Calendar.MINUTE, Integer.parseInt(matcher.group(4)));
    		setEnd(end);
    	}
	}
	

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("UUEvent ");
		result.append("Status: " + status + " ");
		result.append("ID: " + id + " ");
		result.append("Summary: " + summary + " ");
		result.append("Place: " + place + " ");
		result.append("Date: " + start.getTime() + " - " + end.getTime() + " ");
		result.append("Blocking: " + blocksTime);
		return result.toString();
	}
	
	
}
