package cz.pavel.uugooglesync.uu;

import java.util.Calendar;

public class UUEvent {
	
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
		if ("S36_C06".equals(statusImg) || "S36_C02".equals(statusImg)) {
			setStatus(EventStatus.PARTICIPATED);
		} else if ("S37_C03".equals(statusImg) || "S37_C04".equals(statusImg)) {
			setStatus(EventStatus.NOT_PARTICIPATED);
		} else if ("S10_C02".equals(statusImg)) {
			setStatus(EventStatus.ACCEPTED);
		} else if ("S02_C01".equals(statusImg)) {
			setStatus(EventStatus.PROPOSED);
		} else if ("S96_C02".equals(statusImg)) {
			setStatus(EventStatus.INFORMATION);
		} else if ("S34_C05".equals(statusImg)) {
			setStatus(EventStatus.PROBLEM);
		} else if ("S09_C05".equals(statusImg)) {
			setStatus(EventStatus.REJECTED);
		} else {
			throw new RuntimeException("Invalid status: " + statusImg);
		}
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
