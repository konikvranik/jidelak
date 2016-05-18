package net.suteren.android.jidelak.ui;

import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.SimpleCursorAdapter;
import com.terlici.dragndroplist.DragNDropAdapter;
import com.terlici.dragndroplist.DragNDropListView;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.provider.JidelakProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.suteren.android.jidelak.dao.AvailabilityDao.*;
import static net.suteren.android.jidelak.provider.JidelakProvider.DATES_URI;

public class MainActivity extends AbstractJidelakActivity implements
        TabListener, OnNavigationListener {

    private static final int DATES_LOADER = 0;
    private static Logger log = LoggerFactory.getLogger(MainActivity.class);

    private ViewPager dayPagerView;

    private Menu mainMenu;

    private DayPagerAdapter dayPagerAdapter;

    private boolean mBound = false;


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get
            // LocalService instance
            log.debug("service connected");
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            log.debug("service disconnected");
            mBound = false;
        }

    };

    private DrawerLayout drawer;

    private ActionBarDrawerToggle drawerToggle;

    private DragNDropListView restaurantListView;

    private DragNDropRestaurantListAdapter restaurantAdapter;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
     *                           is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        restaurantListView = (DragNDropListView) getWindow().findViewById(R.id.restaurants);
        if (restaurantListView != null) {
            restaurantAdapter = new DragNDropRestaurantListAdapter(null);
            restaurantListView.setDragNDropAdapter(restaurantAdapter);
        }

        setupDrawer();

        goToToday();

        log.debug("DemoReceiver.onReceive(ACTION_BOOT_COMPLETED)");

        updateHomeIcon();

        getLoaderManager().restartLoader(DATES_LOADER, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getApplicationContext(), DATES_URI,
                        new String[]{DAY.getName()},
                        String.format("%s is not null and %s is not null and %s is not null and %s is null",
                                DAY, MONTH, YEAR, RESTAURANT), null,
                        YEAR + SQL_SEPARATOR + MONTH + SQL_SEPARATOR + DAY);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                getDayPagerAdapter().changeCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });

    }

    private View emptyView;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mBound && !hasFocus) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    protected void goToToday() {
        goToDay(getDayPagerAdapter().getPositionByDate(System.currentTimeMillis()));
    }

    protected void goToDay(int arg0) {
        getDayPagerView().setCurrentItem(arg0);
        updateHomeIcon();
    }

    protected boolean isTodaySelected(int arg0) {
        return arg0 == getDayPagerAdapter().getPositionByDate(System.currentTimeMillis());
    }

    protected void updateHomeIcon() {
        if (drawerToggle == null) {
            actionBar
                    .setDisplayHomeAsUpEnabled(!(isTodaySelected() || getDayPagerAdapter()
                            .isEmpty()));
        } else {
            drawerToggle.setDrawerIndicatorEnabled(isDrawerIconEnabled());
            drawerToggle.syncState();
        }
    }

    private boolean isDrawerIconEnabled() {
        if (drawer == null)
            return false;
        return isTodaySelected() || drawer.isDrawerOpen(Gravity.LEFT) || getDayPagerAdapter().isEmpty();
    }

    private boolean isTodaySelected() {
        return isTodaySelected(getDayPagerView().getCurrentItem());
    }

    private void setupDrawer() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer == null)
            return;
        drawerToggle = new ActionBarDrawerToggle(this, drawer,
                R.drawable.ic_drawer, R.string.open, android.R.string.cancel) {
            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                updateHomeIcon();
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateHomeIcon();
            }
        };
        drawer.setDrawerListener(drawerToggle);
    }

    private void setupRestaurantView() {

        restaurantListView = (DragNDropListView) getWindow().findViewById(R.id.restaurants);

        if (restaurantListView == null)
            return;

        restaurantAdapter = new DragNDropRestaurantListAdapter(null);
        restaurantListView.setDragNDropAdapter(restaurantAdapter);

    }

    private DayPagerAdapter getDayPagerAdapter() {
        if (dayPagerAdapter == null) {
            dayPagerAdapter = new DayPagerAdapter(this, getSupportFragmentManager(), null);
            actionBar.setListNavigationCallbacks(dayPagerAdapter, this);
            updateDates();
        }
        return dayPagerAdapter;
    }

    private void updateDates() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                actionBar.removeAllTabs();
                if (!dayPagerAdapter.isEmpty())
                    for (int i = 0; i < dayPagerAdapter.getCount(); i++) {
                        actionBar.addTab(actionBar.newTab()
                                .setText(dayPagerAdapter.getPageTitle(i))
                                .setTabListener(MainActivity.this));
                    }

                if (restaurantAdapter != null)
                    chooseMainView(restaurantAdapter.isEmpty());

                setupActionBar();
            }

            private void chooseMainView(boolean empty) {
                FrameLayout pagerFrame = (FrameLayout) getWindow()
                        .findViewById(R.id.pager_frame);
                if (empty) {
                    MainActivity.log.debug("Showing empty view and hiding frame");
                    if (pagerFrame != null) {
                        pagerFrame.setVisibility(View.GONE);
                        MainActivity.log.debug("daypager hidden");
                    }
                    if (getEmptyView() != null) {
                        getEmptyView().setVisibility(View.VISIBLE);
                        MainActivity.log.debug("empty view displayed");
                    }

                    getWindow().findViewById(R.id.empty_restaurants)
                            .setVisibility(View.VISIBLE);
                    getWindow().findViewById(R.id.restaurants_content)
                            .setVisibility(View.GONE);

                } else {
                    MainActivity.log.debug("Hiding empty view and showing frame");
                    if (getEmptyView() != null) {
                        getEmptyView().setVisibility(View.GONE);
                        MainActivity.log.debug("empty view hidden");
                    }
                    if (pagerFrame != null) {
                        pagerFrame.setVisibility(View.VISIBLE);
                        MainActivity.log.debug("daypager displayed");
                    }
                    getWindow().findViewById(R.id.empty_restaurants)
                            .setVisibility(View.GONE);
                    getWindow().findViewById(R.id.restaurants_content)
                            .setVisibility(View.VISIBLE);
                }
            }

        });
        MainActivity.log.debug("Update dates end");
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

            // case R.id.action_reorder_restaurants:
            // startActivity(new Intent(this, RestaurantManagerActivity.class));
            // return true;

            case R.id.action_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;

            // case R.id.action_find_on_map:
            // startActivity(new Intent(this, MapActivity.class));
            // return true;

            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;

            case android.R.id.home:
                if (isDrawerIconEnabled()) {
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
        actionBar = super.setupActionBar();

        if (getDayPagerAdapter().isEmpty()) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            // actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        } else {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            // actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        return actionBar;
    }


    protected String buildFragmentTag(ViewPager view, long id) {
        return "android:switcher:" + view.getId() + ":" + id;
    }

    private class DragNDropRestaurantListAdapter extends SimpleCursorAdapter implements DragNDropAdapter {

        public DragNDropRestaurantListAdapter(Cursor restaurants) {
            super(getApplicationContext(), R.layout.draggable_restaurant, restaurants,
                    new String[]{RestaurantDao.NAME.getName(), RestaurantDao.ID.getName()},
                    new int[]{R.id.name, R.id.open}, 0);
        }


        @Override
        public void onItemDrag(DragNDropListView parent, View view,
                               int position, long id) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onItemDrop(DragNDropListView parent, View view, int startPosition, int endPosition, long id) {
            ContentValues v = new ContentValues();
            v.put("from", startPosition);
            v.put("to", endPosition);
            getContentResolver().update(JidelakProvider.REORDER_URI, v,
                    String.format("%s=?", RestaurantDao.ID.getName()), new String[]{String.valueOf(id)});
        }

        @Override
        public int getDragHandler() {
            return R.id.handler;
        }

    }

    private void closeDrawer() {
        if (drawer == null)
            return;
        drawer.closeDrawer(Gravity.LEFT);
    }

    private ViewPager getDayPagerView() {
        if (dayPagerView == null) {
            dayPagerView = (ViewPager) findViewById(R.id.pager);
            if (dayPagerView == null)
                return null;
            dayPagerView.setAdapter(getDayPagerAdapter());
            dayPagerView
                    .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                        @Override
                        public void onPageSelected(int position) {
                            actionBar.setSelectedNavigationItem(position);
                        }

                    });
        }
        return dayPagerView;
    }

    private View getEmptyView() {
        log.debug("Getting empty view");
        if (emptyView == null) {
            FrameLayout frameLayout = (FrameLayout) getWindow().findViewById(
                    R.id.content_frame);
            if (frameLayout == null)
                return null;
            emptyView = frameLayout.findViewById(R.id.empty);
            setupEmptyView(emptyView);
        }
        log.debug("Empty view retrieved");
        return emptyView;
    }

    private void setupEmptyView(View emptyviView) {
        WebView disclaimerView = (WebView) emptyviView
                .findViewById(R.id.empty_text);
        WebSettings settings = disclaimerView.getSettings();
        settings.setStandardFontFamily("serif");
        disclaimerView.setBackgroundColor(getResources().getColor(
                android.R.color.background_dark));
        log.debug("Setting up webview");
        disclaimerView
                .loadUrl("file:///android_res/raw/no_restaurants_disclaimer.html");
    }

    private DayFragment getActiveDayFragment() {
        if (getDayPagerView() == null || getDayPagerAdapter() == null)
            return null;
        return (DayFragment) getSupportFragmentManager().findFragmentByTag(
                buildFragmentTag(getDayPagerView(), getDayPagerAdapter()
                        .getItemId(getDayPagerView().getCurrentItem())));
    }

}
