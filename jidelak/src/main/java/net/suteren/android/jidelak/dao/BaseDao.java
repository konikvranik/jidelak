package net.suteren.android.jidelak.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Identificable;
import net.suteren.android.jidelak.model.TimeType;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

public abstract class BaseDao<T extends Identificable> {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String ID = "id";
	public static final Locale LOCALE = Locale.ENGLISH;
	private JidelakDbHelper dbHelper;

	public BaseDao(JidelakDbHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	protected JidelakDbHelper getDbHelper() {
		return dbHelper;
	}

	public void insert(T obj) {
		SQLiteDatabase db = getDbHelper().getWritableDatabase();
		long result = db.insert(getTableName(), null, getValues(obj));
		if (result == -1)
			throw new SQLException();
		obj.setId(result);
		db.close();
	}

	protected abstract ContentValues getValues(T obj);

	public void update(T obj) {
		SQLiteDatabase db = getDbHelper().getWritableDatabase();
		int result = db.update(getTableName(), getValues(obj), "id = ?",
				new String[] { Long.toString(obj.getId()) });
		if (result != 1)
			throw new RuntimeException("Updated " + result
					+ " rows. 1 expected.");
		db.close();
	}

	public void delete(T obj) {
		SQLiteDatabase db = getDbHelper().getWritableDatabase();
		try {
			db.delete(getTableName(), "id = ?",
					new String[] { Long.toString(obj.getId()) });
		} finally {
			db.close();
		}
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
		else if (c == URL.class)
			try {
				value = (V) new URL(cursor.getString(idx));
			} catch (MalformedURLException e) {
				return null;
			}
		else
			throw new ClassCastException();
		return value;
	}

}
