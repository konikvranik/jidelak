package net.suteren.android.jidelak.model;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import net.suteren.android.jidelak.dao.Utils;

public class Source implements Identificable {
	TimeType timeType;
	Calendar baseDate;
	Integer firstdayofweek;
	Integer offset;
	URL url;
	Restaurant restaurant;
	private Long id;
	Locale locale;
	DateFormat dateFormat;
	private String dateFormatString;
	private String encoding;

	public TimeType getTimeType() {
		return timeType;
	}

	public void setTimeType(TimeType time) {
		this.timeType = time;
	}

	public Calendar getBaseDate() {
		return baseDate;
	}

	public void setBaseDate(Calendar base) {
		this.baseDate = base;
	}

	public Integer getFirstdayofweek() {
		return firstdayofweek;
	}

	public void setFirstdayofweek(Integer firstdayofweek) {
		this.firstdayofweek = firstdayofweek;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant2) {
		this.restaurant = restaurant2;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Locale getLocale() {
		return locale;
	}

	public String getLocaleString() {
		return getLocale().getLanguage() + "_" + getLocale().getCountry();
	}

	public void setLocale(String locale) {
		this.locale = Utils.stringToLocale(locale);
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormatString = dateFormat;
		this.dateFormat = new SimpleDateFormat(dateFormat, getLocale());
	}

	public String getDateFormatString() {
		return dateFormatString;
	}

	public void setEncoding(String string) {
		this.encoding = string;
	}

	public String getEncoding() {
		return encoding;
	}

}
