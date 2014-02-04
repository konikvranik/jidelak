package net.suteren.android.jidelak.model;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.location.Address;

public class Restaurant implements Identificable<Restaurant> {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(Restaurant.class);
	private String name;
	private Address address;
	private Integer position;

	public Restaurant(Long id) {
		setId(id);
	}

	public Restaurant() {
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	SortedSet<Availability> openingHours;
	SortedSet<Meal> menu = new TreeSet<Meal>();
	private Long id;
	private Set<Source> source;
	private String version;
	private String code;

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

	public String openingHoursToString(Context ctx) {
		return openingHoursToString(ctx, getOpeningHours());
	}

	public static String openingHoursToString(Context ctx,
			Collection<Availability> openingHours) {

		TreeSet<Availability> tm = new TreeSet<Availability>(
				new Comparator<Availability>() {

					@Override
					public int compare(Availability lhs, Availability rhs) {

						if (lhs == null && rhs == null)
							return 0;
						if (lhs != null && rhs == null)
							return 1;
						if (lhs == null && rhs != null)
							return -1;

						int r = 0;

						if ((r = Utils.compare(lhs.getFrom(), rhs.getFrom())) != 0)
							return r;

						r = Utils.compare(lhs.getTo(), rhs.getTo());

						return r;
					}
				});

		for (Availability availability : openingHours) {
			tm.add(availability);
		}
		StringBuffer sb = new StringBuffer();
		for (Availability availability : tm) {

			if (availability.getDescription() != null) {
				sb.append(availability.getDescription());
				sb.append(" ");
			}

			if (availability.getFrom() != null && availability.getTo() == null) {
				sb.append(ctx.getResources().getString(R.string.from));
				sb.append(": ");
			}
			if (availability.getTo() != null && availability.getFrom() == null) {
				sb.append(ctx.getResources().getString(R.string.to));
				sb.append(": ");
			}

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

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getTemplateName() {
		return "restaurant-" + getId() + ".template.xsl";
	}

	public String openingHoursToString(Context ctx, Calendar day) {
		return openingHoursToString(ctx, getOpeningHours(day));
	}

	@Override
	public int compareTo(Restaurant another) {

		if (another == null)
			return 1;

		int r = getPosition() != null ? getPosition().compareTo(
				another.getPosition() == null ? 0 : another.getPosition())
				: (another.getPosition() == null ? 0 : -1);
		if (r != 0)
			return r;

		r = getId() != null ? getId().compareTo(another.getId()) : (another
				.getId() == null ? 0 : -1);
		if (r != 0)
			return r;

		r = getName() != null ? getName().compareTo(another.getName())
				: (another.getName() == null ? 0 : -1);
		if (r != 0)
			return r;

		return r;
	}

	public static void cloneAddress(Address source, Address target) {
		for (int i = 0; i < source.getMaxAddressLineIndex(); i++)
			target.setAddressLine(i, source.getAddressLine(i));
		target.setAdminArea(source.getAdminArea());
		target.setCountryCode(source.getCountryCode());
		target.setCountryName(source.getCountryName());
		target.setExtras(source.getExtras());
		target.setFeatureName(source.getFeatureName());
		target.setLocality(source.getLocality());
		target.setPhone(source.getPhone());
		target.setPostalCode(source.getPostalCode());
		target.setPremises(source.getPremises());
		target.setSubAdminArea(source.getSubAdminArea());
		target.setSubLocality(source.getSubLocality());
		target.setSubThoroughfare(source.getSubThoroughfare());
		target.setThoroughfare(source.getThoroughfare());
		target.setUrl(source.getUrl());
	}

	public void setCode(String string) {
		code = string;
	}

	public String getCode() {
		return code;
	}

	public void setVersion(String string) {
		version = string;
	}

	public String getVersion() {
		return version;
	}

}
