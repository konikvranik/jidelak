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
	public static final String CITY = "city";
	public static final String ADDRESS = "address";
	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	public static final String ZIP = "zip";
	public static final String PHONE = "phone";
	public static final String WEB = "web";
	public static final String E_MAIL = "email";

	public RestaurantDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	@Override
	protected Restaurant parseRow(Cursor cursor) {
		Restaurant restaurant = new Restaurant();
		restaurant.setId(unpackColumnValue(cursor, ID, Long.class));
		restaurant.setName(unpackColumnValue(cursor, NAME, String.class));
		restaurant.setOpeningHours(new AvailabilityDao(getDbHelper())
				.findByRestaurant(restaurant));

		Address address = new Address(Locale.getDefault());
		address.setCountryName(unpackColumnValue(cursor, COUNTRY, String.class));
		address.setLocality(unpackColumnValue(cursor, CITY, String.class));
		String add = unpackColumnValue(cursor, ADDRESS, String.class);
		if (add != null) {
			String[] addrLines = add.split("\n");
			for (int i = 0; i < addrLines.length; i++) {
				address.setAddressLine(i, addrLines[i]);
			}
		}

		Double lat = unpackColumnValue(cursor, LATITUDE, Double.class);
		if (lat != null)
			address.setLatitude(lat);
		Double lon = unpackColumnValue(cursor, LONGITUDE, Double.class);
		if (lon != null)
			address.setLongitude(lon);
		Integer zip = unpackColumnValue(cursor, ZIP, Integer.class);
		if (zip != null)
			address.setPostalCode(Integer.toString(zip));
		address.setUrl(unpackColumnValue(cursor, WEB, String.class));
		address.setPhone(unpackColumnValue(cursor, PHONE, String.class));
		Bundle b = new Bundle();
		b.putString(E_MAIL, unpackColumnValue(cursor, E_MAIL, String.class));
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

		values.put(ID, obj.getId());
		values.put(NAME, obj.getName());

		Address addr = obj.getAddress();
		if (addr != null) {
			StringBuffer address = new StringBuffer();
			String line = addr.getAddressLine(0);
			for (int i = 0; line != null; line = addr.getAddressLine(++i)) {
				address.append(line);
			}
			values.put(ADDRESS, address.toString());
			values.put(CITY, addr.getLocality());
			values.put(COUNTRY, addr.getCountryName());
			values.put(ZIP, addr.getPostalCode());
			values.put(PHONE, addr.getPhone());
			values.put(WEB, addr.getUrl());
			if (addr.getExtras() != null)
				values.put(E_MAIL, addr.getExtras().getString(E_MAIL));
			if (addr.hasLatitude())
				values.put(LATITUDE, addr.getLatitude());
			if (addr.hasLongitude())
				values.put(LONGITUDE, addr.getLongitude());
		}
		return values;
	}

}
