package net.suteren.android.jidelak.model;

import java.net.URL;
import java.util.Calendar;

public class Source implements Identificable {
	TimeType time;
	Calendar base;
	Integer firstdayofweek;
	Integer offset;
	URL url;
	Restaurant restaurant;
	private Long id;

	public TimeType getTime() {
		return time;
	}

	public void setTimeType(TimeType time) {
		this.time = time;
	}

	public Calendar getBase() {
		return base;
	}

	public void setBase(Calendar base) {
		this.base = base;
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
	this.id=id;
	}

}
