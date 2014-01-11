package net.suteren.android.jidelak;

import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.SourceDao;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class JidelakDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Jidelak.db";

	private static final String SQL_CREATE_RESTAURANT = RestaurantDao
			.getTable().createClausule();

	private static final String SQL_CREATE_AVAILABILITY = AvailabilityDao
			.getTable().createClausule();
	private static final String SQL_CREATE_MEAL = MealDao.getTable()
			.createClausule();
	private static final String SQL_CREATE_SOURCE = SourceDao.getTable()
			.createClausule();

	private final static DataSetObservable mDataSetObservable = new DataSetObservable();
	private static final String LOGGER_TAG = "JidelakDbHelper";

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
		Log.d(LOGGER_TAG, SQL_CREATE_RESTAURANT);
		Log.d(LOGGER_TAG, SQL_CREATE_AVAILABILITY);
		Log.d(LOGGER_TAG, SQL_CREATE_MEAL);
		Log.d(LOGGER_TAG, SQL_CREATE_SOURCE);

		db.execSQL(SQL_CREATE_RESTAURANT);
		db.execSQL(SQL_CREATE_AVAILABILITY);
		db.execSQL(SQL_CREATE_MEAL);
		db.execSQL(SQL_CREATE_SOURCE);
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
