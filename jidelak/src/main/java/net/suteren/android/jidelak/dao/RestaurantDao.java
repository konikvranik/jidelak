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
		String[] addrLines = unpackColumnValue(cursor, ADDRESS, String.class)
				.split("\n");
		for (int i = 0; i < addrLines.length; i++) {
			address.setAddressLine(i, addrLines[i]);
		}
		address.setLatitude(unpackColumnValue(cursor, LATITUDE, Double.class));
		address.setLongitude(unpackColumnValue(cursor, LONGITUDE, Double.class));
		address.setPostalCode(Integer.toString(unpackColumnValue(cursor, ZIP,
				Integer.class)));
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

		Address addr = obj.getAddress();
		if (addr != null) {
			StringBuffer address = new StringBuffer();

			String line = addr.getAddressLine(0);
			for (int i = 0; line != null; line = addr.getAddressLine(++i)) {
				address.append(line);
			}
			values.put(ID, obj.getId());
			values.put(NAME, obj.getName());
			values.put(ADDRESS, address.toString());
			values.put(CITY, addr.getLocality());
			values.put(COUNTRY, addr.getCountryName());
			values.put(ZIP, addr.getPostalCode());
			values.put(PHONE, addr.getPhone());
			values.put(WEB, addr.getUrl());
			if (addr.getExtras() != null)
				values.put(E_MAIL, addr.getExtras().getString(E_MAIL));
			values.put(LATITUDE, addr.getLatitude());
			values.put(LONGITUDE, addr.getLongitude());
		}
		return values;
	}

}
