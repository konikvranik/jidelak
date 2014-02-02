package net.suteren.android.jidelak.ui;

import static net.suteren.android.jidelak.Constants.CATEGORY_BACKGROUND_KEY;
import static net.suteren.android.jidelak.Constants.DEFAULT_PREFERENCES;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DayFragment extends Fragment {

	private static Logger log = LoggerFactory.getLogger(DayFragment.class);

	private JidelakDbHelper dbHelper;

	public class DailyMenuAdapter extends BaseExpandableListAdapter {

		private static final boolean ENABLE_UPPER_SHADOW = false;
		private Context ctx;
		private final Calendar day;
		private List<Restaurant> restaurants;
		private MealUpdateWorker menuUpdater = new MealUpdateWorker();

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

			boolean showDish = false;
			if (paramInt2 > 0) {
				Meal prevMeal = getChild(paramInt1, paramInt2 - 1);
				if (((prevMeal == null || prevMeal.getDish() == null) && meal
						.getDish() != null)
						|| prevMeal.getDish() != meal.getDish()) {
					showDish = true;
				} else {
					showDish = false;
				}
			} else {
				showDish = true;
			}

			TextView dishView = (TextView) paramView.findViewById(R.id.dish);

			if (showDish) {
				dishView.setText(meal.getDish().getResource());
				dishView.setVisibility(View.VISIBLE);
			} else {
				dishView.setVisibility(View.GONE);
			}

			((TextView) paramView.findViewById(R.id.name)).setText(meal
					.getTitle());
			((TextView) paramView.findViewById(R.id.description)).setText(meal
					.getDescription());
			((TextView) paramView.findViewById(R.id.price)).setText(meal
					.getPrice());

			View menuView = paramView.findViewById(R.id.menu);
			menuView.setBackgroundColor(getResources().getColor(
					R.color.RestaurantBackground));
			if (getActivity().getSharedPreferences(DEFAULT_PREFERENCES,
					Context.MODE_PRIVATE).getBoolean(CATEGORY_BACKGROUND_KEY,
					true)) {
				String cat = meal.getCategory();
				if (cat != null) {

					Drawable background = getResources().getDrawable(
							R.drawable.meal_background);

					Integer color = null;
					if (cat.matches(".*vegetar.*")) {
						color = getResources()
								.getColor(R.color.vegeterian_meal);
					} else if (cat.matches(".*salad.*")) {
						color = getResources().getColor(R.color.cold_meal);
					} else if (cat.matches(".*pasta.*")) {
						color = getResources().getColor(R.color.pasta_meal);
					} else if (cat.matches(".*live.*")) {
						color = getResources().getColor(R.color.live_meal);
					} else if (cat.matches(".*(steak|superior).*")) {
						color = getResources().getColor(R.color.steak_meal);
					}

					log.debug("Color for category " + cat + ": " + color);

					if (color != null) {
						if (background instanceof ShapeDrawable) {
							((ShapeDrawable) background).getPaint().setColor(
									color);
							log.debug("Set color to ShapeDrawable");
						} else if (background instanceof GradientDrawable) {
							((GradientDrawable) background).setColor(color);
							log.debug("Set color to GradientDrawable");
						}
						background.setAlpha(50);
						menuView.setBackgroundDrawable(background);
					}
				}
			}

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
			log.debug("Update restaurants start");
			menuUpdater.doInBackground(new Void[] {});
		}

		private class MealUpdateWorker extends AsyncTask<Void, Void, Void> {

			@Override
			protected Void doInBackground(Void... params) {

				long milis = System.currentTimeMillis();
				log.debug("Update restaurants background start");

				restaurants = new ArrayList<Restaurant>(new RestaurantDao(
						dbHelper).findAll());

				log.debug("Update restaurants got restaurants");

				long partMilis = System.currentTimeMillis();
				MealDao mdao = new MealDao(dbHelper);

				AvailabilityDao adao = new AvailabilityDao(dbHelper);

				log.debug("Update restaurants update start");
				for (Restaurant restaurant : restaurants) {
					restaurant.setMenu(mdao.findByDayAndRestaurant(day,
							restaurant));
					restaurant.setOpeningHours(new TreeSet<Availability>(adao
							.findByRestaurant(restaurant)));
				}

				log.debug("Update restaurants update end: "
						+ (System.currentTimeMillis() - partMilis));

				log.debug("Update restaurants background end: "
						+ (System.currentTimeMillis() - milis));
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				notifyAdapter();
			}

			@Override
			protected void onCancelled() {
				super.onCancelled();
				notifyAdapter();
			}

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			protected void onCancelled(Void result) {
				super.onCancelled(result);
				notifyAdapter();
			}

			private void notifyAdapter() {
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ad.notifyDataSetChanged();
					}
				});
			}
		}
	}

	public static final String ARG_DAY = "day";
	private DailyMenuAdapter ad;
	private ExpandableListView menuList;
	private FragmentActivity act;

	private ExpandableListContextMenuInfo lastMenuInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle args = getArguments();

		act = getActivity();
		View rootView = inflater.inflate(R.layout.day, container, false);

		ad = new DailyMenuAdapter(getActivity().getApplicationContext(),
				prepareDay(args));

		if (ad.isEmpty()) {
			rootView.findViewById(R.id.menu_list).setVisibility(View.GONE);
			rootView.findViewById(R.id.empty).setVisibility(View.VISIBLE);
			WebView disclaimerView = (WebView) rootView
					.findViewById(R.id.empty_text);

			WebSettings settings = disclaimerView.getSettings();
			settings.setLoadWithOverviewMode(false);
			disclaimerView
					.loadUrl("file:///android_res/raw/no_restaurants_disclaimer.html");
		} else {
			rootView.findViewById(R.id.menu_list).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.empty).setVisibility(View.GONE);

		}
		menuList = (ExpandableListView) rootView.findViewById(R.id.menu_list);
		registerForContextMenu(menuList);
		menuList.setAdapter(ad);

		for (int i = 0; i < ad.getGroupCount(); i++) {
			menuList.expandGroup(i);
		}

		final JidelakMainActivity act = (JidelakMainActivity) getActivity();
		act.getDbHelper().registerObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				ad.updateRestaurants();
			}

			@Override
			public void onInvalidated() {
				super.onInvalidated();
				ad.updateRestaurants();
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

		if (info == null) {
			info = lastMenuInfo;
		} else {
			lastMenuInfo = info;
		}

		log.debug("Item: " + item);
		log.debug("Info: " + info);
		log.debug("Adapter: " + ad);

		final Restaurant r = ad.getGroup(ExpandableListView
				.getPackedPositionGroup(info.packedPosition));

		String uri;
		switch (item.getItemId()) {

		case R.id.action_call:

			uri = "tel:" + r.getAddress().getPhone();
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
			log.debug("Opening dialer: " + uri);
			startActivity(intent);

			return true;

		case R.id.action_locate:
			Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
			try {
				try {
					log.debug("Requesting position for " + r.getAddress());
					Address addr = new Address(r.getAddress().getLocale());

					Restaurant.cloneAddress(r.getAddress(), addr);

					List<Address> addresses = geocoder.getFromLocationName(
							addr.toString(), 1);

					if (addresses.isEmpty()) {

						addr.setCountryName(null);
						addr.setPostalCode(null);
						addr.setPhone(null);
						addr.setExtras(null);
						addr.setUrl(null);
						addr.setLocality(addr.getLocality().replaceAll("\\d*",
								""));

						log.debug("Rerequesting position for " + r.getAddress());
						geocoder.getFromLocationName(addr.toString(), 1);
						if (addresses.isEmpty()) {
							throw new JidelakException(
									R.string.unable_to_get_location);
						}
					}
					Address address = addresses.get(0);
					uri = "geo:" + address.getLatitude() + ","
							+ address.getLongitude();
					log.debug("Opening map: " + uri);
					startActivity(new Intent(
							android.content.Intent.ACTION_VIEW, Uri.parse(uri)));

				} catch (IOException e) {
					throw new JidelakException(R.string.unable_to_get_location,
							e);
				}
			} catch (JidelakException e1) {
				Toast.makeText(getActivity(), R.string.unable_to_get_location,
						Toast.LENGTH_LONG).show();
			}
			return true;

		case R.id.details:
			startActivity(new Intent(getActivity(), RestaurantActivity.class)
					.putExtra("restaurant", r.getId()));
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