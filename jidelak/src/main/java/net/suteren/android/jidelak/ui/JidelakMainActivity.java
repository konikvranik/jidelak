package net.suteren.android.jidelak.ui;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.Utils;
import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.ui.JidelakFeederService.LocalBinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class JidelakMainActivity extends ActionBarActivity implements
		TabListener, OnNavigationListener {

	private static Logger log = LoggerFactory
			.getLogger(JidelakMainActivity.class);

	boolean mBound = false;

	JidelakFeederService mService;

	public class DayPagerAdapter extends FragmentPagerAdapter implements
			SpinnerAdapter {

		private List<Availability> dates = new ArrayList<Availability>();

		public DayPagerAdapter(FragmentManager fm) {
			super(fm);
			updateDates();
		}

		@Override
		public int getCount() {
			if (dates == null || dates.isEmpty())
				return 1;
			return dates.size();
		}

		Calendar getDateByPosition(int position) {
			if (dates == null || dates.isEmpty())
				return null;

			return dates.get(position).getCalendar();
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {

			if (dates == null || dates.isEmpty())
				return convertView;

			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.spinner_list, null);
			}

			String value = DateFormat.getDateInstance(DateFormat.FULL,
					Locale.getDefault()).format(
					dates.get(position).getCalendar().getTime());
			((TextView) convertView.findViewById(R.id.value)).setText(value);

			int pd = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
							.getDisplayMetrics());
			convertView.setPadding(pd, pd, pd, pd);

			return convertView;
		}

		public Fragment getItem(Calendar day) {
			log.debug("getFragment: " + day);
			Fragment fragment = new DayFragment();
			Bundle args = new Bundle();
			if (!isEmpty())
				args.putLong(DayFragment.ARG_DAY, day.getTime().getTime());
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public Fragment getItem(int position) {
			log.debug("getFragment: " + position);
			return getItem(getDateByPosition(position));
		}

		@Override
		public long getItemId(int position) {
			if (getDateByPosition(position) == null)
				return -1;
			return getDateByPosition(position).getTime().getDate();
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Availability d;
			if (dates == null || dates.isEmpty()) {
				Calendar cal = Calendar.getInstance(Locale.getDefault());
				cal.setTimeInMillis(System.currentTimeMillis());
				d = new Availability(cal);
			} else {
				d = dates.get(position);
			}
			return DateFormat.getDateInstance(DateFormat.FULL,
					Locale.getDefault()).format(d.getCalendar().getTime());

		}

		int getPositionByDate(Calendar cal) {

			if (dates == null || dates.isEmpty())
				return -1;

			Availability a = new Availability(cal);
			for (int i = 0; i < dates.size(); i++) {
				if (a.compareTo(dates.get(i)) < 0)
					return i;
			}

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.spinner_view, null);
			}

			String value = " - ";
			if (!isEmpty())
				value = DateFormat.getDateInstance(DateFormat.SHORT,
						Locale.getDefault()).format(
						dates.get(position).getCalendar().getTime());
			((TextView) convertView.findViewById(R.id.value)).setText(value);

			return convertView;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isEmpty() {
			return dates.isEmpty();
		}

		private void updateDates() {

			log.debug("Update dates start");
			AvailabilityDao ad = new AvailabilityDao(getDbHelper());
			dates = new ArrayList<Availability>(ad.findAllDays());
			log.debug("Update dates end");
		}

	}

	private ActionBar ab;

	private JidelakDbHelper dbHelper;

	DayPagerAdapter dpa;

	private ViewPager pagerView;

	private Menu mainMenu;

	public JidelakDbHelper getDbHelper() {
		if (dbHelper == null)
			dbHelper = new JidelakDbHelper(getApplicationContext());
		return dbHelper;
	}

	private void goToday() {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(System.currentTimeMillis());
		goToDay(dpa.getPositionByDate(cal));
		ab.setDisplayHomeAsUpEnabled(false);
	}

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getOverflowMenu();
		setContentView(R.layout.activity_main);

		dpa = new DayPagerAdapter(getSupportFragmentManager());

		setupActionBar();

		setupPagerView();

		goToday();

		getDbHelper().registerObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				dpa.updateDates();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dpa.notifyDataSetChanged();
						ab.removeAllTabs();
						for (int i = 0; i < dpa.getCount(); i++) {
							ab.addTab(ab.newTab().setText(dpa.getPageTitle(i))
									.setTabListener(JidelakMainActivity.this));

						}

					}
				});
				super.onChanged();
			}

			@Override
			public void onInvalidated() {
				dpa.updateDates();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dpa.notifyDataSetChanged();
						ab.removeAllTabs();
						for (int i = 0; i < dpa.getCount(); i++) {
							ab.addTab(ab.newTab().setText(dpa.getPageTitle(i))
									.setTabListener(JidelakMainActivity.this));

						}

					}
				});
				super.onInvalidated();
			}

		});

	}

	private void setupPagerView() {
		pagerView = (ViewPager) findViewById(R.id.pager);
		pagerView.setAdapter(dpa);
		pagerView
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						ab.setSelectedNavigationItem(position);
					}

				});
	}

	private void setupActionBar() {
		ab = getSupportActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		ab.setListNavigationCallbacks(dpa, this);
	}

	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mainMenu = menu;
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jidelak, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		goToDay(arg0);
		return true;
	}

	protected void goToDay(int arg0) {
		pagerView.setCurrentItem(arg0);
		ab.setDisplayHomeAsUpEnabled(true);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	void startRefreshHc() {
		log.debug("Start refresh");
		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
		rotation.setRepeatCount(Animation.INFINITE);

		MenuItem refreshItem = mainMenu.findItem(R.id.action_update);

		ImageView iv = (ImageView) refreshItem.getActionView();
		if (iv == null) {
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			iv = (ImageView) inflater.inflate(R.layout.refresh_action_view,
					null);
			refreshItem.setActionView(iv);
		}
		iv.startAnimation(rotation);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	void stopRefreshHc() {
		log.debug("Stop refresh");
		MenuItem refreshItem = mainMenu.findItem(R.id.action_update);

		ImageView iv = (ImageView) refreshItem.getActionView();
		if (iv != null) {
			iv.setAnimation(null);
		}
		refreshItem.setActionView(null);
	}

	void startRefreshFr() {
		log.debug("Start refresh old");

	}

	void stopRefreshFr() {
		log.debug("Stop refresh old");

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_update:
			if (Utils.isServiceRunning(getApplicationContext(),
					JidelakFeederService.class.getName())) {
				Toast.makeText(getApplicationContext(),
						R.string.update_already_running, Toast.LENGTH_SHORT)
						.show();
				return true;
			}
			Intent intent = new Intent(this, JidelakFeederService.class);
			startService(intent);
			boolean res = bindService(intent, mConnection,
					Context.BIND_NOT_FOREGROUND);
			log.debug("Bind result: " + res);
			return true;

		case R.id.action_reorder_restaurants:
			startActivity(new Intent(this, RestaurantManagerActivity.class));
			return true;

		case R.id.action_settings:
			startActivity(new Intent(this, PreferencesActivity.class));
			return true;

		case R.id.action_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;

		case android.R.id.home:
			goToday();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		goToDay(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			log.debug("service connected");
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
			if (Build.VERSION.SDK_INT >= 3.0)
				startRefreshHc();
			else
				startRefreshFr();

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			log.debug("service disconnected");
			mBound = false;
			if (Build.VERSION.SDK_INT >= 3.0)
				stopRefreshHc();
			else
				stopRefreshFr();

		}
	};
}
