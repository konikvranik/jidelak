package net.suteren.android.jidelak;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class DayFragment extends Fragment {

	private static Logger log = LoggerFactory.getLogger(DayFragment.class);

	public static class DailyMenuAdapter extends BaseExpandableListAdapter {

		private static final boolean ENABLE_UPPER_SHADOW = false;
		private Context ctx;
		private final Calendar day;
		private final JidelakDbHelper dbHelper;
		private List<Restaurant> restaurants;

		public DailyMenuAdapter(Context ctx, Calendar day) {
			this.day = day;
			this.ctx = ctx;
			dbHelper = new JidelakDbHelper(ctx);

			updateRestaurants();
		}

		@Override
		public Meal getChild(int paramInt1, int paramInt2) {
			List<Meal> ml = getGroup(paramInt1).getMenuAsList();
			if (ml == null)
				return null;
			return ml.get(paramInt2);
		}

		@Override
		public long getChildId(int paramInt1, int paramInt2) {
			return getChild(paramInt1, paramInt2).getId();
		}

		@Override
		public int getChildrenCount(int paramInt) {
			if (getGroup(paramInt).getMenu() == null)
				return 0;
			return getGroup(paramInt).getMenu().size();
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
		public Restaurant getGroup(int paramInt) {
			return restaurants.get(paramInt);
		}

		@Override
		public int getGroupCount() {
			return restaurants.size();
		}

		@Override
		public long getGroupId(int paramInt) {
			return getGroup(paramInt).getId();
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
			else
				paramView.findViewById(R.id.upper_shadow).setVisibility(
						View.GONE);

			if (paramInt > 0)
				paramView.findViewById(R.id.bottom_shadow).setVisibility(
						View.VISIBLE);
			else
				paramView.findViewById(R.id.bottom_shadow).setVisibility(
						View.GONE);

			Restaurant restaurant = getGroup(paramInt);

			TextView nameView = (TextView) paramView.findViewById(R.id.name);
			nameView.setText(restaurant.getName());

			TextView openingView = (TextView) paramView.findViewById(R.id.open);
			openingView.setText(restaurant.openingHoursToString(day));

			return paramView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int paramInt1, int paramInt2) {
			return false;
		}

		private void updateRestaurants() {
			long milis = System.currentTimeMillis();
			log.debug("Update restaurants start");

			restaurants = new RestaurantDao(dbHelper).findAll();

			log.debug("Update restaurants got restaurants");

			MealDao mdao = new MealDao(dbHelper);
			AvailabilityDao adao = new AvailabilityDao(dbHelper);

			long partMilis = System.currentTimeMillis();

//			TreeMap<Long, SortedSet<Meal>> mmap = new TreeMap<Long, SortedSet<Meal>>();
//			for (Meal m : mdao.findByDay(day)) {
//				SortedSet<Meal> s = mmap.get(m.getRestaurant().getId());
//				if (s == null)
//					s = new TreeSet<Meal>();
//				s.add(m);
//				mmap.put(m.getRestaurant().getId(), s);
//			}

			log.debug("Update restaurants update start");
			for (Restaurant restaurant : restaurants) {
				 restaurant.setMenu(new TreeSet<Meal>(mdao
				 .findByDayAndRestaurant(day, restaurant)));
//				SortedSet<Meal> m = mmap.get(restaurant.getId());
//				if (m != null)
//					restaurant.setMenu(m);
				restaurant.setOpeningHours(new TreeSet<Availability>(adao
						.findByRestaurant(restaurant)));
			}
			log.debug("Update restaurants update end: "
					+ (System.currentTimeMillis() - partMilis));

			log.debug("Update restaurants end: "
					+ (System.currentTimeMillis() - milis));
		}
	}

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

		final DayFragment.DailyMenuAdapter ad = new DailyMenuAdapter(
				getActivity().getApplicationContext(), day);
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