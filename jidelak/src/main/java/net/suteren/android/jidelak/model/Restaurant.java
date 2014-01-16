package net.suteren.android.jidelak.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

	SortedSet<Availability> openingHours;
	SortedSet<Meal> menu;
	private Long id;
	private Set<Source> source;

	public SortedSet<Availability> getOpeningHours() {
		return openingHours;
	}

	public Set<Availability> getOpeningHours(Calendar day) {
		Set<Availability> av = new TreeSet<Availability>();

		for (Availability availability : openingHours) {
			if (testDay(day, availability))
				av.add(availability);
		}
		return av;
	}

	private boolean testDay(Calendar day, Availability availability) {
		if (day == null)
			return true;
		if (availability == null)
			return false;

		if (availability.getDay() != null
				&& day.get(Calendar.DAY_OF_MONTH) != availability.getDay())
			return false;

		if (availability.getMonth() != null
				&& day.get(Calendar.MONTH) + 1 != availability.getMonth())
			return false;

		if (availability.getYear() != null
				&& day.get(Calendar.YEAR) != availability.getYear())
			return false;

		if (availability.getDow() != null
				&& day.get(Calendar.DAY_OF_WEEK) != availability.getDow())
			return false;

		return true;
	}

	public void setOpeningHours(SortedSet<Availability> openingHours) {
		this.openingHours = openingHours;
	}

	public void addOpeningHours(Availability oh) {
		if (openingHours == null)
			openingHours = new TreeSet<Availability>();
		openingHours.add(oh);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setMenu(SortedSet<Meal> menu) {
		this.menu = menu;
	}

	public SortedSet<Meal> getMenu() {
		return menu;
	}

	public void addMenu(Meal meal) {
		if (menu == null)
			menu = new TreeSet<Meal>();
		menu.add(meal);
	}

	public void addMenuAll(Collection<Meal> meal) {
		if (menu == null)
			menu = new TreeSet<Meal>();
		menu.addAll(meal);
	}

	public SortedSet<Meal> getMenu(Calendar day) {
		SortedSet<Meal> dailyMenu = new TreeSet<Meal>();

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

	public String openingHoursToString() {
		return openingHoursToString(getOpeningHours());
	}

	public static String openingHoursToString(
			Collection<Availability> openingHours) {

		TreeSet<Availability> tm = new TreeSet<Availability>(
				new Comparator<Availability>() {

					@Override
					public int compare(Availability lhs, Availability rhs) {

						if (lhs == null) {
							if (rhs == null) {
								return 0;
							} else {
								return 1;
							}
						} else {
							if (rhs == null) {
								return -1;
							}
						}

						int r = 0;
						if (lhs.getFrom() != null
								&& (r = lhs.getFrom().compareTo(rhs.getFrom())) != 0)
							return r;

						if (lhs.getTo() != null
								&& (r = lhs.getTo().compareTo(rhs.getTo())) != 0)
							return r;

						return 0;
					}
				});

		for (Availability availability : openingHours) {
			tm.add(availability);
		}
		StringBuffer sb = new StringBuffer();
		for (Availability availability : tm) {
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

	public Set<Source> getSource() {
		return source;
	}

	public void setSource(Set<Source> source) {
		this.source = source;
	}

	public void addSource(Source source) {
		if (this.source == null)
			this.source = new HashSet<Source>();
		this.source.add(source);
	}

	public String getTemplateName() {
		return "restaurant-" + getId() + ".template.xsl";
	}

	public String openingHoursToString(Calendar day) {
		return openingHoursToString(getOpeningHours(day));
	}

	public List<Meal> getMenuAsList() {
		if (getMenu() == null)
			return null;
		return new ArrayList<Meal>(getMenu());
	}
}
