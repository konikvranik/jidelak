package net.suteren.android.jidelak.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.SortedSet;
import java.util.TreeSet;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Dish;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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

	public static final Column[] availabilityColumns;

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

		availabilityColumns = prepareAvailabilityColumns();

	}

	public MealDao(JidelakDbHelper dbHelper) {
		super(dbHelper);
	}

	public SortedSet<Meal> findByDayAndRestaurant(Calendar day,
			Restaurant restaurant) {
		return rawQuery(
				"select " + columnNamesToClause("m", getTable().getColumns())
						+ ", " + columnNamesToClause("a", availabilityColumns)
						+ " from " + AvailabilityDao.getTable().getName()
						+ " a" + " inner join " + getTableName() + " m"
						+ " on " + " m." + RESTAURANT + "= ? and m."
						+ AVAILABILITY + " = a." + AvailabilityDao.ID
						+ " where" + " (a." + AvailabilityDao.YEAR
						+ " = ? and a." + AvailabilityDao.MONTH + " = ? and a."
						+ AvailabilityDao.DAY + " = ?) or (a."
						+ AvailabilityDao.YEAR + " is null and a."
						+ AvailabilityDao.MONTH + " is null and a."
						+ AvailabilityDao.DAY + " is null and (a."
						+ AvailabilityDao.DOW + " = ? or a."
						+ AvailabilityDao.DOW + " is null))",
				new String[] { String.valueOf(restaurant.getId()),
						String.valueOf(day.get(Calendar.YEAR)),
						String.valueOf(day.get(Calendar.MONTH)),
						String.valueOf(day.get(Calendar.DAY_OF_MONTH)),
						String.valueOf(day.get(Calendar.DAY_OF_WEEK)) });
	}

	public SortedSet<Meal> findByDay(Calendar day) {

		AvailabilityDao.getTable().getColumns();

		return rawQuery(
				"select " + columnNamesToClause("m", getTable().getColumns())
						+ ", " + columnNamesToClause("a", availabilityColumns)
						+ " from " + getTableName() + " m, "
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

	protected static Column[] prepareAvailabilityColumns() {
		ArrayList<Column> ac = new ArrayList<BaseDao.Column>();
		for (Column ca : AvailabilityDao.getTable().getColumns()) {
			boolean add = true;
			for (Column cm : getTable().getColumns()) {
				if (ca.equals(cm)) {
					add = false;
					break;
				}
			}
			if (add)
				ac.add(ca);
		}
		return ac.toArray(new Column[] {});
	}

	public SortedSet<Meal> findOlder(Calendar day) {
		return rawQuery(
				"select " + columnNamesToClause("m", getTable().getColumns())
						+ ", " + columnNamesToClause("a", availabilityColumns)
						+ " from " + getTableName() + " m, "
						+ AvailabilityDao.getTable().getName() + " a where m."
						+ AVAILABILITY + " = a." + AvailabilityDao.ID
						+ " and (" + "a." + AvailabilityDao.YEAR + " < ? "
						+ " or " + "a." + AvailabilityDao.YEAR + " = ? "
						+ " and a." + AvailabilityDao.MONTH + " < ? " + " or "
						+ "a." + AvailabilityDao.YEAR + " = ? " + " and a."
						+ AvailabilityDao.MONTH + " = ? " + " and a."
						+ AvailabilityDao.DAY + " = ?" + ") ",
				new String[] { String.valueOf(day.get(Calendar.YEAR)),
						String.valueOf(day.get(Calendar.YEAR)),
						String.valueOf(day.get(Calendar.MONTH)),
						String.valueOf(day.get(Calendar.YEAR)),
						String.valueOf(day.get(Calendar.MONTH)),
						String.valueOf(day.get(Calendar.DAY_OF_MONTH)) });
	}

	@Override
	public SortedSet<Meal> findAll() {
		return rawQuery(
				"select " + columnNamesToClause("m", getTable().getColumns())
						+ ", " + columnNamesToClause("a", availabilityColumns)
						+ " from " + getTableName() + " m left outer join "
						+ AvailabilityDao.getTable().getName() + " a on m."
						+ AVAILABILITY + " = a." + AvailabilityDao.ID,
				new String[] {});
	}

	@Override
	public Meal findById(long obj) {
		SortedSet<Meal> result = rawQuery(
				"select " + columnNamesToClause("m", getTable().getColumns())
						+ ", " + columnNamesToClause("a", availabilityColumns)
						+ " from " + getTableName() + " m left outer join "
						+ AvailabilityDao.getTable().getName() + " a on m."
						+ AVAILABILITY + " = a." + AvailabilityDao.ID
						+ " and m." + ID + "=?",
				new String[] { Long.toString(obj) });
		if (result.size() > 1)
			throw new SQLiteConstraintException();
		else if (result.isEmpty())
			return null;
		return result.first();
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected Meal parseRow(Cursor cursor) {

		log.debug("Column names: " + Arrays.toString(cursor.getColumnNames()));

		Meal meal = new Meal();
		meal.setId(unpackColumnValue(cursor, ID, Long.class));
		meal.setCategory(unpackColumnValue(cursor, CATEGORY, String.class));
		meal.setTitle(unpackColumnValue(cursor, TITLE, String.class));
		meal.setDescription(unpackColumnValue(cursor, DESCRIPTION, String.class));

		Availability availability = new AvailabilityDao(getDbHelper())
				.parseRow(cursor);
		availability.setId(unpackColumnValue(cursor, AVAILABILITY, Long.class));
		availability.setRestaurant(null);
		meal.setAvailability(availability);
		meal.setDish(unpackColumnValue(cursor, DISH, Dish.class));
		meal.setPrice(unpackColumnValue(cursor, PRICE, String.class));
		meal.setPosition(unpackColumnValue(cursor, POSITION, Integer.class));
		meal.setRestaurant(new Restaurant(unpackColumnValue(cursor, RESTAURANT,
				Long.class)));
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
			//db.close();
		}

	}

	public void deleteOlder(Calendar day) {

		SortedSet<Meal> meals = findOlder(day);
		SortedSet<Availability> avails = new TreeSet<Availability>();
		for (Meal m : meals)
			avails.add(m.getAvailability());

		delete(meals);
		new AvailabilityDao(getDbHelper()).delete(avails);

	}
}
