package cz.pavel.uugooglesync.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;

public class CalendarUtils {
	private static final int MAX_SYNC_DAYS_BEFORE = 28;
	private static final int MAX_SYNC_DAYS_AFTER = 280;
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Europe/Prague");
	private static final SimpleDateFormat GOOGLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ");
	
	private static final Logger log = LogUtils.getLogger(); 

	
	public static Calendar getStartDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		int syncDays = Configuration.getInt(Configuration.Parameters.SYNC_DAYS_BEFORE, 7);
		if (syncDays > MAX_SYNC_DAYS_BEFORE) {
			syncDays = MAX_SYNC_DAYS_BEFORE;
			log.warn("Sync days before reduced to " + MAX_SYNC_DAYS_BEFORE);
		}
		calendar.add(Calendar.DATE, -syncDays);
		
		// go the the first day of week
		int dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
		calendar.add(Calendar.DATE, -dayOfWeek);
		
		return calendar;
	}

	public static Calendar getEndDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		int syncDays = Configuration.getInt(Configuration.Parameters.SYNC_DAYS_AFTER, 56);
		if (syncDays > MAX_SYNC_DAYS_AFTER) {
			syncDays = MAX_SYNC_DAYS_AFTER;
			log.warn("Sync days after reduced to " + MAX_SYNC_DAYS_AFTER);
		}
		calendar.add(Calendar.DATE, syncDays);
		
		// go the the first day of week
		int dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
		calendar.add(Calendar.DATE, -dayOfWeek);
		
		return calendar;
	}
	
	public static Calendar getEndDateSunday() {
		Calendar calendar = getEndDate();
		calendar.add(Calendar.DATE, 7);
		calendar.add(Calendar.SECOND, -1);
		return calendar;
	}
	
	public static Calendar stringToDate(String date) {
		Calendar calendar = Calendar.getInstance();
		String dateParts [] = date.split("\\.");
		calendar.clear();
		calendar.set(Calendar.DATE, Integer.parseInt(dateParts[0]));
		calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
		calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));
		return calendar;
	}
	
	public static String formatGoogleDate(EventDateTime date) {
		return date.getDateTime() != null ? (new Date(date.getDateTime().getValue())).toString() : date.getDate();
	}

	
	public static EventDateTime calendarToGoogle(Calendar input) {
		DateTime start = new DateTime(input.getTime(), TIME_ZONE);
		return new EventDateTime().setDateTime(start);
	}
	
	public static Calendar googleToCalendar(EventDateTime input) {
		long time = input.getDateTime().getValue();
		Calendar result = Calendar.getInstance();
		result.setTimeInMillis(time / 1000 * 1000);
		return result;
	}
	
	public static String calendarToGoogleString(Calendar input) {
		GOOGLE_DATE_FORMAT.setTimeZone(TIME_ZONE);
		return GOOGLE_DATE_FORMAT.format(input.getTime());
	}
	
}
