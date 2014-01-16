package net.suteren.android.jidelak.dao;

import java.util.Locale;
import java.util.TreeSet;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Address;
import android.os.Bundle;

public class RestaurantDao extends BaseDao<Restaurant> {

	public static final String TABLE_NAME = "restaurant";

	public static final Column NAME = new Column("name", SQLiteDataTypes.TEXT);
	public static final Column COUNTRY = new Column("country",
			SQLiteDataTypes.TEXT);
	public static final Column CITY = new Column("city", SQLiteDataTypes.TEXT);
	public static final Column ADDRESS = new Column("address",
			SQLiteDataTypes.TEXT);
	public static final Column LONGITUDE = new Column("longitude",
			SQLiteDataTypes.REAL);
	public static final Column LATITUDE = new Column("latitude",
			SQLiteDataTypes.REAL);
	public static final Column ZIP = new Column("zip", SQLiteDataTypes.INTEGER);
	public static final Column PHONE = new Column("phone", SQLiteDataTypes.TEXT);
	public static final Column WEB = new Column("web", SQLiteDataTypes.TEXT);
	public static final Column E_MAIL = new Column("email",
			SQLiteDataTypes.TEXT);

	static {

		registerTable(TABLE_NAME);

		getTable().addColumn(ID);
		getTable().addColumn(CITY);
		getTable().addColumn(ADDRESS);
		getTable().addColumn(LONGITUDE);
		getTable().addColumn(LATITUDE);
		getTable().addColumn(ZIP);
		getTable().addColumn(COUNTRY);
		getTable().addColumn(PHONE);
		getTable().addColumn(WEB);
		getTable().addColumn(E_MAIL);
		getTable().addColumn(NAME);

	}

	public RestaurantDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	@Override
	protected Restaurant parseRow(Cursor cursor) {
		Restaurant restaurant = new Restaurant();
		restaurant.setId(unpackColumnValue(cursor, ID, Long.class));
		restaurant.setName(unpackColumnValue(cursor, NAME, String.class));
		restaurant
				.setOpeningHours(new TreeSet<Availability>(new AvailabilityDao(
						getDbHelper()).findByRestaurant(restaurant)));

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
		b.putString(E_MAIL.getName(),
				unpackColumnValue(cursor, E_MAIL, String.class));
		address.setExtras(b);
		restaurant.setAddress(address);
		return restaurant;
	}

	@Override
	protected String getTableName() {
		return getTable().getName();
	}

	@Override
	protected String[] getColumnNames() {
		return getTable().getColumnNames();
	}

	@Override
	protected ContentValues getValues(Restaurant obj) {
		ContentValues values = new ContentValues();

		values.put(ID.getName(), obj.getId());
		values.put(NAME.getName(), obj.getName());

		Address addr = obj.getAddress();
		if (addr != null) {
			StringBuffer address = new StringBuffer();
			String line = addr.getAddressLine(0);
			for (int i = 0; line != null; line = addr.getAddressLine(++i)) {
				address.append(line);
			}
			values.put(ADDRESS.getName(), address.toString());
			values.put(CITY.getName(), addr.getLocality());
			values.put(COUNTRY.getName(), addr.getCountryName());
			values.put(ZIP.getName(), addr.getPostalCode());
			values.put(PHONE.getName(), addr.getPhone());
			values.put(WEB.getName(), addr.getUrl());
			if (addr.getExtras() != null)
				values.put(E_MAIL.getName(),
						addr.getExtras().getString(E_MAIL.getName()));
			if (addr.hasLatitude())
				values.put(LATITUDE.getName(), addr.getLatitude());
			if (addr.hasLongitude())
				values.put(LONGITUDE.getName(), addr.getLongitude());
		}
		return values;
	}

	public static Table getTable() {
		return getTable(TABLE_NAME);
	}
}
