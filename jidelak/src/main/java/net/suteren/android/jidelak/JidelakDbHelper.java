package net.suteren.android.jidelak;

import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.SourceDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JidelakDbHelper extends SQLiteOpenHelper {

	private static Logger log = LoggerFactory.getLogger(JidelakDbHelper.class);

	public static final int DATABASE_VERSION = 4;
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
		log.debug(SQL_CREATE_RESTAURANT);
		log.debug(SQL_CREATE_AVAILABILITY);
		log.debug(SQL_CREATE_MEAL);
		log.debug(SQL_CREATE_SOURCE);

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

		switch (oldVersion) {
		case 1:

			db.execSQL("create index " + AvailabilityDao.getTable().getName()
					+ "_date on " + AvailabilityDao.getTable().getName() + "("
					+ AvailabilityDao.YEAR + "," + AvailabilityDao.MONTH + ","
					+ AvailabilityDao.DAY + ")");
			db.execSQL("create index " + AvailabilityDao.getTable().getName()
					+ "_" + AvailabilityDao.DOW + " on "
					+ AvailabilityDao.getTable().getName() + "("
					+ AvailabilityDao.DOW + ")");

			if (newVersion <= 2)
				break;

		case 2:
			db.execSQL("create index " + AvailabilityDao.getTable().getName()
					+ "_" + AvailabilityDao.YEAR + " on "
					+ AvailabilityDao.getTable().getName() + "("
					+ AvailabilityDao.YEAR + ")");
			db.execSQL("create index " + AvailabilityDao.getTable().getName()
					+ "_" + AvailabilityDao.MONTH + " on "
					+ AvailabilityDao.getTable().getName() + "("
					+ AvailabilityDao.MONTH + ")");
			db.execSQL("create index " + AvailabilityDao.getTable().getName()
					+ "_" + AvailabilityDao.DAY + " on "
					+ AvailabilityDao.getTable().getName() + "("
					+ AvailabilityDao.DAY + ")");

			db.execSQL("create index " + AvailabilityDao.getTable().getName()
					+ "_" + AvailabilityDao.RESTAURANT + " on "
					+ AvailabilityDao.getTable().getName() + "("
					+ AvailabilityDao.RESTAURANT + ")");

			db.execSQL("create index " + MealDao.getTable().getName() + "_"
					+ MealDao.RESTAURANT + " on "
					+ MealDao.getTable().getName() + "(" + MealDao.RESTAURANT
					+ ")");
			db.execSQL("create index " + MealDao.getTable().getName() + "_"
					+ MealDao.AVAILABILITY + " on "
					+ MealDao.getTable().getName() + "(" + MealDao.AVAILABILITY
					+ ")");

			db.execSQL("create index " + SourceDao.getTable().getName() + "_"
					+ SourceDao.RESTAURANT + " on "
					+ SourceDao.getTable().getName() + "("
					+ SourceDao.RESTAURANT + ")");

			if (newVersion <= 3)
				break;

		case 3:

			db.execSQL("create index " + AvailabilityDao.getTable().getName()
					+ "_whole on " + AvailabilityDao.getTable().getName() + "("
					+ AvailabilityDao.YEAR + "," + AvailabilityDao.MONTH + ","
					+ AvailabilityDao.DAY + ", " + AvailabilityDao.DOW + ")");

			if (newVersion <= 4)
				break;

		default:
			break;
		}

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
		log.debug("notifyDataSetChanged");
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
		log.debug("Registering observer: " + observer);
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
		log.debug("UnRegistering observer: " + observer);
		mDataSetObservable.unregisterObserver(observer);
	}

	public void notifyDataSetInvalidated() {
		log.debug("notifyDataSetInvalidated");
		mDataSetObservable.notifyInvalidated();
	}

}
