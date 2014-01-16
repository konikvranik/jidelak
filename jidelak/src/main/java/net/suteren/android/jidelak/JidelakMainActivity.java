package net.suteren.android.jidelak;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class JidelakMainActivity extends ActionBarActivity {

	private JidelakDbHelper dbHelper;

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
		ViewPager pagerView = (ViewPager) findViewById(R.id.pager);
		final DayPagerAdapter dpa = new DayPagerAdapter(
				getSupportFragmentManager());
		pagerView.setAdapter(dpa);

		getDbHelper().registerObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				dpa.updateDates();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dpa.notifyDataSetChanged();
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
					}
				});
				super.onInvalidated();
			}

		});

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

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static class DailyMenuAdapter extends BaseExpandableListAdapter {

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
			for (Restaurant restaurant : restaurants) {
				restaurant
						.setMenu(mdao.findByDayAndRestaurant(day, restaurant));

				Log.d(LOGGER_TAG, "Added " + restaurant.getMenu().size()
						+ " meals.");
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
			Log.d(LOGGER_TAG, "MEnu count: "
					+ getGroup(paramInt).getMenu().size());
			return getGroup(paramInt).getMenu().size();
		}

		@Override
		public Restaurant getGroup(int paramInt) {
			return restaurants.get(paramInt);
		}

		@Override
		public Meal getChild(int paramInt1, int paramInt2) {
			return getGroup(paramInt1).getMenu().get(paramInt2);
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

		@Override
		public View getGroupView(int paramInt, boolean paramBoolean,
				View paramView, ViewGroup paramViewGroup) {

			if (paramView == null) {
				paramView = View.inflate(ctx, R.layout.restaurant_daily_menu,
						null);
			}

//			if (paramInt > 0)
//				paramView.findViewById(R.id.upper_shadow).setVisibility(
//						View.VISIBLE);

			Restaurant restaurant = getGroup(paramInt);

			TextView nameView = (TextView) paramView.findViewById(R.id.name);
			nameView.setText(restaurant.getName());

			TextView openingView = (TextView) paramView.findViewById(R.id.open);
			openingView.setText(Restaurant.openingHoursToString(restaurant
					.getOpeningHours(day)));

			return paramView;
		}

		@Override
		public View getChildView(int paramInt1, int paramInt2,
				boolean isLastChild, View paramView, ViewGroup paramViewGroup) {

			if (paramView == null)
				paramView = View.inflate(ctx, R.layout.meal, null);

			if (isLastChild)
				paramView.findViewById(R.id.bottom_shadow).setVisibility(
						View.VISIBLE);

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

			Log.d(LOGGER_TAG, "Creating new DayFragment for "
					+ new SimpleDateFormat("yyyy-MM-dd").format(day.getTime()));
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

	public class DayPagerAdapter extends FragmentStatePagerAdapter {

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

		private Calendar getDateByPosition(int position) {
			return dates.get(position).getCalendar();
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

	}
}
