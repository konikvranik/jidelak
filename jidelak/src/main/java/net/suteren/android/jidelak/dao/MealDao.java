package net.suteren.android.jidelak.dao;

import java.util.Calendar;
import java.util.List;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import android.content.ContentValues;
import android.database.Cursor;

public class MealDao extends BaseDao<Meal> {

	public static final Column PRICE = new Column("price", SQLiteDataTypes.REAL);
	public static final Column DISH = new Column("dish", SQLiteDataTypes.TEXT);
	public static final Column CATEGORY = new Column("category",
			SQLiteDataTypes.TEXT);
	public static final Column DESCRIPTION = new Column("description",
			SQLiteDataTypes.TEXT);
	public static final Column TITLE = new Column("title", SQLiteDataTypes.TEXT);
	public static final Column RESTAURANT = new Column("restaurant",
			RestaurantDao.ID.getType(), new ForeignKey(
					RestaurantDao.getTable(), RestaurantDao.ID));
	public static final Column AVAILABILITY = new Column("availability",
			AvailabilityDao.ID.getType(), new ForeignKey(
					AvailabilityDao.getTable(), AvailabilityDao.ID));

	public static final String TABLE_NAME = "meal";

	static {

		registerTable(TABLE_NAME);

		getTable().addColumn(ID);
		getTable().addColumn(TITLE);
		getTable().addColumn(DESCRIPTION);
		getTable().addColumn(PRICE);
		getTable().addColumn(DISH);
		getTable().addColumn(CATEGORY);
		getTable().addColumn(RESTAURANT);
		getTable().addColumn(AVAILABILITY);

	}

	public MealDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	public List<Meal> findByDayAndRestaurant(Calendar day, Restaurant restaurant) {
		return query(
				RESTAURANT + "= ? and " + AVAILABILITY
						+ " in ( select id from " + AvailabilityDao.TABLE_NAME
						+ " a where (a." + AvailabilityDao.YEAR
						+ " = ? and a." + AvailabilityDao.MONTH + " = ? and a."
						+ AvailabilityDao.DAY + " = ?) or (a."
						+ AvailabilityDao.YEAR + " is null and a."
						+ AvailabilityDao.MONTH + " is null and a."
						+ AvailabilityDao.DAY + " is null and (a."
						+ AvailabilityDao.DOW + " = ? or a."
						+ AvailabilityDao.DOW + " is null)))",
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
		return getTable().getColumnNames();
	}

	@Override
	protected ContentValues getValues(Meal obj) {
		ContentValues values = new ContentValues();
		values.put(AVAILABILITY.getName(), obj.getAvailability().getId());
		values.put(DESCRIPTION.getName(), obj.getDescription());
		values.put(DISH.getName(), obj.getDish());
		values.put(ID.getName(), obj.getId());
		values.put(TITLE.getName(), obj.getTitle());
		values.put(CATEGORY.getName(), obj.getCategory());
		values.put(PRICE.getName(), obj.getPrice());
		values.put(RESTAURANT.getName(), obj.getRestaurant().getId());
		return values;
	}

	public static Table getTable() {
		return getTable(TABLE_NAME);
	}
}
