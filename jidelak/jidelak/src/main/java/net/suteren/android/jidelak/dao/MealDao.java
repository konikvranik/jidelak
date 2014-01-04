package net.suteren.android.jidelak.dao;

import java.util.Calendar;
import java.util.List;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import android.database.Cursor;

public class MealDao extends BaseDao<Meal> {

	public static final String PRICE = "price";
	public static final String DISH = "dish";
	public static final String CATEGORY = "category";
	public static final String DESCRIPTION = "description";
	public static final String TITLE = "title";
	public static final String ID = "id";
	public static final String RESTAURANT = "restaurant";
	public static final String AVAILABILITY = "availability";
	public static final String TABLE_NAME = "meal";

	public MealDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	public List<Meal> findByDayAndRestaurant(Calendar day, Restaurant restaurant) {
		return query(
				RESTAURANT
						+ "= ? and "
						+ AVAILABILITY
						+ " in ( select id from availability a where ((a.year = ? and a.month = ? and a.day = ?) or (a.year is null and a.month is null and a.day is null)) and (a.dow = ? or a.dow is null))",
				new String[] { String.valueOf(day.get(Calendar.YEAR)),
						String.valueOf(day.get(Calendar.MONTH)),
						String.valueOf(day.get(Calendar.DAY_OF_MONTH)),
						String.valueOf(day.get(Calendar.DAY_OF_WEEK)) }, null,
				null, "dish, category");
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected Meal parseRow(Cursor cursor) {
		Meal meal = new Meal();
		meal.setId(getColumnValue(cursor, ID, Integer.class));
		meal.setCategory(getColumnValue(cursor, CATEGORY, String.class));
		meal.setTitle(getColumnValue(cursor, TITLE, String.class));
		meal.setDescription(getColumnValue(cursor, DESCRIPTION, String.class));
		meal.setAvailability(new AvailabilityDao(getDbHelper())
				.findById(getColumnValue(cursor, AVAILABILITY, Integer.class)));
		meal.setDish(getColumnValue(cursor, DISH, String.class));
		meal.setRestaurant(new RestaurantDao(getDbHelper())
				.findById(getColumnValue(cursor, RESTAURANT, Integer.class)));
		return meal;
	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { ID, TITLE, DESCRIPTION, CATEGORY, DISH, PRICE,
				RESTAURANT, AVAILABILITY };
	}
}
