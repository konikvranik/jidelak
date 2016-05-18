package net.suteren.android.jidelak.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;

import java.util.Collection;
import java.util.SortedSet;

import static net.suteren.android.jidelak.dao.SQLiteDataTypes.INTEGER;
import static net.suteren.android.jidelak.dao.SQLiteDataTypes.TEXT;

public class AvailabilityDao extends BaseDao<Availability> {

    private static final String SQL_SEPARATOR = ", ";

    public static final String TABLE_NAME = "availability";

    public static final Column YEAR = new Column("year", INTEGER);
    public static final Column MONTH = new Column("month", INTEGER);
    public static final Column DAY = new Column("day", INTEGER);
    public static final Column DOW = new Column("dow", INTEGER);
    public static final Column RESTAURANT = new Column("restaurant", RestaurantDao.ID.getType(),
            new ForeignKey(RestaurantDao.getTable(), RestaurantDao.ID));
    public static final Column FROM = new Column("from_time", TEXT);
    public static final Column TO = new Column("to_time", TEXT);
    public static final Column DESCRIPTION = new Column("description", TEXT);
    public static final Column CLOSED = new Column("closed", INTEGER);

    static {

        registerTable(TABLE_NAME);

        getTable().addColumn(ID);
        getTable().addColumn(YEAR);
        getTable().addColumn(MONTH);
        getTable().addColumn(DAY);
        getTable().addColumn(DOW);
        getTable().addColumn(FROM);
        getTable().addColumn(TO);
        getTable().addColumn(DESCRIPTION);
        getTable().addColumn(CLOSED);
        getTable().addColumn(RESTAURANT);

    }

    public AvailabilityDao(SQLiteDatabase db) {
        super(db);
    }

    public SortedSet<Availability> findAllDays() {
        return query(DAY + " is not null and " + MONTH + " is not null and "
                        + YEAR + " is not null and " + RESTAURANT + " is null", null,
                YEAR + SQL_SEPARATOR + MONTH + SQL_SEPARATOR + DAY, null, YEAR
                        + SQL_SEPARATOR + MONTH + SQL_SEPARATOR + DAY);
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
        av.setDescription(unpackColumnValue(cursor, DESCRIPTION, String.class));
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

    public SortedSet<Availability> findByRestaurant(long restaurant) {
        return query(RESTAURANT + " = ?",
                new String[]{Long.toString(restaurant)}, null, null, null);
    }

    @Override
    protected ContentValues getValues(Availability obj, boolean updateNull) {
        ContentValues values = new ContentValues();

        if (obj.getDay() != null || updateNull)
            values.put(DAY.getName(), obj.getDay());
        if (obj.getDow() != null || updateNull)
            values.put(DOW.getName(), obj.getDow());
        if (obj.getFrom() != null || updateNull)
            values.put(FROM.getName(), obj.getFrom());
        if (obj.getId() != null || updateNull)
            values.put(ID.getName(), obj.getId());
        if (obj.getMonth() != null || updateNull)
            values.put(MONTH.getName(), obj.getMonth());
        if (obj.getTo() != null || updateNull)
            values.put(TO.getName(), obj.getTo());
        if (obj.getDescription() != null || updateNull)
            values.put(DESCRIPTION.getName(), obj.getDescription());
        if (obj.getYear() != null || updateNull)
            values.put(YEAR.getName(), obj.getYear());
        if (obj.getRestaurant() != null || updateNull)
            values.put(RESTAURANT.getName(), obj.getRestaurant() != null ? obj
                    .getRestaurant().getId() : null);
        return values;
    }

    public static Table getTable() {
        return getTable(TABLE_NAME);
    }

    public void delete(Restaurant r) {
        deleteByRestaurantId(r.getId());
    }

    public void deleteByRestaurantId(Long r) {
        getDatabase().delete(getTableName(), RESTAURANT + " = ?", new String[]{Long.toString(r)});
    }

}
