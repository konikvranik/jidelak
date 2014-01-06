package net.suteren.android.jidelak.dao;

import java.util.Calendar;
import java.util.List;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import android.content.ContentValues;
import android.database.Cursor;

public class MealDao extends BaseDao<Meal> {

	public static final String PRICE = "price";
	public static final String DISH = "dish";
	public static final String CATEGORY = "category";
	public static final String DESCRIPTION = "description";
	public static final String TITLE = "title";
	public static final String RESTAURANT = "restaurant";
	public static final String AVAILABILITY = "availability";
	public static final String TABLE_NAME = "meal";

	public MealDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	public List<Meal> findByDayAndRestaurant(Calendar day, Restaurant restaurant) {
		return query(
				RESTAURANT + "= ? and " + AVAILABILITY
						+ " in ( select id from " + AvailabilityDao.TABLE_NAME
						+ " a where ((a." + AvailabilityDao.YEAR
						+ " = ? and a." + AvailabilityDao.MONTH + " = ? and a."
						+ AvailabilityDao.DAY + " = ?) or (a."
						+ AvailabilityDao.YEAR + " is null and a."
						+ AvailabilityDao.MONTH + " is null and a."
						+ AvailabilityDao.DAY + " is null)) and (a."
						+ AvailabilityDao.DOW + " = ? or a."
						+ AvailabilityDao.DOW + " is null))",
				new String[] { String.valueOf(restaurant.getId()),
						String.valueOf(day.get(Calendar.YEAR)),
						String.valueOf(day.get(Calendar.MONTH)),
						String.valueOf(day.get(Calendar.DAY_OF_MONTH)),
						String.valueOf(day.get(Calendar.DAY_OF_WEEK)) }, null,
				null, DISH + ", " + CATEGORY);
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected Meal parseRow(Cursor cursor) {
		Meal meal = new Meal();
		meal.setId(unpackColumnValue(cursor, ID, Long.class));
		meal.setCategory(unpackColumnValue(cursor, CATEGORY, String.class));
		meal.setTitle(unpackColumnValue(cursor, TITLE, String.class));
		meal.setDescription(unpackColumnValue(cursor, DESCRIPTION, String.class));
		meal.setAvailability(new AvailabilityDao(getDbHelper())
				.findById(unpackColumnValue(cursor, AVAILABILITY, Long.class)));
		meal.setDish(unpackColumnValue(cursor, DISH, String.class));
		meal.setRestaurant(new RestaurantDao(getDbHelper())
				.findById(unpackColumnValue(cursor, RESTAURANT, Long.class)));
		return meal;
	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { ID, TITLE, DESCRIPTION, CATEGORY, DISH, PRICE,
				RESTAURANT, AVAILABILITY };
	}

	@Override
	protected ContentValues getValues(Meal obj) {
		ContentValues values = new ContentValues();
		values.put(AVAILABILITY, obj.getAvailability().getId());
		values.put(DESCRIPTION, obj.getDescription());
		values.put(DISH, obj.getDish());
		values.put(ID, obj.getId());
		values.put(TITLE, obj.getTitle());
		values.put(CATEGORY, obj.getCategory());
		values.put(PRICE, obj.getPrice());
		values.put(RESTAURANT, obj.getRestaurant().getId());
		return values;
	}
}
