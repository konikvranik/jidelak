package net.suteren.android.jidelak.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import android.location.Address;

public class Restaurant implements Identificable {

	private String name;
	private Address address;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	List<Availability> openingHours;
	List<Meal> menu;
	private Long id;
	private List<Source> source;

	public List<Availability> getOpeningHours() {
		return openingHours;
	}

	public List<Availability> getOpeningHours(Calendar day) {
		List<Availability> av = new ArrayList<Availability>();
		for (Availability availability : openingHours) {
			if (testDay(day, availability))
				av.add(availability);
		}
		return av;
	}

	private boolean testDay(Calendar day, Availability availability) {
		if (day == null || availability == null)
			return false;
		return ((availability.getYear() != null
				&& availability.getMonth() != null
				&& availability.getDay() != null
				&& day.get(Calendar.YEAR) == availability.getYear()
				&& day.get(Calendar.MONTH) == availability.getMonth()
				&& day.get(Calendar.YEAR) == availability.getYear() && (availability
				.getDow() == null || day.get(Calendar.DAY_OF_WEEK) == availability
				.getDow())) || (null == availability.getYear()
				&& null == availability.getMonth()
				&& null == availability.getYear()
				&& availability.getDow() != null && day
					.get(Calendar.DAY_OF_WEEK) == availability.getDow()));

	}

	public void setOpeningHours(List<Availability> openingHours) {
		this.openingHours = openingHours;
	}

	public void addOpeningHours(Availability oh) {
		if (openingHours == null)
			openingHours = new ArrayList<Availability>();
		openingHours.add(oh);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setMenu(List<Meal> menu) {
		this.menu = menu;
	}

	public List<Meal> getMenu() {
		return menu;
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

	public List<Meal> getMenu(Calendar day) {
		List<Meal> dailyMenu = new ArrayList<Meal>();

		for (Meal meal : menu) {
			if (testDay(day, meal.getAvailability()))
				dailyMenu.add(meal);
		}
		return dailyMenu;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public static String openingHoursToString(List<Availability> openingHours) {

		StringBuffer sb = new StringBuffer();
		for (Availability availability : openingHours) {
			if (availability.getFrom() != null)
				sb.append(availability.getFrom());
			if (availability.getFrom() != null && availability.getTo() != null)
				sb.append(" – ");
			if (availability.getTo() != null)
				sb.append(availability.getTo());
			if (availability.getFrom() != null || availability.getTo() != null)
				sb.append(", ");
		}

		if (sb.length() > 1)
			sb.delete(sb.length() - 2, sb.length());

		return sb.toString();
	}

	public List<Source> getSource() {
		return source;
	}

	public void setSource(List<Source> source) {
		this.source = source;
	}

	public void addSource(Source source) {
		if (this.source == null)
			this.source = new ArrayList<Source>();
		this.source.add(source);
	}

	public String getTemplateName() {
		return "restaurant-" + getId() + ".template.xsl";
	}
}
