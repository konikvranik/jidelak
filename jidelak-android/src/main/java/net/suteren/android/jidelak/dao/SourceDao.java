package net.suteren.android.jidelak.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;
import net.suteren.android.jidelak.model.TimeOffsetType;
import net.suteren.android.jidelak.model.TimeType;

import java.net.URL;
import java.text.DateFormat;
import java.util.Locale;

import static net.suteren.android.jidelak.dao.SQLiteDataTypes.*;

public class SourceDao extends BaseDao<Source> {

    public static final Column TIME_TYPE = new Column("timetype", TEXT);
    public static final Column BASE_TIME = new Column("basedate", TEXT);
    public static final Column FIRST_DAY_OF_WEEK = new Column("firstdayofweek", TEXT);
    public static final Column OFFSET = new Column("offset", INTEGER);
    public static final Column URL = new Column("url", TEXT);
    public static final Column RESTAURANT = new Column("restaurant", RestaurantDao.ID.getType(),
            new ForeignKey(RestaurantDao.getTable(), RestaurantDao.ID));
    public static final Column LOCALE = new Column("locale", TEXT);
    public static final Column DATE_FORMAT = new Column("datedofmat", TEXT);
    public static final Column ENCODING = new Column("encoding", TEXT);

    public static final String TABLE_NAME = "source";

    static {

        registerTable(TABLE_NAME);

        getTable().addColumn(ID);

        getTable().addColumn(FIRST_DAY_OF_WEEK);
        getTable().addColumn(ENCODING);
        getTable().addColumn(TIME_TYPE);
        getTable().addColumn(BASE_TIME);
        getTable().addColumn(OFFSET);
        getTable().addColumn(LOCALE);
        getTable().addColumn(DATE_FORMAT);
        getTable().addColumn(URL);
        getTable().addColumn(RESTAURANT);
    }

    public SourceDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Source parseRow(Cursor cursor) {
        Source source = new Source();
        source.setId(unpackColumnValue(cursor, ID, Long.class));
        source.setTimeType(unpackColumnValue(cursor, TIME_TYPE, TimeType.class));
        source.setOffsetBase(unpackColumnValue(cursor, BASE_TIME, TimeOffsetType.class));
        source.setFirstdayofweek(unpackColumnValue(cursor, FIRST_DAY_OF_WEEK, Integer.class));
        source.setOffset(unpackColumnValue(cursor, OFFSET, Integer.class));
        source.setUrl(unpackColumnValue(cursor, URL, URL.class));

        Locale locale = unpackColumnValue(cursor, LOCALE, Locale.class);
        source.setLocale(locale);
        setLocale(locale);

        source.setEncoding(unpackColumnValue(cursor, ENCODING, String.class));
        source.setDateFormat(unpackColumnValue(cursor, DATE_FORMAT, DateFormat.class));

        Restaurant restaurant = new Restaurant(unpackColumnValue(cursor, RESTAURANT, Long.class));
        source.setRestaurant(restaurant);

        return source;
    }

    @Override
    protected String[] getColumnNames() {
        return getTable().getColumnNames();
    }

    @Override
    protected ContentValues getValues(Source obj, boolean updateNull) {
        ContentValues values = new ContentValues();
        if (obj.getId() != null || updateNull)
            values.put(ID.getName(), obj.getId());
        if (obj.getTimeType() != null || updateNull)
            values.put(TIME_TYPE.getName(), obj.getTimeType() == null ? null : obj.getTimeType().ordinal());
        if (obj.getOffsetBase() != null || updateNull)
            values.put(BASE_TIME.getName(), obj.getOffsetBase() == null ? null : obj.getOffsetBase().ordinal());
        if (obj.getFirstdayofweek() != null || updateNull)
            values.put(FIRST_DAY_OF_WEEK.getName(), obj.getFirstdayofweek());
        if (obj.getOffset() != null || updateNull)
            values.put(OFFSET.getName(), obj.getOffset());
        if (obj.getUrl() != null || updateNull)
            values.put(URL.getName(), obj.getUrl().toString());
        if (obj.getDateFormat() != null || updateNull)
            values.put(DATE_FORMAT.getName(), obj.getDateFormatString());
        if (obj.getLocaleString() != null || updateNull)
            values.put(LOCALE.getName(), obj.getLocaleString());
        if (obj.getEncoding() != null || updateNull)
            values.put(ENCODING.getName(), obj.getEncoding());
        if (obj.getRestaurant() != null || updateNull)
            values.put(RESTAURANT.getName(), obj.getRestaurant().getId());

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
