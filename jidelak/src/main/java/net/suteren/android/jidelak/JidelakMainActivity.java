package net.suteren.android.jidelak;

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
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jidelak, menu);
		return true;
	}

	public static class MenuListAdapter extends BaseAdapter implements
			ListAdapter {

		private final JidelakDbHelper dbHelper;
		private Context ctx;
		private Calendar day;
		private List<Meal> meals = new ArrayList<Meal>();
		private Restaurant restaurant;

		public MenuListAdapter(Context ctx, Calendar day, Restaurant restaurant) {
			this.day = day;
			this.ctx = ctx;
			this.restaurant = restaurant;
			dbHelper = new JidelakDbHelper(ctx);
		}

		@Override
		public void notifyDataSetChanged() {
			dbHelper.notifyDataSetChanged();
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			dbHelper.registerObserver(observer);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			dbHelper.unregisterObserver(observer);
		}

		private void updateMeals() {

			MealDao mealDao = new MealDao(dbHelper);
			meals = mealDao.findByDayAndRestaurant(day, restaurant);
			notifyDataSetChanged();
		}

		public int getCount() {
			updateMeals();
			return meals.size();
		}

		public Meal getItem(int paramInt) {
			updateMeals();
			return meals.get(paramInt);
		}

		public long getItemId(int paramInt) {
			// updateMeals();
			return meals.get(paramInt).getId();
		}

		public boolean hasStableIds() {
			return false;
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {

			if (paramView == null)
				paramView = View.inflate(ctx, R.layout.meal, paramViewGroup);

			Meal meal = getItem(paramInt);
			((TextView) paramView.findViewById(R.id.name)).setText(meal
					.getTitle());
			((TextView) paramView.findViewById(R.id.description)).setText(meal
					.getDescription());
			((TextView) paramView.findViewById(R.id.price)).setText(meal
					.getPrice());

			return paramView;
		}

		public int getItemViewType(int paramInt) {
			return 0;
		}

		public int getViewTypeCount() {
			return 0;
		}

		public boolean isEmpty() {
			updateMeals();
			return meals.isEmpty();
		}

		public boolean areAllItemsEnabled() {
			return true;
		}

		public boolean isEnabled(int paramInt) {
			return true;
		}

	}

	public static class DailyMenuAdapter extends BaseAdapter implements
			ListAdapter {

		private final Calendar day;
		private final JidelakDbHelper dbHelper;
		private List<Restaurant> restaurants;
		private Context ctx;

		public DailyMenuAdapter(Context ctx, Calendar day) {
			this.day = day;
			this.ctx = ctx;
			dbHelper = new JidelakDbHelper(ctx);
		}

		@Override
		public void notifyDataSetChanged() {
			dbHelper.notifyDataSetChanged();
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			dbHelper.registerObserver(observer);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			dbHelper.unregisterObserver(observer);
		}

		public int getCount() {
			updateRestaurants();
			return restaurants.size();
		}

		private void updateRestaurants() {
			restaurants = new RestaurantDao(dbHelper).findAll();
			notifyDataSetChanged();
		}

		public Restaurant getItem(int position) {
			updateRestaurants();
			return restaurants.get(position);
		}

		public long getItemId(int position) {
			return getItem(position).getId();
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = View.inflate(ctx, R.layout.restaurant_daily_menu,
						parent);
			}

			Restaurant restaurant = getItem(position);

			TextView nameView = (TextView) convertView.findViewById(R.id.name);
			nameView.setText(restaurant.getName());

			TextView openingView = (TextView) convertView
					.findViewById(R.id.open);
			openingView.setText(Restaurant.openingHoursToString(restaurant
					.getOpeningHours(day)));

			ListView menuListView = (ListView) convertView
					.findViewById(R.id.menu_list);

			ListAdapter menuListAdapter = new MenuListAdapter(ctx, day,
					restaurant);
			menuListView.setAdapter(menuListAdapter);

			return convertView;
		}

	}

	public static class DayFragment extends Fragment {
		public static final String ARG_DAY = "day";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			Bundle args = getArguments();

			View rootView = inflater.inflate(R.layout.day, container, false);
			ListView menuList = (ListView) rootView
					.findViewById(R.id.menu_list);

			Calendar day = Calendar.getInstance(Locale.getDefault());
			day.setTime(new Date(args.getLong(DayFragment.ARG_DAY)));

			menuList.setAdapter(new DailyMenuAdapter(getActivity()
					.getApplicationContext(), day));

			return rootView;
		}
	}

	public class DayPagerAdapter extends FragmentStatePagerAdapter {

		private List<Availability> dates = new ArrayList<Availability>();

		public DayPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			updateDates();
			return dates.size();
		}

		private void updateDates() {
			AvailabilityDao ad = new AvailabilityDao(getDbHelper());

			dates = ad.findAllDays();

			notifyDataSetChanged();
		}

		private Calendar getDateByPosition(int position) {
			updateDates();
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
