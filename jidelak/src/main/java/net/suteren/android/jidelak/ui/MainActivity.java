package net.suteren.android.jidelak.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.ui.FeederService.FeederServiceBinder;

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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.terlici.dragndroplist.DragNDropAdapter;
import com.terlici.dragndroplist.DragNDropListView;

public class MainActivity extends AbstractJidelakActivity implements
		TabListener, OnNavigationListener {

	private static Logger log = LoggerFactory.getLogger(MainActivity.class);

	private ViewPager pagerView;

	private Menu mainMenu;

	private DayPagerAdapter dpa;

	private boolean mBound = false;

	private FeederService mService;

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			log.debug("service connected");
			FeederServiceBinder binder = (FeederServiceBinder) service;
			mService = binder.getService();
			mBound = true;

			mService.registerStartObserver(new DataSetObserver() {

				@Override
				public void onChanged() {
					updateRefreshButton();

				}

			});
			updateRefreshButton();

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			log.debug("service disconnected");
			mBound = false;
			updateRefreshButton();
		}

	};

	private DrawerLayout drawer;

	private ActionBarDrawerToggle drawerToggle;

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
		ab.setListNavigationCallbacks(dpa, this);

		setupPagerView();

		setupDrawer();

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawer,
				R.drawable.ic_drawer, R.string.open, android.R.string.cancel) {
			/** Called when a drawer has settled in a completely closed state. */
			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				setupHomeButton(isTodaySelected(pagerView.getCurrentItem()));
			}

			/** Called when a drawer has settled in a completely open state. */
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				setupHomeButton(isTodaySelected(pagerView.getCurrentItem()));
			}
		};
		drawer.setDrawerListener(drawerToggle);

		goToToday();

		getDbHelper().registerObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				dpa.updateDates();
				super.onChanged();
			}

			@Override
			public void onInvalidated() {
				dpa.updateDates();
				super.onInvalidated();
			}

		});

		log.debug("DemoReceiver.onReceive(ACTION_BOOT_COMPLETED)");
		startService(new Intent(this, FeederService.class).putExtra("register",
				true));

		setupHomeButton(true);

	}

	private void setupDrawer() {
		// TODO Auto-generated method stub
		DragNDropListView ddlv = (DragNDropListView) getWindow().findViewById(
				R.id.restaurants);

		final DragNDropRestaurantListAdapter ddsa = new DragNDropRestaurantListAdapter(
				new RestaurantDao(JidelakDbHelper.getInstance(this)).findAll());
		ddlv.setDragNDropAdapter(ddsa);

		ImageButton cancel = (ImageButton) getWindow()
				.findViewById(R.id.cancel);
		cancel.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.buttons).setVisibility(View.GONE);
				ddsa.setRestaurants(new RestaurantDao(JidelakDbHelper
						.getInstance(getApplicationContext())).findAll());
				ddsa.notifyDataSetChanged();
				drawer.closeDrawer(Gravity.LEFT);
			}
		});

		ImageButton save = (ImageButton) getWindow().findViewById(R.id.save);
		save.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				JidelakDbHelper dbHelper = JidelakDbHelper
						.getInstance(getApplication());
				new RestaurantDao(dbHelper).update(ddsa.getRestaurants());
				dbHelper.notifyDataSetChanged();
				dpa.updateDates();
				findViewById(R.id.buttons).setVisibility(View.GONE);
				ddsa.resetChanged();
				drawer.closeDrawer(Gravity.LEFT);
			}
		});
	}

	private boolean todaySelected;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (mBound && !hasFocus) {
			unbindService(mConnection);
			mBound = false;
		} else if (!mBound && hasFocus) {
			boolean res = bindService(new Intent(this, FeederService.class),
					mConnection, Context.BIND_NOT_FOREGROUND);
			log.debug("Bind result: " + res);
		}

	}

	protected void goToToday() {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(System.currentTimeMillis());
		goToDay(dpa.getPositionByDate(cal));
	}

	protected void goToDay(int arg0) {
		pagerView.setCurrentItem(arg0);
		setupHomeButton(isTodaySelected(arg0));
	}

	protected boolean isTodaySelected(int arg0) {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(System.currentTimeMillis());

		return todaySelected = (arg0 == dpa.getPositionByDate(cal));
	}

	protected void setupHomeButton(boolean today) {
		// ab.setDisplayHomeAsUpEnabled(back);
		drawerToggle.setDrawerIndicatorEnabled(today
				|| drawer.isDrawerOpen(Gravity.LEFT));
		drawerToggle.syncState();
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

	private void updateRefreshButton() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (mBound && mService != null && mService.isRunning()) {

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						startRefreshHc();
					else
						startRefreshFr();
				} else {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						stopRefreshHc();
					else
						stopRefreshFr();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mainMenu = menu;
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jidelak, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_update:

			if (mBound && mService.isRunning()) {
				Toast.makeText(getApplicationContext(),
						R.string.update_already_running, Toast.LENGTH_SHORT)
						.show();
				return true;
			}
			Intent intent = new Intent(this, FeederService.class);
			startService(intent);

			return true;

			// case R.id.action_reorder_restaurants:
			// startActivity(new Intent(this, RestaurantManagerActivity.class));
			// return true;

		case R.id.action_settings:
			startActivity(new Intent(this, PreferencesActivity.class));
			return true;

		case R.id.action_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;

		case android.R.id.home:
			if (todaySelected || drawer.isDrawerOpen(Gravity.LEFT)) {
				if (drawerToggle.onOptionsItemSelected(item)) {
					return true;
				}
				return super.onOptionsItemSelected(item);
			}
			goToToday();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		goToDay(arg0);
		return true;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		goToDay(tab.getPosition());
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}

	@Override
	protected ActionBar setupActionBar() {
		ab = super.setupActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		return ab;
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
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					notifyDataSetChanged();
					ab.removeAllTabs();
					for (int i = 0; i < getCount(); i++) {
						ab.addTab(ab.newTab().setText(getPageTitle(i))
								.setTabListener(MainActivity.this));

					}

				}
			});
			log.debug("Update dates end");
		}

	}

	protected String buildFragmentTag(ViewPager view, long id) {
		return "android:switcher:" + view.getId() + ":" + id;
	}

	private class DragNDropRestaurantListAdapter extends BaseAdapter implements
			DragNDropAdapter {

		private List<Restaurant> restaurants;

		private boolean changed = false;

		public DragNDropRestaurantListAdapter(Collection<Restaurant> restaurants) {
			super();
			this.restaurants = new ArrayList<Restaurant>(restaurants);
		}

		@Override
		public int getCount() {
			return restaurants.size();
		}

		@Override
		public Restaurant getItem(int paramInt) {
			return restaurants.get(paramInt);
		}

		@Override
		public long getItemId(int paramInt) {
			return getItem(paramInt).getId();
		}

		@Override
		public View getView(final int paramInt, View paramView,
				ViewGroup paramViewGroup) {

			if (paramView == null) {
				paramView = View.inflate(getApplicationContext(),
						R.layout.draggable_restaurant, null);
			}

			paramView.findViewById(R.id.header).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (changed)
								return;
							DayFragment page = (DayFragment) getSupportFragmentManager()
									.findFragmentByTag(
											buildFragmentTag(pagerView, dpa
													.getItemId(pagerView
															.getCurrentItem())));
							if (page == null)
								return;
							ExpandableListView menuListView = (ExpandableListView) page
									.getMenuList();
							if (menuListView == null)
								return;
							menuListView.setSelection(page.getAdapter()
									.countAbsolutePosition(paramInt));
							drawer.closeDrawer(Gravity.LEFT);
						}

					});

			Restaurant restaurant = getItem(paramInt);

			TextView nameView = (TextView) paramView.findViewById(R.id.name);
			nameView.setText(restaurant.getName());

			TextView openingView = (TextView) paramView.findViewById(R.id.open);
			openingView.setText(restaurant
					.openingHoursToString(getApplicationContext()));

			return paramView;
		}

		@Override
		public void onItemDrag(DragNDropListView parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onItemDrop(DragNDropListView parent, View view,
				int startPosition, int endPosition, long id) {
			Restaurant restaurant = restaurants.remove(startPosition);
			restaurants.add(endPosition, restaurant);

			for (int i = 0; i < restaurants.size(); i++)
				restaurants.get(i).setPosition(i);

			notifyDataSetChanged();

			changed = true;
			findViewById(R.id.buttons).setVisibility(View.VISIBLE);

		}

		@Override
		public int getDragHandler() {
			return R.id.handler;
		}

		public List<Restaurant> getRestaurants() {
			return restaurants;
		}

		public void setRestaurants(Collection<Restaurant> sortedSet) {
			this.restaurants = new ArrayList<Restaurant>(sortedSet);
			resetChanged();
			notifyDataSetChanged();
		}

		public void resetChanged() {
			changed = false;
		}

	}
}
