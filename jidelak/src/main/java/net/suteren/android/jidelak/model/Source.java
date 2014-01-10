package net.suteren.android.jidelak.model;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import net.suteren.android.jidelak.dao.Utils;

public class Source implements Identificable {
	TimeType timeType;
	TimeOffsetType offsetBase;
	Integer firstdayofweek;
	Integer offset;
	URL url;
	Restaurant restaurant;
	private Long id;
	Locale locale;
	DateFormat dateFormat;
	private String dateFormatString;
	private String encoding;
	private List<Meal> menu;
	Calendar date;

	public TimeType getTimeType() {
		return timeType;
	}

	public void setTimeType(TimeType time) {
		this.timeType = time;
	}

	public TimeOffsetType getOffsetBase() {
		return offsetBase;
	}

	public void setOffsetBase(TimeOffsetType base) {
		this.offsetBase = base;
	}

	public Integer getFirstdayofweek() {
		return firstdayofweek;
	}

	public void setFirstdayofweek(String firstdayofweek) throws ParseException {
		DateFormat df = new SimpleDateFormat("E", getLocale());
		Calendar cal = Calendar.getInstance();
		cal.setTime(df.parse(firstdayofweek));
		setFirstdayofweek(cal.get(Calendar.DAY_OF_WEEK));
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
		if (locale == null)
			return Locale.getDefault();
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

		if (dateFormat == null) {
			Locale l = getLocale();
			if (l == null)
				l = Locale.getDefault();
			return new SimpleDateFormat("dd.mm.yyyy", l);
		}

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

	public List<Meal> getMenu() {
		return menu;
	}

	public void setMenu(List<Meal> menu) {
		this.menu = menu;
	}

	public void addMenu(Meal meal) {
		if (menu == null)
			menu = new ArrayList<Meal>();
		menu.add(meal);
	}

	public void addMenuAll(Collection<Meal> meal) {
		if (menu == null)
			menu = new ArrayList<Meal>();
		menu.addAll(meal);
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}
}
