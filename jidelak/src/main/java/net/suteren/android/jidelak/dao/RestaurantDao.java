package net.suteren.android.jidelak.dao;

import java.util.Locale;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Restaurant;
import android.content.ContentValues;
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
	private static final String URL = "url";

	public RestaurantDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	@Override
	protected Restaurant parseRow(Cursor cursor) {
		Restaurant restaurant = new Restaurant();
		restaurant.setId(getColumnValue(cursor, ID, Long.class));
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

	@Override
	protected ContentValues getValues(Restaurant obj) {
		ContentValues values = new ContentValues();

		Address addr = obj.getAddress();
		if (addr != null) {
			StringBuffer address = new StringBuffer();

			String line = addr.getAddressLine(0);
			for (int i = 0; line != null; line = addr.getAddressLine(++i)) {
				address.append(line);
			}
			values.put(ADDRESS, address.toString());
			values.put(COUNTRY, addr.getCountryName());
			values.put(CITY, addr.getLocality());
			values.put(LONGITUDE, addr.getLongitude());
			values.put(LATITUDE, addr.getLatitude());
			values.put(ZIP, addr.getPostalCode());
			values.put(PHONE, addr.getPhone());
			values.put(WEB, addr.getUrl());
			if (addr.getExtras() != null)
				values.put(E_MAIL, addr.getExtras().getString(E_MAIL));
		}
		values.put(ID, obj.getId());
		values.put(NAME, obj.getName());
		if (obj.getSourceUrl() != null)
			values.put(URL, obj.getSourceUrl().toString());

		return values;
	}

}
