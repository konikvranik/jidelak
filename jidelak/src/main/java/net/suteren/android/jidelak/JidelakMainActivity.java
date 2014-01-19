package net.suteren.android.jidelak;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class JidelakMainActivity extends ActionBarActivity implements
		TabListener, OnNavigationListener {

	private JidelakDbHelper dbHelper;
	private ViewPager pagerView;
	private ActionBar ab;
	DayPagerAdapter dpa;

	public JidelakDbHelper getDbHelper() {
		if (dbHelper == null)
			dbHelper = new JidelakDbHelper(getApplicationContext());

		return dbHelper;
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
		pagerView = (ViewPager) findViewById(R.id.pager);

		ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		dpa = new DayPagerAdapter(getSupportFragmentManager());
		pagerView.setAdapter(dpa);

		ab.setListNavigationCallbacks(dpa, this);

		pagerView
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						ab.setSelectedNavigationItem(position);
					}

				});

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

	private void goToday() {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(System.currentTimeMillis());
		pagerView.setCurrentItem(dpa.getPositionByDate(cal));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jidelak, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_update:
			return null != startService(new Intent(this,
					JidelakFeederService.class).putExtra("force", true));

		case R.id.action_settings:
			return true;

		case android.R.id.home:
			goToday();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static class DailyMenuAdapter extends BaseExpandableListAdapter {

		private static final boolean ENABLE_UPPER_SHADOW = false;
		private final Calendar day;
		private final JidelakDbHelper dbHelper;
		private List<Restaurant> restaurants;
		private Context ctx;

		public DailyMenuAdapter(Context ctx, Calendar day) {
			this.day = day;
			this.ctx = ctx;
			dbHelper = new JidelakDbHelper(ctx);

			updateRestaurants();
		}

		private void updateRestaurants() {
			restaurants = new RestaurantDao(dbHelper).findAll();
			MealDao mdao = new MealDao(dbHelper);
			AvailabilityDao adao = new AvailabilityDao(dbHelper);
			for (Restaurant restaurant : restaurants) {
				restaurant.setMenu(new TreeSet<Meal>(mdao
						.findByDayAndRestaurant(day, restaurant)));
				restaurant.setOpeningHours(new TreeSet<Availability>(adao
						.findByRestaurant(restaurant)));
			}
		}

		@Override
		public int getGroupCount() {
			return restaurants.size();
		}

		@Override
		public int getChildrenCount(int paramInt) {
			if (getGroup(paramInt).getMenu() == null)
				return 0;
			return getGroup(paramInt).getMenu().size();
		}

		@Override
		public Restaurant getGroup(int paramInt) {
			return restaurants.get(paramInt);
		}

		@Override
		public Meal getChild(int paramInt1, int paramInt2) {
			List<Meal> ml = getGroup(paramInt1).getMenuAsList();
			if (ml == null)
				return null;
			return ml.get(paramInt2);
		}

		@Override
		public long getGroupId(int paramInt) {
			return getGroup(paramInt).getId();
		}

		@Override
		public long getChildId(int paramInt1, int paramInt2) {
			return getChild(paramInt1, paramInt2).getId();
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@SuppressWarnings("unused")
		@Override
		public View getGroupView(int paramInt, boolean paramBoolean,
				View paramView, ViewGroup paramViewGroup) {

			if (paramView == null) {
				paramView = View.inflate(ctx, R.layout.restaurant, null);
			}

			paramView.invalidate();

			if (paramInt > 0 && ENABLE_UPPER_SHADOW)
				paramView.findViewById(R.id.upper_shadow).setVisibility(
						View.VISIBLE);
			if (paramInt > 0)
				paramView.findViewById(R.id.bottom_shadow).setVisibility(
						View.VISIBLE);

			Restaurant restaurant = getGroup(paramInt);

			TextView nameView = (TextView) paramView.findViewById(R.id.name);
			nameView.setText(restaurant.getName());

			TextView openingView = (TextView) paramView.findViewById(R.id.open);
			openingView.setText(restaurant.openingHoursToString(day));

			return paramView;
		}

		@Override
		public View getChildView(int paramInt1, int paramInt2,
				boolean isLastChild, View paramView, ViewGroup paramViewGroup) {

			if (paramView == null)
				paramView = View.inflate(ctx, R.layout.meal, null);

			Meal meal = getChild(paramInt1, paramInt2);

			((TextView) paramView.findViewById(R.id.name)).setText(meal
					.getTitle());
			((TextView) paramView.findViewById(R.id.description)).setText(meal
					.getDescription());
			((TextView) paramView.findViewById(R.id.price)).setText(meal
					.getPrice());

			return paramView;
		}

		@Override
		public boolean isChildSelectable(int paramInt1, int paramInt2) {
			return false;
		}
	}

	public static class DayFragment extends Fragment {

		public static final String ARG_DAY = "day";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			Bundle args = getArguments();

			View rootView = inflater.inflate(R.layout.day, container, false);
			ExpandableListView menuList = (ExpandableListView) rootView
					.findViewById(R.id.menu_list);

			Calendar day = Calendar.getInstance(Locale.getDefault());
			Long time = args.getLong(DayFragment.ARG_DAY);
			if (time == null)
				time = System.currentTimeMillis();
			day.setTime(new Date(time));

			final DailyMenuAdapter ad = new DailyMenuAdapter(getActivity()
					.getApplicationContext(), day);
			menuList.setAdapter(ad);

			for (int i = 0; i < ad.getGroupCount(); i++) {
				menuList.expandGroup(i);
			}

			final JidelakMainActivity act = (JidelakMainActivity) getActivity();
			act.getDbHelper().registerObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
					ad.updateRestaurants();
					act.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ad.notifyDataSetChanged();
						}
					});
					super.onChanged();
				}

				@Override
				public void onInvalidated() {
					ad.updateRestaurants();
					act.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ad.notifyDataSetInvalidated();
						}
					});
					super.onInvalidated();
				}
			});

			return rootView;
		}
	}

	static final String LOGGER_TAG = "JidelakMainActivity";

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

		private void updateDates() {
			AvailabilityDao ad = new AvailabilityDao(getDbHelper());
			dates = ad.findAllDays();

		}

		@Override
		public CharSequence getPageTitle(int position) {
			Availability d = dates.get(position);
			return DateFormat.getDateInstance(DateFormat.FULL,
					Locale.getDefault()).format(d.getCalendar().getTime());

		}

		Calendar getDateByPosition(int position) {
			return dates.get(position).getCalendar();
		}

		int getPositionByDate(Calendar cal) {
			int i = dates.indexOf(new Availability(cal));
			if (i < 0) {
				Availability a = new Availability(cal);
				TreeSet<Availability> g = new TreeSet<Availability>(dates);
				a = g.ceiling(a);
				return dates.indexOf(a);
			}
			return i;
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
			return position;
		}

		@Override
		public int getItemViewType(int position) {
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
			return false;
		}

		@Override
		public boolean isEmpty() {
			return dates.isEmpty();
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

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		pagerView.setCurrentItem(arg0);
		return true;
	}
}
