package net.suteren.android.jidelak.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Identificable;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

public abstract class BaseDao<T extends Identificable> {
	private JidelakDbHelper dbHelper;

	public BaseDao(JidelakDbHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	protected JidelakDbHelper getDbHelper() {
		return dbHelper;
	}

	public void insert(T obj) {
		// TODO Auto-generated method stub

	}

	public void update(T obj) {
		// TODO Auto-generated method stub

	}

	public void delete(T obj) {
		getDbHelper().getWritableDatabase().delete(getTableName(), "id = ?", new String[]{Integer.toString( obj.getId())});
	}

	public List<T> findAll() {
		return query(null, null, null, null, null);
	}

	public T findById(T obj) {
		return findById(obj.getId());
	}

	public T findById(int obj) {
		List<T> result = query("id = ?",
				new String[] { Integer.toString(obj) }, null, null, null);
		if (result.size() > 1)
			throw new SQLiteConstraintException();
		else if (result.isEmpty())
			throw new NoSuchElementException();
		return result.get(0);
	}

	protected List<T> query(String selection, String[] selectionArgs,
			String groupBy, String having, String orderBy) {
		Cursor cursor = getDbHelper().getWritableDatabase().query(
				getTableName(), getColumnNames(), selection, selectionArgs,
				groupBy, having, orderBy);

		ArrayList<T> results = new ArrayList<T>();
		while (!cursor.isAfterLast()) {
			if (cursor.isBeforeFirst())
				cursor.moveToNext();
			if (cursor.isAfterLast())
				break;

			results.add(parseRow(cursor));
			cursor.moveToNext();

		}

		return results;

	}

	protected abstract T parseRow(Cursor cursor);

	protected abstract String getTableName();

	protected abstract String[] getColumnNames();

	@SuppressWarnings("unchecked")
	public <V> V getColumnValue(Cursor cursor, String name, Class<V> c) {
		int idx = cursor.getColumnIndex(name);
		V value;
		if (cursor.isNull(idx))
			value = null;
		else if (c == Integer.class)
			value = (V) Integer.valueOf(cursor.getInt(idx));
		else if (c == Float.class)
			value = (V) Double.valueOf(cursor.getDouble(idx));
		else if (c == String.class)
			value = (V) Double.valueOf(cursor.getString(idx));
		else if (c == byte[].class)
			value = (V) cursor.getBlob(idx);
		else
			throw new ClassCastException();
		return value;
	}
}
