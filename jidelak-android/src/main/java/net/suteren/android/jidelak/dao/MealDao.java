package net.suteren.android.jidelak.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Dish;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.SortedSet;
import java.util.TreeSet;

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

    public MealDao(SQLiteDatabase db) {
        super(db);
    }

    public SortedSet<Meal> findByDayAndRestaurant(Calendar day,
                                                  Restaurant restaurant) {
        return rawQuery(
                String.format("select %s, %s from %s a inner join %s m on  m.%s= ? and m.%s = a.%s where (a.%s = ? " +
                                "and a.%s = ? and a.%s = ?) or (a.%s is null and a.%s is null and a.%s is null and (a" +
                                ".%s = ? " +
                                "or a.%s is null))",
                        columnNamesToClause("m", getTable().getColumns()),
                        columnNamesToClause("a", availabilityColumns),
                        AvailabilityDao.getTable().getName(), getTableName(),
                        RESTAURANT, AVAILABILITY, AvailabilityDao.ID, AvailabilityDao.YEAR, AvailabilityDao.MONTH,
                        AvailabilityDao.DAY, AvailabilityDao.YEAR, AvailabilityDao.MONTH, AvailabilityDao.DAY,
                        AvailabilityDao.DOW, AvailabilityDao.DOW),
                new String[]{String.valueOf(restaurant.getId()),
                        String.valueOf(day.get(Calendar.YEAR)),
                        String.valueOf(day.get(Calendar.MONTH)),
                        String.valueOf(day.get(Calendar.DAY_OF_MONTH)),
                        String.valueOf(day.get(Calendar.DAY_OF_WEEK))});
    }

    protected static Column[] prepareAvailabilityColumns() {
        ArrayList<Column> ac = new ArrayList<>();
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
        return ac.toArray(new Column[ac.size()]);
    }

    public SortedSet<Meal> findOlder(Calendar day) {
        return rawQuery(
                String.format("select %s, %s from %s m, %s a where m.%s = a.%s and (a.%s < ?  or (a.%s = ?  and a.%s " +
                                "< ?) or (a.%s = ?  and a.%s = ?  and a.%s < ?)) ",
                        columnNamesToClause("m", getTable().getColumns()),
                        columnNamesToClause("a", availabilityColumns),
                        getTableName(), AvailabilityDao.getTable().getName(),
                        AVAILABILITY, AvailabilityDao.ID, AvailabilityDao.YEAR, AvailabilityDao.YEAR,
                        AvailabilityDao.MONTH, AvailabilityDao.YEAR, AvailabilityDao.MONTH, AvailabilityDao.DAY),
                new String[]{String.valueOf(day.get(Calendar.YEAR)),
                        String.valueOf(day.get(Calendar.YEAR)),
                        String.valueOf(day.get(Calendar.MONTH)),
                        String.valueOf(day.get(Calendar.YEAR)),
                        String.valueOf(day.get(Calendar.MONTH)),
                        String.valueOf(day.get(Calendar.DAY_OF_MONTH))});
    }

    @Override
    public SortedSet<Meal> findAll() {
        return rawQuery(
                String.format("select %s, %s from %s m left outer join %s a on m.%s = a.%s",
                        columnNamesToClause("m", getTable().getColumns()),
                        columnNamesToClause("a", availabilityColumns),
                        getTableName(), AvailabilityDao.getTable().getName(),
                        AVAILABILITY, AvailabilityDao.ID),
                new String[]{});
    }

    @Override
    public Meal findById(long obj) {
        SortedSet<Meal> result = rawQuery(
                String.format("select %s, %s from %s m left outer join %s a on m.%s = a.%s and m.%s=?",
                        columnNamesToClause("m", getTable().getColumns()),
                        columnNamesToClause("a", availabilityColumns),
                        getTableName(), AvailabilityDao.getTable().getName(),
                        AVAILABILITY, AvailabilityDao.ID, ID),
                new String[]{Long.toString(obj)});
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
    public Meal parseRow(Cursor cursor) {

        Meal meal = new Meal();
        meal.setId(unpackColumnValue(cursor, ID, Long.class));
        meal.setCategory(unpackColumnValue(cursor, CATEGORY, String.class));
        meal.setTitle(unpackColumnValue(cursor, TITLE, String.class));
        meal.setDescription(unpackColumnValue(cursor, DESCRIPTION, String.class));

        Availability availability = new AvailabilityDao(getDatabase())
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
                new String[]{Long.toString(obj.getId())});
        db.delete(AvailabilityDao.getTable().getName(), AvailabilityDao.ID
                + " = ?", new String[]{String.format("%d", obj
                .getAvailability().getId())});
    }

    public void delete(Restaurant r) {
        deleteByRestaurantId(r.getId());
    }

    public void deleteByRestaurantId(Long r) {
        SQLiteDatabase db = getDatabase();
        db.delete(AvailabilityDao.getTable().getName(),
                String.format("%s in (select %s from %s where %s = ?)",
                        AvailabilityDao.ID, AVAILABILITY, getTableName(), RESTAURANT),
                new String[]{Long.toString(r)});
        db.delete(getTableName(), RESTAURANT + " = ?",
                new String[]{Long.toString(r)});
    }

    public void deleteOlder(Calendar day) {

        SortedSet<Meal> meals = findOlder(day);
        SortedSet<Availability> avails = new TreeSet<>();
        for (Meal m : meals)
            avails.add(m.getAvailability());

        log.debug(String.format("Deleting %d meals: %s",
                meals.size(), Arrays.toString(new ArrayList<>(meals).toArray())));

        delete(meals);

        log.debug(String.format("Deleting %d avails: %s",
                avails.size(), Arrays.toString(new ArrayList<>(avails).toArray())));
        new AvailabilityDao(getDatabase()).delete(avails);

    }
}
