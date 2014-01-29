package net.suteren.android.jidelak.dao;

import java.util.Calendar;
import java.util.SortedSet;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Dish;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MealDao extends BaseDao<Meal> {

	public static final Column PRICE = new Column("price", SQLiteDataTypes.TEXT);
	public static final Column DISH = new Column("dish",
			SQLiteDataTypes.INTEGER);
	public static final Column POSITION = new Column("position",
			SQLiteDataTypes.INTEGER);
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
		getTable().addColumn(POSITION);
		getTable().addColumn(DISH);
		getTable().addColumn(CATEGORY);
		getTable().addColumn(RESTAURANT);
		getTable().addColumn(AVAILABILITY);

	}

	public MealDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	public SortedSet<Meal> findByDayAndRestaurant(Calendar day,
			Restaurant restaurant) {
		return rawQuery(
				"select m.* from " + getTableName() + " m, "
						+ AvailabilityDao.getTable().getName() + " a where m."
						+ RESTAURANT + "= ? and m." + AVAILABILITY + " = a."
						+ AvailabilityDao.ID + " and ((a."
						+ AvailabilityDao.YEAR + " = ? and a."
						+ AvailabilityDao.MONTH + " = ? and a."
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
						String.valueOf(day.get(Calendar.DAY_OF_WEEK)) });
	}

	public SortedSet<Meal> findByDay(Calendar day) {
		return rawQuery(
				"select m.* from " + getTableName() + " m, "
						+ AvailabilityDao.getTable().getName() + " a where m."
						+ AVAILABILITY + " = a." + AvailabilityDao.ID
						+ " and ((a." + AvailabilityDao.YEAR + " = ? and a."
						+ AvailabilityDao.MONTH + " = ? and a."
						+ AvailabilityDao.DAY + " = ?) or (a."
						+ AvailabilityDao.YEAR + " is null and a."
						+ AvailabilityDao.MONTH + " is null and a."
						+ AvailabilityDao.DAY + " is null and (a."
						+ AvailabilityDao.DOW + " = ? or a."
						+ AvailabilityDao.DOW + " is null))) ",
				new String[] { String.valueOf(day.get(Calendar.YEAR)),
						String.valueOf(day.get(Calendar.MONTH)),
						String.valueOf(day.get(Calendar.DAY_OF_MONTH)),
						String.valueOf(day.get(Calendar.DAY_OF_WEEK)) });
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
		meal.setDish(unpackColumnValue(cursor, DISH, Dish.class));
		meal.setPrice(unpackColumnValue(cursor, PRICE, String.class));
		meal.setPosition(unpackColumnValue(cursor, POSITION, Integer.class));
		meal.setRestaurant(new RestaurantDao(getDbHelper())
				.findById(unpackColumnValue(cursor, RESTAURANT, Long.class)));
		return meal;
	}

	@Override
	protected String[] getColumnNames() {
		return getTable().getColumnNames();
	}

	@Override
	protected ContentValues getValues(Meal obj, boolean updateNull) {
		ContentValues values = new ContentValues();
		if (obj.getAvailability() != null || updateNull)
			values.put(AVAILABILITY.getName(), obj.getAvailability().getId());
		if (obj.getDescription() != null || updateNull)
			values.put(DESCRIPTION.getName(), obj.getDescription());
		if (obj.getDish() != null || updateNull)
			values.put(DISH.getName(), obj.getDish().ordinal());
		if (obj.getId() != null || updateNull)
			values.put(ID.getName(), obj.getId());
		if (obj.getPosition() != null || updateNull)
			values.put(POSITION.getName(), obj.getPosition());
		if (obj.getTitle() != null || updateNull)
			values.put(TITLE.getName(), obj.getTitle());
		if (obj.getCategory() != null || updateNull)
			values.put(CATEGORY.getName(), obj.getCategory());
		if (obj.getPrice() != null || updateNull)
			values.put(PRICE.getName(), obj.getPrice());
		if (obj.getRestaurant() != null || updateNull)
			values.put(RESTAURANT.getName(), obj.getRestaurant().getId());
		return values;
	}

	public static Table getTable() {
		return getTable(TABLE_NAME);
	}

	protected void delete(SQLiteDatabase db, Meal obj) {
		db.delete(getTableName(), "id = ?",
				new String[] { Long.toString(obj.getId()) });
		db.delete(AvailabilityDao.getTable().getName(), AvailabilityDao.ID
				+ " = ?", new String[] { String.format("%d", obj
				.getAvailability().getId()) });
	}

	public void delete(Restaurant r) {
		SQLiteDatabase db = getDbHelper().getWritableDatabase();
		try {
			db.delete(AvailabilityDao.getTable().getName(), AvailabilityDao.ID
					+ " in (select " + AVAILABILITY + " from " + getTableName()
					+ " where " + RESTAURANT + " = ?)",
					new String[] { Long.toString(r.getId()) });
			db.delete(getTableName(), RESTAURANT + " = ?",
					new String[] { Long.toString(r.getId()) });
		} finally {
			db.close();
		}

	}
}
