package net.suteren.android.jidelak.dao;

import java.util.List;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Availability;
import android.database.Cursor;

public class AvailabilityDao extends BaseDao<Availability> {

	public static final String YEAR = "year";
	public static final String MONTH = "month";
	public static final String DAY = "day";
	private static final String ID = "id";
	public static final String TABLE_NAME = "availability";
	public static final String RESTAURANT = "restaurant";

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
		av.setYear(getColumnValue(cursor, YEAR, Integer.class));
		av.setMonth(getColumnValue(cursor, MONTH, Integer.class));
		av.setDay(getColumnValue(cursor, DAY, Integer.class));
		av.setId(getColumnValue(cursor, ID, Integer.class));
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

}
