package net.suteren.android.jidelak.dao;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;
import net.suteren.android.jidelak.model.TimeType;
import android.content.ContentValues;
import android.database.Cursor;

public class SourceDao extends BaseDao<Source> {

	public static final String TIME_TYPE = "time";
	public static final String BASE_TIME = "base";
	public static final String FIRST_DAY_OF_WEEK = "firstdayofweek";
	public static final String OFFSET = "offset";
	public static final String URL = "url";
	public static final String RESTAURANT = "restaurant";
	public static final String TABLE_NAME = "source";

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
		source.setBase(unpackColumnValue(cursor, BASE_TIME, Calendar.class));
		source.setFirstdayofweek(unpackColumnValue(cursor, FIRST_DAY_OF_WEEK,
				Integer.class));
		source.setOffset(unpackColumnValue(cursor, OFFSET, Integer.class));
		source.setUrl(unpackColumnValue(cursor, URL, URL.class));
		source.setRestaurant(new RestaurantDao(getDbHelper())
				.findById(unpackColumnValue(cursor, RESTAURANT, Integer.class)));
		return source;
	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { ID, TIME_TYPE, BASE_TIME, FIRST_DAY_OF_WEEK,
				OFFSET, URL, RESTAURANT };
	}

	@Override
	protected ContentValues getValues(Source obj) {
		ContentValues values = new ContentValues();
		values.put(ID, obj.getId());
		values.put(TIME_TYPE, obj.getTime().ordinal());
		values.put(BASE_TIME, new SimpleDateFormat(BaseDao.DATE_FORMAT,
				BaseDao.LOCALE).format(obj.getBase().getTime()));
		values.put(FIRST_DAY_OF_WEEK, obj.getFirstdayofweek());
		values.put(OFFSET, obj.getOffset());
		values.put(URL, obj.getUrl().toString());
		values.put(RESTAURANT, obj.getRestaurant().getId());
		return values;
	}
}
