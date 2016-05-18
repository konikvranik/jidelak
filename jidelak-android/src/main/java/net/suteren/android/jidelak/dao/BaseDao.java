package net.suteren.android.jidelak.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import net.suteren.android.jidelak.Utils;
import net.suteren.android.jidelak.model.Dish;
import net.suteren.android.jidelak.model.Identificable;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.TimeOffsetType;
import net.suteren.android.jidelak.model.TimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class BaseDao<T extends Identificable<T>> {

    protected static Logger log = LoggerFactory.getLogger(BaseDao.class);
    private final SQLiteDatabase database;

    public BaseDao() {
        database=null;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public static class Table {

        private String name;
        private List<Column> columns = new ArrayList<>();
        private List<ForeignKey> foreignKeys = new ArrayList<>();

        public Table(String name) {
            this.name = name;
        }

        public void addColumn(Column col) {
            columns.add(col);
            if (col.getFk() != null) {
                if (col.getFk().isUnset())
                    col.getFk().setColumn(col);
                foreignKeys.add(col.getFk());
            }
        }

        public String createClausule() {
            StringBuilder sb = new StringBuilder("create table ");
            sb.append(name);
            sb.append("(");
            Iterator<Column> ci = columns.iterator();

            while (ci.hasNext()) {
                Column c = ci.next();
                sb.append(c.createClausule());
                if (ci.hasNext())
                    sb.append(",");
            }

            for (ForeignKey fk : foreignKeys) {
                sb.append(",");
                sb.append(fk.createClausule());

            }
            sb.append(")");
            return sb.toString();
        }

        public String getName() {
            return name;
        }

        public String[] getColumnNames() {
            List<String> colNames = new ArrayList<>();
            for (Column col : columns) {
                colNames.add(col.getName());
            }
            return colNames.toArray(new String[colNames.size()]);
        }

        public Column[] getColumns() {
            return columns.toArray(new Column[columns.size()]);
        }
    }

    public static class Column {

        private String name;
        private SQLiteDataTypes type;
        private boolean pk;
        private ForeignKey fk;

        public Column(String name, SQLiteDataTypes type, boolean pk) {
            this(name, type, pk, null);
        }

        public Column(String name, SQLiteDataTypes type, ForeignKey fk) {
            this(name, type, false, fk);
        }

        public Column(String name, SQLiteDataTypes type) {
            this(name, type, false, null);
        }

        public Column(String name, SQLiteDataTypes type, boolean pk,
                      ForeignKey fk) {
            this.name = name;
            this.type = type;
            this.pk = pk;
            this.fk = fk;
        }

        @Override
        public String toString() {
            return getName();
        }

        public String createClausule() {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(" ");
            sb.append(type.getType());
            if (pk)
                sb.append(" primary key");
            return sb.toString();
        }

        public ForeignKey getFk() {
            return fk;
        }

        public String getName() {
            return name;
        }

        public SQLiteDataTypes getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Column && toString().equals(o.toString());
        }

    }

    public static class ForeignKey {

        private Column target;
        private Table table;
        private Column source;

        public ForeignKey(String table, Column column) {
            this.table = getTable(table);
            this.target = column;
        }

        public boolean isUnset() {
            return source == null;
        }

        public ForeignKey(Table table, Column column) {
            this.table = table;
            this.target = column;
        }

        public void setColumn(Column column) {
            this.source = column;
        }

        public Object createClausule() {
            return String.format("foreign key(%s) references %s(%s)",
                    source.getName(), table.getName(), target.getName());
        }
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final Column ID = new Column("id", SQLiteDataTypes.INTEGER,
            true);

    public static final Locale LOCALE = Locale.ENGLISH;

    private static Map<String, Table> tables = new HashMap<>();
    public Locale locale;

    public BaseDao(SQLiteDatabase database) {
        this.database = database;
    }

    public void insert(Collection<T> objs) {
        if (objs == null)
            return;
        for (T obj : objs) {
            insert(getDatabase(), obj);
        }
    }

    protected void insert(SQLiteDatabase db, T obj) {
        insert(db, obj, true);
    }

    protected void insert(SQLiteDatabase db, T obj, boolean updateNull) {
        long result = db.insert(getTableName(), null,
                getValues(obj, updateNull));
        if (result == -1)
            throw new SQLException();
        obj.setId(result);
    }

    public void insert(T obj) {
        insert(getDatabase(), obj);
    }

    protected abstract ContentValues getValues(T obj, boolean updateNull);

    public void update(T obj, boolean updateNull) {
        update(getDatabase(), obj, updateNull);
    }

    public void update(T obj) {
        update(obj, true);
    }

    protected void update(SQLiteDatabase db, T obj, boolean updateNull) {
        int result = db.update(getTableName(), getValues(obj, updateNull),
                "id = ?", new String[]{Long.toString(obj.getId())});
        if (result != 1)
            throw new RuntimeException("Updated " + result
                    + " rows. 1 expected.");
    }

    public void update(Collection<T> objs) {
        update(objs, true);
    }

    public void update(Collection<T> objs, boolean updateNull) {
        for (T obj : objs) {
            update(getDatabase(), obj, updateNull);
        }
    }

    public void delete(Collection<T> obj) {
        for (T t : obj) {
            delete(getDatabase(), t.getId());
        }
    }

    public void delete(T obj) {
        delete(obj.getId());
    }

    public void delete(Long obj) {
        delete(getDatabase(), obj);
    }

    protected void delete(SQLiteDatabase db, Long id) {
        log.trace("SQL delete: " + getTableName() + ", id=" + id);
        db.delete(getTableName(), "id = ?",
                new String[]{Long.toString(id)});
    }

    public SortedSet<T> findAll() {
        return query(null, null, null, null, null);
    }

    public T findById(T obj) {
        return findById(obj.getId());
    }

    public T findById(long obj) {
        SortedSet<T> result = query(ID + " = ?",
                new String[]{Long.toString(obj)}, null, null, null);
        if (result.size() > 1)
            throw new SQLiteConstraintException();
        else if (result.isEmpty())
            return null;
        return result.first();
    }

    protected SortedSet<T> query(String selection, String[] selectionArgs,
                                 String groupBy, String having, String orderBy) {
        long millis1 = System.currentTimeMillis();
        long millis2 = System.currentTimeMillis();
        try {
            Cursor cursor = getDatabase().query(getTableName(), getColumnNames(),
                    selection, selectionArgs, groupBy, having, orderBy);
            return parseResults(cursor);
        } finally {
            long now = System.currentTimeMillis();
            log.trace("SQL query: " + getTableName() + ": " + selection
                    + Arrays.toString(selectionArgs) + " / times: "
                    + (now - millis1) + ", " + (now - millis2));
        }
    }

    protected SortedSet<T> rawQuery(String selection, String[] selectionArgs) {
        long millis1 = System.currentTimeMillis();
        long millis2 = System.currentTimeMillis();
        try {
            Cursor cursor = getDatabase().rawQuery(selection, selectionArgs);
            return parseResults(cursor);
        } finally {
            long now = System.currentTimeMillis();
            log.trace("SQL raw query: " + selection
                    + Arrays.toString(selectionArgs) + " / times: "
                    + (now - millis1) + ", " + (now - millis2));
        }
    }

    private SortedSet<T> parseResults(Cursor cursor) {
        SortedSet<T> results = new TreeSet<>();
        try {

            while (!cursor.isAfterLast()) {
                if (cursor.isBeforeFirst())
                    cursor.moveToNext();
                if (cursor.isAfterLast())
                    break;

                results.add(parseRow(cursor));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return results;
    }

    public abstract T parseRow(Cursor cursor);

    protected abstract String getTableName();

    protected abstract String[] getColumnNames();

    protected <V> V unpackColumnValue(Cursor cursor, Column name, Class<V> c) {
        return unpackColumnValue(cursor, name.name, c);
    }

    @SuppressWarnings("unchecked")
    protected <V> V unpackColumnValue(Cursor cursor, String name, Class<V> c) {
        int idx = cursor.getColumnIndex(name);
        V value;
        if (cursor.isNull(idx))
            value = null;
        else if (c == Integer.class)
            value = (V) Integer.valueOf(cursor.getInt(idx));
        else if (c == Long.class)
            value = (V) Long.valueOf(cursor.getInt(idx));
        else if (c == Float.class)
            value = (V) Float.valueOf(cursor.getFloat(idx));
        else if (c == Double.class)
            value = (V) Double.valueOf(cursor.getDouble(idx));
        else if (c == String.class)
            value = (V) cursor.getString(idx);
        else if (c == byte[].class)
            value = (V) cursor.getBlob(idx);
        else if (c == Calendar.class)
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new SimpleDateFormat(DATE_FORMAT, LOCALE)
                        .parse(cursor.getString(idx)));
                value = (V) cal;
            } catch (ParseException e) {
                return null;
            }
        else if (c == TimeType.class)
            value = (V) TimeType.values()[cursor.getInt(idx)];
        else if (c == Dish.class)
            value = (V) Dish.values()[cursor.getInt(idx)];
        else if (c == TimeOffsetType.class)
            value = (V) TimeOffsetType.values()[cursor.getInt(idx)];
        else if (c == Locale.class) {
            value = (V) Utils.stringToLocale(cursor.getString(idx));
        } else if (c == DateFormat.class) {
            value = (V) new SimpleDateFormat(cursor.getString(idx), getLocale());
        } else if (c == Restaurant.class) {
            value = (V) new Restaurant(cursor.getLong(idx));
        } else if (c == URL.class) {
            try {
                value = (V) new URL(cursor.getString(idx));
            } catch (MalformedURLException e) {
                return null;
            }
        } else {
            throw new ClassCastException();
        }
        return value;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        if (locale == null)
            return Locale.getDefault();
        return locale;
    }

    protected static Table getTable(String name) {
        return tables.get(name);
    }

    protected static void registerTable(String name) {
        tables.put(name, new Table(name));
    }

    public static String columnNamesToClause(String alias, Column[] columns) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < columns.length; i++) {
            if (i > 0)
                sb.append(", ");
            if (alias != null) {
                sb.append(alias);
                sb.append(".");
            }
            sb.append(columns[i]);
        }
        return sb.toString();

    }
}
