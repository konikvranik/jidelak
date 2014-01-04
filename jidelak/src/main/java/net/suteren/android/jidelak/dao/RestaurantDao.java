package net.suteren.android.jidelak.dao;

import java.util.Locale;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Restaurant;
import android.database.Cursor;
import android.location.Address;
import android.os.Bundle;

public class RestaurantDao extends BaseDao<Restaurant> {

	public static final String TABLE_NAME = "restaurant";
	public static final String NAME = "name";
	public static final String COUNTRY = "country";
	private static final String CITY = "city";
	private static final String ADDRESS = "address";
	private static final String LONGITUDE = "longitude";
	private static final String LATITUDE = "latitude";
	private static final String ZIP = "zip";
	private static final String PHONE = "phone";
	private static final String WEB = "web";
	private static final String E_MAIL = "email";

	public RestaurantDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	@Override
	protected Restaurant parseRow(Cursor cursor) {
		Restaurant restaurant = new Restaurant();
		restaurant.setId(getColumnValue(cursor, ID, Integer.class));
		restaurant.setName(getColumnValue(cursor, NAME, String.class));
		restaurant.setOpeningHours(new AvailabilityDao(getDbHelper())
				.findByRestaurant(restaurant));

		Address address = new Address(Locale.getDefault());
		address.setCountryName(getColumnValue(cursor, COUNTRY, String.class));
		address.setLocality(getColumnValue(cursor, CITY, String.class));
		String[] addrLines = getColumnValue(cursor, ADDRESS, String.class)
				.split("\n");
		for (int i = 0; i < addrLines.length; i++) {
			address.setAddressLine(i, addrLines[i]);
		}
		address.setLatitude(getColumnValue(cursor, LATITUDE, Double.class));
		address.setLongitude(getColumnValue(cursor, LONGITUDE, Double.class));
		address.setPostalCode(Integer.toString(getColumnValue(cursor, ZIP,
				Integer.class)));
		address.setUrl(getColumnValue(cursor, WEB, String.class));
		address.setPhone(getColumnValue(cursor, PHONE, String.class));
		Bundle b = new Bundle();
		b.putString(E_MAIL, getColumnValue(cursor, E_MAIL, String.class));
		address.setExtras(b);
		restaurant.setAddress(address);
		return restaurant;
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { ID, NAME, ADDRESS, CITY, COUNTRY, ZIP, PHONE,
				WEB, E_MAIL, LATITUDE, LONGITUDE };
	}

}
