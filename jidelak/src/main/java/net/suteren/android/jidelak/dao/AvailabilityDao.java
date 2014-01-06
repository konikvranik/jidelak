package net.suteren.android.jidelak.dao;

import java.util.List;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;
import android.content.ContentValues;
import android.database.Cursor;

public class AvailabilityDao extends BaseDao<Availability> {

	public static final String YEAR = "year";
	public static final String MONTH = "month";
	public static final String DAY = "day";
	public static final String DOW = "dow";
	public static final String TABLE_NAME = "availability";
	public static final String RESTAURANT = "restaurant";
	public static final String FROM = "from_time";
	public static final String TO = "to_time";

	public AvailabilityDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	public List<Availability> findAllDays() {

		return query(DAY + " is not null and " + MONTH + " is not null and "
				+ YEAR + " is not null and " + RESTAURANT + " is null", null,
				YEAR + ", " + MONTH + ", " + DAY, null, YEAR + ", " + MONTH
						+ ", " + DAY);
	}

	@Override
	protected Availability parseRow(Cursor cursor) {
		Availability av = new Availability();
		av.setYear(unpackColumnValue(cursor, YEAR, Integer.class));
		av.setMonth(unpackColumnValue(cursor, MONTH, Integer.class));
		av.setDay(unpackColumnValue(cursor, DAY, Integer.class));
		av.setId(unpackColumnValue(cursor, ID, Long.class));
		return av;
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { ID, DAY, MONTH, YEAR };
	}

	public List<Availability> findByRestaurant(Restaurant restaurant) {
		return findByRestaurant(restaurant.getId());
	}

	public List<Availability> findByRestaurant(long restaurant) {
		return query(RESTAURANT + " = ?",
				new String[] { Long.toString(restaurant) }, null, null, null);
	}

	@Override
	protected ContentValues getValues(Availability obj) {
		ContentValues values = new ContentValues();
		values.put(DAY, obj.getDay());
		values.put(DOW, obj.getDow());
		values.put(FROM, obj.getFrom());
		values.put(ID, obj.getId());
		values.put(MONTH, obj.getMonth());
		values.put(TO, obj.getTo());
		values.put(YEAR, obj.getYear());
		return values;
	}

}
