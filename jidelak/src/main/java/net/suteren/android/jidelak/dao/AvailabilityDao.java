package net.suteren.android.jidelak.dao;

import java.util.Collection;
import java.util.List;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;
import android.content.ContentValues;
import android.database.Cursor;

public class AvailabilityDao extends BaseDao<Availability> {

	public static final String TABLE_NAME = "availability";

	public static final Column YEAR = new Column("year",
			SQLiteDataTypes.INTEGER);
	public static final Column MONTH = new Column("month",
			SQLiteDataTypes.INTEGER);
	public static final Column DAY = new Column("day", SQLiteDataTypes.INTEGER);
	public static final Column DOW = new Column("dow", SQLiteDataTypes.INTEGER);
	public static final Column RESTAURANT = new Column("restaurant",
			RestaurantDao.ID.getType(), new ForeignKey(
					RestaurantDao.getTable(), RestaurantDao.ID));
	public static final Column FROM = new Column("from_time",
			SQLiteDataTypes.TEXT);
	public static final Column TO = new Column("to_time", SQLiteDataTypes.TEXT);
	public static final Column CLOSED = new Column("closed",
			SQLiteDataTypes.INTEGER);

	static {

		registerTable(TABLE_NAME);

		getTable().addColumn(ID);
		getTable().addColumn(YEAR);
		getTable().addColumn(MONTH);
		getTable().addColumn(DAY);
		getTable().addColumn(DOW);
		getTable().addColumn(FROM);
		getTable().addColumn(TO);
		getTable().addColumn(CLOSED);
		getTable().addColumn(RESTAURANT);

	}

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
		av.setDow(unpackColumnValue(cursor, DOW, Integer.class));
		av.setId(unpackColumnValue(cursor, ID, Long.class));
		av.setFrom(unpackColumnValue(cursor, FROM, String.class));
		av.setTo(unpackColumnValue(cursor, TO, String.class));
		av.setRestaurant(unpackColumnValue(cursor, RESTAURANT, Restaurant.class));
		return av;
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String[] getColumnNames() {
		return getTable().getColumnNames();
	}

	public Collection<Availability> findByRestaurant(Restaurant restaurant) {
		return findByRestaurant(restaurant.getId());
	}

	public List<Availability> findByRestaurant(long restaurant) {
		return query(RESTAURANT + " = ?",
				new String[] { Long.toString(restaurant) }, null, null, null);
	}

	@Override
	protected ContentValues getValues(Availability obj) {
		ContentValues values = new ContentValues();
		values.put(DAY.getName(), obj.getDay());
		values.put(DOW.getName(), obj.getDow());
		values.put(FROM.getName(), obj.getFrom());
		values.put(ID.getName(), obj.getId());
		values.put(MONTH.getName(), obj.getMonth());
		values.put(TO.getName(), obj.getTo());
		values.put(YEAR.getName(), obj.getYear());
		values.put(RESTAURANT.getName(), obj.getRestaurant() != null ? obj
				.getRestaurant().getId() : null);
		return values;
	}

	public static Table getTable() {
		return getTable(TABLE_NAME);
	}

}
