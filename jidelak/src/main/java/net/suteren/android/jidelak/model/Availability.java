package net.suteren.android.jidelak.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import net.suteren.android.jidelak.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Availability implements Identificable<Availability> {

	private static final long serialVersionUID = -1830245975723142985L;
	private Integer day;
	private Integer month;
	private Integer year;
	private Integer dow;
	private String from;
	private String to;
	private String description;
	private Long id;
	private Boolean closed;
	private Restaurant restaurant;

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(Availability.class);

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

	public Integer getDowOrder() {

		if (dow == null)
			return null;

		Locale l = getLocale();
		if (l == null)
			l = Locale.getDefault();
		int fdow = Calendar.getInstance(l).getFirstDayOfWeek();

		int r;
		if (dow < fdow)
			r = 7 + dow;
		else
			r = dow;

		return r;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	private Locale locale;

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
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
		if ((c = Utils.compare(getYear(), another.getYear())) != 0)
			return c;

		if ((c = Utils.compare(getMonth(), another.getMonth())) != 0)
			return c;

		if ((c = Utils.compare(getDay(), another.getDay())) != 0)
			return c;

		if ((c = Utils.compare(getDowOrder(), another.getDowOrder())) != 0)
			return c;

		if ((c = Utils.compare(getClosed(), another.getClosed())) != 0)
			return c;

		if ((c = Utils.compare(getFrom(), another.getFrom())) != 0)
			return c;

		if ((c = Utils.compare(getTo(), another.getTo())) != 0)
			return c;

		if ((c = Utils.compare(getDescription(), another.getDescription())) != 0)
			return c;

		if ((c = Utils.compare(getId(), another.getId())) != 0)
			return c;

		return c;
	}

	@Override
	public String toString() {
		return Arrays.toString(new Object[] { getId(), getYear(), getMonth(),
				getDay(), getDow(), getClosed(), getFrom(), getTo(),
				getRestaurant() });
	}

	private void readObject(ObjectInputStream aInputStream)
			throws ClassNotFoundException, IOException {
		aInputStream.defaultReadObject();
	}

	private void writeObject(ObjectOutputStream aOutputStream)
			throws IOException {
		// perform the default serialization for all non-transient, non-static
		// fields
		aOutputStream.defaultWriteObject();
		// aOutputStream.writeObject(resource);
		// aOutputStream.writeObject(args);
	}
}
