package net.suteren.android.jidelak;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.model.Availability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class JidelakMainActivity extends ActionBarActivity implements
		TabListener, OnNavigationListener {

	private static Logger log = LoggerFactory
			.getLogger(JidelakMainActivity.class);

	public class DayPagerAdapter extends FragmentStatePagerAdapter implements
			SpinnerAdapter {

		private List<Availability> dates = new ArrayList<Availability>();

		public DayPagerAdapter(FragmentManager fm) {
			super(fm);
			updateDates();
		}

		@Override
		public int getCount() {
			return dates.size();
		}

		Calendar getDateByPosition(int position) {
			return dates.get(position).getCalendar();
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			if (convertView == null)
				convertView = new TextView(getApplicationContext());
			((TextView) convertView).setText(DateFormat.getDateInstance(
					DateFormat.FULL, Locale.getDefault()).format(
					dates.get(position).getCalendar().getTime()));

			int pd = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
							.getDisplayMetrics());
			convertView.setPadding(pd, pd, pd, pd);

			return convertView;
		}

		public Fragment getItem(Calendar day) {
			Fragment fragment = new DayFragment();
			Bundle args = new Bundle();
			args.putLong(DayFragment.ARG_DAY, day.getTime().getTime());
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public Fragment getItem(int position) {
			return getItem(getDateByPosition(position));
		}

		@Override
		public long getItemId(int position) {
			return getDateByPosition(position).getTime().getDate();
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Availability d = dates.get(position);
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
			if (convertView == null)
				convertView = new TextView(getApplicationContext());

			int pd = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 5, getResources()
							.getDisplayMetrics());
			convertView.setPadding(pd, pd, pd, pd);

			((TextView) convertView).setText(DateFormat.getDateInstance(
					DateFormat.SHORT, Locale.getDefault()).format(
					dates.get(position).getCalendar().getTime()));

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

	public JidelakDbHelper getDbHelper() {
		if (dbHelper == null)
			dbHelper = new JidelakDbHelper(getApplicationContext());
		return dbHelper;
	}

	private void goToday() {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(System.currentTimeMillis());
		pagerView.setCurrentItem(dpa.getPositionByDate(cal));
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

		setContentView(R.layout.activity_main);

		dpa = new DayPagerAdapter(getSupportFragmentManager());

		setupActiveBar();

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

	private void setupActiveBar() {
		ab = getSupportActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		ab.setListNavigationCallbacks(dpa, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jidelak, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		pagerView.setCurrentItem(arg0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_update:
			return null != startService(new Intent(this,
					JidelakFeederService.class).putExtra("force", true));

		case R.id.action_settings:

			startActivity(new Intent(this, RestaurantManagerActivity.class));

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
		pagerView.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}
}
