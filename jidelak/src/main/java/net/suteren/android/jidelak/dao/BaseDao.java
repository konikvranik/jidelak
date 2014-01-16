package net.suteren.android.jidelak.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.Utils;
import net.suteren.android.jidelak.model.Identificable;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.TimeOffsetType;
import net.suteren.android.jidelak.model.TimeType;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

public abstract class BaseDao<T extends Identificable> {
	public static class Table {

		private String name;
		private List<Column> columns = new ArrayList<Column>();
		private List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();

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
			StringBuffer sb = new StringBuffer("create table ");
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
			List<String> colNames = new ArrayList<String>();
			for (Column col : columns) {
				colNames.add(col.getName());
			}
			return colNames.toArray(new String[0]);
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
			StringBuffer sb = new StringBuffer();
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
			StringBuffer sb = new StringBuffer();
			sb.append("foreign key(");
			sb.append(source.getName());
			sb.append(") references ");
			sb.append(table.getName());
			sb.append("(");
			sb.append(target.getName());
			sb.append(")");
			return sb.toString();
		}
	}

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	public static final Column ID = new Column("id", SQLiteDataTypes.INTEGER,
			true);

	public static final Locale LOCALE = Locale.ENGLISH;

	private static Map<String, Table> tables = new HashMap<String, Table>();
	private JidelakDbHelper dbHelper;
	public Locale locale;

	public BaseDao(JidelakDbHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	protected JidelakDbHelper getDbHelper() {
		return dbHelper;
	}

	public void insert(Collection<T> objs) {
		SQLiteDatabase db = getDbHelper().getWritableDatabase();
		for (T obj : objs) {
			long result = db.insert(getTableName(), null, getValues(obj));
			if (result == -1)
				throw new SQLException();
			obj.setId(result);
		}
		db.close();
		getDbHelper().notifyDataSetChanged();
	}

	public void insert(T obj) {
		SQLiteDatabase db = getDbHelper().getWritableDatabase();
		long result = db.insert(getTableName(), null, getValues(obj));
		if (result == -1)
			throw new SQLException();
		obj.setId(result);
		db.close();
		getDbHelper().notifyDataSetChanged();
	}

	protected abstract ContentValues getValues(T obj);

	public void update(T obj) {
		SQLiteDatabase db = getDbHelper().getWritableDatabase();
		try {
			int result = db.update(getTableName(), getValues(obj), "id = ?",
					new String[] { Long.toString(obj.getId()) });
			if (result != 1)
				throw new RuntimeException("Updated " + result
						+ " rows. 1 expected.");
		} finally {
			db.close();
		}
		getDbHelper().notifyDataSetChanged();
	}

	public void delete(Collection<T> obj) {

		SQLiteDatabase db = getDbHelper().getWritableDatabase();
		try {
			for (T t : obj) {
				db.delete(getTableName(), "id = ?",
						new String[] { Long.toString(t.getId()) });
			}
		} finally {
			db.close();
		}
		getDbHelper().notifyDataSetInvalidated();
	}

	public void delete(T obj) {
		SQLiteDatabase db = getDbHelper().getWritableDatabase();
		try {
			db.delete(getTableName(), "id = ?",
					new String[] { Long.toString(obj.getId()) });
		} finally {
			db.close();
		}
		getDbHelper().notifyDataSetInvalidated();
	}

	public List<T> findAll() {
		return query(null, null, null, null, null);
	}

	public T findById(T obj) {
		return findById(obj.getId());
	}

	public T findById(long obj) {
		List<T> result = query(ID + " = ?",
				new String[] { Long.toString(obj) }, null, null, null);
		if (result.size() > 1)
			throw new SQLiteConstraintException();
		else if (result.isEmpty())
			return null;
		return result.get(0);
	}

	protected List<T> query(String selection, String[] selectionArgs,
			String groupBy, String having, String orderBy) {
		ArrayList<T> results = new ArrayList<T>();
		SQLiteDatabase db = getDbHelper().getReadableDatabase();
		try {
			Cursor cursor = db.query(getTableName(), getColumnNames(),
					selection, selectionArgs, groupBy, having, orderBy);
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
		} finally {
			db.close();
		}

		return results;

	}

	protected abstract T parseRow(Cursor cursor);

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
		else if (c == TimeOffsetType.class)
			value = (V) TimeOffsetType.values()[cursor.getInt(idx)];
		else if (c == Locale.class) {
			value = (V) Utils.stringToLocale(cursor.getString(idx));
		} else if (c == DateFormat.class) {
			value = (V) new SimpleDateFormat(cursor.getString(idx), getLocale());
		} else if (c == Restaurant.class) {
			value = (V) new Restaurant();
			((Restaurant) value).setId(cursor.getLong(idx));
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
}
