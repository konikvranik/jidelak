package net.suteren.android.jidelak.model;

import java.util.Calendar;
import java.util.Locale;

public class Availability implements Identificable {

	private Integer day;
	private Integer month;
	private Integer year;
	private Integer dow;
	private String from;
	private String to;
	private Long id;
	private Boolean closed;
	private Restaurant restaurant;

	public Availability(Calendar cal) {
		super();
		setYear(cal.get(Calendar.YEAR));
		setMonth(cal.get(Calendar.MONTH));
		setDay(cal.get(Calendar.DAY_OF_MONTH));
	}

	public Availability() {
		super();
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getDow() {
		return dow;
	}

	public void setDow(Integer dow) {
		this.dow = dow;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Calendar getCalendar() {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.set(Calendar.YEAR, getYear());
		cal.set(Calendar.MONTH, getMonth());
		cal.set(Calendar.DAY_OF_MONTH, getDay());
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public void setClosed(Boolean closed) {
		this.closed = closed;
	}

	public Boolean getClosed() {
		return closed;
	}

	public Restaurant getRestaurant() {
		return this.restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

}
