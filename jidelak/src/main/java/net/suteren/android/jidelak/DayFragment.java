package net.suteren.android.jidelak;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ImageButton;
import android.widget.TextView;

public class DayFragment extends Fragment {

	private static Logger log = LoggerFactory.getLogger(DayFragment.class);

	private JidelakDbHelper dbHelper;

	public class DailyMenuAdapter extends BaseExpandableListAdapter {

		private static final boolean ENABLE_UPPER_SHADOW = false;
		private Context ctx;
		private final Calendar day;
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
			if (ml == null || ml.isEmpty())
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

			ImageButton ib = (ImageButton) paramView
					.findViewById(R.id.btn_menu);
			ib.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					act.openContextMenu(v);
				}
			});

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

			if (getChildrenCount(paramInt) > 0)
				paramView.findViewById(R.id.empty).setVisibility(View.GONE);
			else
				paramView.findViewById(R.id.empty).setVisibility(View.VISIBLE);

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

			restaurants = new ArrayList<Restaurant>(
					new RestaurantDao(dbHelper).findAll());

			log.debug("Update restaurants got restaurants");

			MealDao mdao = new MealDao(dbHelper);

			long partMilis = System.currentTimeMillis();

			// TreeMap<Long, SortedSet<Meal>> mmap = new TreeMap<Long,
			// SortedSet<Meal>>();
			// for (Meal m : mdao.findByDay(day)) {
			// SortedSet<Meal> s = mmap.get(m.getRestaurant().getId());
			// if (s == null)
			// mmap.put(m.getRestaurant().getId(), s = new TreeSet<Meal>());
			//
			// s.add(m);
			// }

			AvailabilityDao adao = new AvailabilityDao(dbHelper);

			log.debug("Update restaurants update start");
			for (Restaurant restaurant : restaurants) {
				restaurant
						.setMenu(mdao.findByDayAndRestaurant(day, restaurant));
				// SortedSet<Meal> m = mmap.get(restaurant.getId());
				// if (m != null)
				// restaurant.setMenu(m);
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
	private DailyMenuAdapter ad;
	private ExpandableListView menuList;
	private FragmentActivity act;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle args = getArguments();

		act = getActivity();
		View rootView = inflater.inflate(R.layout.day, container, false);

		ad = new DailyMenuAdapter(getActivity().getApplicationContext(),
				prepareDay(args));

		menuList = (ExpandableListView) rootView.findViewById(R.id.menu_list);
		menuList.setAdapter(ad);
		registerForContextMenu(menuList);

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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.restaurant_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
				.getMenuInfo();

		final Restaurant r = ad.getGroup(ExpandableListView
				.getPackedPositionGroup(info.packedPosition));

		switch (item.getItemId()) {

		case R.id.action_call:

			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ r.getAddress().getPhone()));
			log.debug("tel:" + r.getAddress().getPhone());
			startActivity(intent);

			return true;

		case R.id.action_locate:
			// TODO
			return true;

		case R.id.action_delete:

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(
					getResources().getString(R.string.delete_restaurant,
							r.getName()))
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									MealDao mdao = new MealDao(dbHelper);
									mdao.delete(r);
									AvailabilityDao adao = new AvailabilityDao(
											dbHelper);
									adao.delete(r);
									SourceDao sdao = new SourceDao(dbHelper);
									sdao.delete(r);
									RestaurantDao rdao = new RestaurantDao(
											dbHelper);
									rdao.delete(r);
									getActivity().deleteFile(
											r.getTemplateName());
									dbHelper.notifyDataSetChanged();

								}
							}).setNegativeButton("No", null)
					.setCancelable(false).show();
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	private Calendar prepareDay(Bundle args) {
		Calendar day = Calendar.getInstance(Locale.getDefault());
		Long time = args.getLong(DayFragment.ARG_DAY);
		if (time == null)
			time = System.currentTimeMillis();
		day.setTime(new Date(time));
		return day;
	}
}