package net.suteren.android.jidelak.model;

import java.util.Calendar;
import java.util.Locale;

public class Availability implements Identificable<Availability> {

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
		if (getYear() != null)
			cal.set(Calendar.YEAR, getYear());
		if (getMonth() != null)
			cal.set(Calendar.MONTH, getMonth());
		if (getDay() != null)
			cal.set(Calendar.DAY_OF_MONTH, getDay());
		if (getDow() != null)
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof Availability) {
			Availability a = (Availability) o;

			if (a.getCalendar() != null)
				if (!a.getCalendar().equals(getCalendar()))
					return false;

			if (a.getClosed() != null)
				if (!a.getClosed().equals(getClosed()))
					return false;

			if (a.getFrom() != null)
				if (!a.getFrom().equals(getFrom()))
					return false;

			if (a.getTo() != null)
				if (!a.getTo().equals(getTo()))
					return false;

			return true;
		} else
			return false;
	}

	@Override
	public int compareTo(Availability another) {

		if (another == null)
			return 1;

		int c = 0;
		if (getCalendar() == null) {
			if (another.getCalendar() != null)
				return -1;
		} else {
			c = getCalendar().compareTo(another.getCalendar());
			if (c != 0)
				return c;
		}

		if (getDow() == null) {
			if (another.getDow() != null)
				return -1;
		} else {
			if (another.getDow() == null)
				return 1;
			c = getDow().compareTo(another.getDow());
			if (c != 0)
				return c;
		}

		if (getClosed() == null) {
			if (another.getClosed() != null)
				return -1;
		} else {
			if (another.getClosed() == null)
				return 1;
			c = getClosed().compareTo(another.getClosed());
			if (c != 0)
				return c;
		}

		if (getFrom() == null) {
			if (another.getFrom() != null)
				return -1;
		} else {
			c = getFrom().compareTo(another.getFrom());
			if (c != 0)
				return c;
		}

		if (getTo() == null) {
			if (another.getTo() != null)
				return -1;
		} else {
			c = getTo().compareTo(another.getTo());
			if (c != 0)
				return c;
		}

		if (getId() == null) {
			if (another.getId() != null)
				return -1;
		} else {
			c = getId().compareTo(another.getId());
			if (c != 0)
				return c;
		}

		return c;
	}
}
