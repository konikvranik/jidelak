package net.suteren.android.jidelak;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JidelakDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 0;
	public static final String DATABASE_NAME = "Jidelak.db";
	private static final String SQL_CREATE_ENTRIES = "create table restaurant (id integer primary key, city text, address text, longitude real, latitude real, zip integer, country text, phone text, web text, mail text, name text);"
			+ "create table availability(id integer primary key, year integer, month integer, day integer, dow integer, from integer, to integer, restaurant integer, meal integer, foreign key(restaurant) references restaurant(id));"
			+ "create table meal(title text, description text, category text, dish text, price real, restaurant integer, availability integer, foreign key(restaurant) references restaurant(id),foreign key(availability) references availability(id));";

	private final static DataSetObservable mDataSetObservable = new DataSetObservable();

	public JidelakDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.suteren.android.jidelak.INotifyingDbHelper#onCreate(android.database
	 * .sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.suteren.android.jidelak.INotifyingDbHelper#onUpgrade(android.database
	 * .sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.suteren.android.jidelak.INotifyingDbHelper#onDowngrade(android.database
	 * .sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	@SuppressLint("Override")
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.suteren.android.jidelak.INotifyingDbHelper#notifyDataSetChanged()
	 */
	public void notifyDataSetChanged() {
		mDataSetObservable.notifyChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.suteren.android.jidelak.INotifyingDbHelper#registerObserver(android
	 * .database.DataSetObserver)
	 */
	public void registerObserver(DataSetObserver observer) {
		mDataSetObservable.registerObserver(observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.suteren.android.jidelak.INotifyingDbHelper#unregisterObserver(android
	 * .database.DataSetObserver)
	 */
	public void unregisterObserver(DataSetObserver observer) {
		mDataSetObservable.unregisterObserver(observer);
	}
}
