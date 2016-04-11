package net.suteren.android.jidelak.ui;

import static net.suteren.android.jidelak.Constants.DEFAULT_DELETE_DELAY;
import static net.suteren.android.jidelak.Constants.DEFAULT_PREFERENCES;
import static net.suteren.android.jidelak.Constants.DELETE_DELAY_KEY;
import static net.suteren.android.jidelak.Constants.LAST_UPDATED_KEY;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import net.suteren.android.jidelak.ErrorType;
import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.JidelakParseException;
import net.suteren.android.jidelak.JidelakTransformerException;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.Utils;
import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.RestaurantMarshaller;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class FeederService extends Service {

	private DataSetObservable changeObservers = new DataSetObservable();

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public static class FeederServiceBinder extends Binder {
		private FeederService ctx;

		public FeederServiceBinder(FeederService ctx) {
			this.ctx = ctx;
		}

		FeederService getService() {
			return ctx;
		}

	}

	public void notifyChanged() {
		log.debug("Notify start in binder.");
		changeObservers.notifyChanged();
	}

	public void registerStartObserver(DataSetObserver refreshObserver) {
		log.debug("registering start observer");
		changeObservers.registerObserver(refreshObserver);
	}

	public void unregisterStartObserver(DataSetObserver refreshObserver) {
		log.debug("unregistering start observer");
		changeObservers.unregisterObserver(refreshObserver);
	}

	private static Logger log = LoggerFactory.getLogger(FeederService.class);

	private final FeederServiceBinder mBinder = new FeederServiceBinder(this);
	static final String LOGGING_TAG = "JidelakFeederService";

	private JidelakDbHelper dbHelper;
	private Handler mHandler = new Handler();

	private FeederReceiver timerReceiver;

	private boolean updating;

	@Override
	public IBinder onBind(Intent intent) {
		log.debug("JidelakFeederService.onBind()");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (isRunning() || intent.getBooleanExtra("register", false))
			return START_NOT_STICKY;
		log.trace("JidelakFeederService.onStartCommand()");
		// this.force = intent.getExtras().getBoolean("force", false);

		if (!updating)
			new Worker().execute(new Void[0]);

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		log.trace("JidelakFeederService.onCreate()");

		registerReceiver(timerReceiver = new FeederReceiver(),
				new IntentFilter(Intent.ACTION_TIME_TICK));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(timerReceiver);
	}

	void updateData() throws JidelakException {

		SourceDao sdao = new SourceDao(getDbHelper());
		MealDao mdao = new MealDao(getDbHelper());
		RestaurantDao rdao = new RestaurantDao(getDbHelper());
		AvailabilityDao adao = new AvailabilityDao(getDbHelper());

		RestaurantMarshaller rm = new RestaurantMarshaller();
		Restaurant restaurant = new Restaurant();
		boolean notFullyUpdated = false;
		for (Source source : sdao.findAll()) {
			try {

				try {
					restaurant = source.getRestaurant();

					InputStream template = openFileInput(restaurant
							.getTemplateName());

					Node result = Utils.retrieve(source, template);

					rm.unmarshall("#document.jidelak.config", result,
							restaurant);

					Set<Availability> avs = new HashSet<Availability>();
					for (Meal meal : restaurant.getMenu()) {
						avs.add(meal.getAvailability());
					}

					for (Availability av : avs) {
						SortedSet<Meal> atd = mdao.findByDayAndRestaurant(
								av.getCalendar(), restaurant);
						mdao.delete(atd);
					}

					for (Meal meal : restaurant.getMenu()) {
						adao.insert(meal.getAvailability());
						mdao.insert(meal);
					}

					if (!(restaurant.getOpeningHours() == null || restaurant
							.getOpeningHours().isEmpty())) {
						Restaurant savedRestaurant = rdao.findById(restaurant);
						if (savedRestaurant != null
								&& savedRestaurant.getOpeningHours() != null)
							adao.delete(savedRestaurant.getOpeningHours());
						adao.insert(restaurant.getOpeningHours());
					}
					rdao.update(restaurant, false);

				} catch (IOException e) {
					throw new JidelakException(getResources().getString(
							R.string.feeder_io_exception), e).setSource(source)
							.setRestaurant(rdao.findById(restaurant))
							.setHandled(true).setErrorType(ErrorType.NETWORK);
				} catch (TransformerException e) {
					throw new JidelakTransformerException(getResources()
							.getString(R.string.transformer_exception), rdao
							.findById(restaurant).getTemplateName(), source
							.getUrl().toString(), e).setSource(source)
							.setRestaurant(rdao.findById(restaurant))
							.setHandled(true).setErrorType(ErrorType.PARSING);
				} catch (ParserConfigurationException e) {
					throw new JidelakException(getResources().getString(
							R.string.parser_configuration_exception), e)
							.setSource(source)
							.setRestaurant(rdao.findById(restaurant))
							.setHandled(true).setErrorType(ErrorType.PARSING);
				} catch (JidelakException e) {
					throw e.setSource(source).setRestaurant(
							rdao.findById(restaurant));
				}

			} catch (JidelakException e) {
				log.error(e.getMessage(), e);
				mHandler.post(new ToastRunnable(getResources().getString(
						R.string.import_failed)
						+ e.getMessage()));
				notFullyUpdated = true;
				// NotificationUtils.makeNotification(getApplicationContext(),
				// e);
			}
		}
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(
				DEFAULT_PREFERENCES, Context.MODE_PRIVATE);
		if (notFullyUpdated) {
			prefs.edit().putLong(LAST_UPDATED_KEY, System.currentTimeMillis())
					.commit();
		}

		long delay = prefs.getLong(DELETE_DELAY_KEY, DEFAULT_DELETE_DELAY);
		if (delay >= 0) {
			Calendar cal = Calendar.getInstance(Locale.getDefault());
			cal.setTimeInMillis(System.currentTimeMillis() - delay);
			mdao.deleteOlder(cal);
		}

		getDbHelper().notifyDataSetChanged();
	}

	private JidelakDbHelper getDbHelper() {
		if (dbHelper == null)
			dbHelper = JidelakDbHelper.getInstance(getApplicationContext());
		return dbHelper;
	}

	private class Worker extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			updating = true;
			notifyChanged();
			try {

				updateData();
			} catch (JidelakException e) {
				log.error(e.getMessage(), e);
				mHandler.post(new ToastRunnable(getResources().getString(
						R.string.import_failed)
						+ e.getMessage()));

				// NotificationUtils.makeNotification(getApplicationContext(),
				// e);

			} finally {
				updating = false;
				notifyChanged();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			updating = false;
			notifyChanged();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			updating = false;
			notifyChanged();
		}

	}

	private class ToastRunnable implements Runnable {
		String mText;

		public ToastRunnable(String text) {
			mText = text;
		}

		@Override
		public void run() {
			Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_LONG)
					.show();
		}
	}

	public boolean isRunning() {
		return updating;

	}

}