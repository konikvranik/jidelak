package net.suteren.android.jidelak.dao;

import java.net.URL;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;
import net.suteren.android.jidelak.model.TimeOffsetType;
import net.suteren.android.jidelak.model.TimeType;
import android.content.ContentValues;
import android.database.Cursor;

public class SourceDao extends BaseDao<Source> {

	public static final Column TIME_TYPE = new Column("timetype",
			SQLiteDataTypes.TEXT);
	public static final Column BASE_TIME = new Column("basedate",
			SQLiteDataTypes.TEXT);
	public static final Column FIRST_DAY_OF_WEEK = new Column("firstdayofweek",
			SQLiteDataTypes.TEXT);
	public static final Column OFFSET = new Column("offset",
			SQLiteDataTypes.INTEGER);
	public static final Column URL = new Column("url", SQLiteDataTypes.TEXT);
	public static final Column RESTAURANT = new Column("restaurant",
			RestaurantDao.ID.getType(), new ForeignKey(
					RestaurantDao.getTable(), RestaurantDao.ID));
	public static final Column LOCALE = new Column("locale",
			SQLiteDataTypes.TEXT);
	public static final Column DATE_FORMAT = new Column("datedofmat",
			SQLiteDataTypes.TEXT);
	public static final Column ENCODING = new Column("encoding",
			SQLiteDataTypes.TEXT);

	public static final String TABLE_NAME = "source";

	static {

		registerTable(TABLE_NAME);

		getTable().addColumn(ID);

		getTable().addColumn(FIRST_DAY_OF_WEEK);
		getTable().addColumn(ENCODING);
		getTable().addColumn(TIME_TYPE);
		getTable().addColumn(BASE_TIME);
		getTable().addColumn(OFFSET);
		getTable().addColumn(LOCALE);
		getTable().addColumn(DATE_FORMAT);
		getTable().addColumn(URL);
		getTable().addColumn(RESTAURANT);
	}

	public SourceDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	public List<Source> findByRestaurant(Restaurant restaurant) {
		return query(RESTAURANT + "= ?",
				new String[] { String.valueOf(restaurant.getId()) }, null,
				null, null);
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected Source parseRow(Cursor cursor) {
		Source source = new Source();
		source.setId(unpackColumnValue(cursor, ID, Long.class));
		source.setTimeType(unpackColumnValue(cursor, TIME_TYPE, TimeType.class));
		source.setOffsetBase(unpackColumnValue(cursor, BASE_TIME,
				TimeOffsetType.class));
		source.setFirstdayofweek(unpackColumnValue(cursor, FIRST_DAY_OF_WEEK,
				Integer.class));
		source.setOffset(unpackColumnValue(cursor, OFFSET, Integer.class));
		source.setUrl(unpackColumnValue(cursor, URL, URL.class));

		Locale locale = unpackColumnValue(cursor, LOCALE, Locale.class);
		source.setLocale(locale);
		setLocale(locale);

		source.setEncoding(unpackColumnValue(cursor, ENCODING, String.class));
		source.setDateFormat(unpackColumnValue(cursor, DATE_FORMAT,
				DateFormat.class));

		Restaurant restaurant = new Restaurant();
		restaurant.setId(unpackColumnValue(cursor, RESTAURANT, Long.class));
		source.setRestaurant(restaurant);
		// source.setRestaurant(new RestaurantDao(getDbHelper())
		// .findById(unpackColumnValue(cursor, RESTAURANT, Integer.class)));

		return source;
	}

	@Override
	protected String[] getColumnNames() {
		return getTable().getColumnNames();
	}

	@Override
	protected ContentValues getValues(Source obj) {
		ContentValues values = new ContentValues();
		values.put(ID.getName(), obj.getId());
		if (obj.getTimeType() != null)
			values.put(TIME_TYPE.getName(), obj.getTimeType().ordinal());
		if (obj.getOffsetBase() != null)
			values.put(BASE_TIME.getName(), obj.getOffsetBase().ordinal());
		values.put(FIRST_DAY_OF_WEEK.getName(), obj.getFirstdayofweek());
		values.put(OFFSET.getName(), obj.getOffset());
		values.put(URL.getName(), obj.getUrl().toString());
		values.put(DATE_FORMAT.getName(), obj.getDateFormatString());
		values.put(LOCALE.getName(), obj.getLocaleString());
		values.put(ENCODING.getName(), obj.getEncoding());
		values.put(RESTAURANT.getName(), obj.getRestaurant().getId());

		return values;
	}

	public static Table getTable() {
		return getTable(TABLE_NAME);
	}
}
