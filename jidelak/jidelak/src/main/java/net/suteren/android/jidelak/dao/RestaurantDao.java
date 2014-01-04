package net.suteren.android.jidelak.dao;

import android.database.Cursor;
import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Restaurant;

public class RestaurantDao extends BaseDao<Restaurant> {

	public static final String TABLE_NAME = "restaurant";

	public RestaurantDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	@Override
	protected Restaurant parseRow(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String[] getColumnNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
