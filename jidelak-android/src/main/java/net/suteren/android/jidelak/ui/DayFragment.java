package net.suteren.android.jidelak.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;
import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.model.Restaurant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static net.suteren.android.jidelak.provider.JidelakProvider.*;

public class DayFragment extends Fragment {

    private static final int DAYS_LOADER = 1;
    private static final int DELETE_LOADER = 2;

    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(DayFragment.class);

    private SimpleCursorTreeAdapter dailyMenuAdapter;

    private void showEmpty(boolean showEmpty, View paramView) {
        if (paramView == null)
            return;
        View emptyView = paramView.findViewById(R.id.empty);
        if (emptyView == null)
            return;
        if (showEmpty)
            emptyView.setVisibility(View.VISIBLE);
        else
            emptyView.setVisibility(View.GONE);
    }

    public static final String ARG_DAY = "day";
    private ExpandableListView dailyMenuList;

    private ExpandableListContextMenuInfo lastMenuInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Bundle args = getArguments();

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(args.getLong(ARG_DAY));

        View rootView = inflater.inflate(R.layout.day, container, false);

        dailyMenuAdapter = new SimpleCursorTreeAdapter(getContext(), null, R.layout.restaurant,
                new String[]{RestaurantDao.NAME.getName()},
                new int[]{R.id.name}, R.layout.meal, new String[]{MealDao.TITLE.getName(), MealDao.DESCRIPTION.getName(), MealDao.PRICE.getName()}, new int[]{R.id.name, R.id.description, R.id.price}) {
            @Override
            protected Cursor getChildrenCursor(final Cursor groupCursor) {
                final long restaurantId = groupCursor.getLong(groupCursor.getColumnIndex(RestaurantDao.ID.getName()));

                long loaderId = cal.get(Calendar.YEAR)
                        + cal.get(Calendar.MONTH) * 10000
                        + cal.get(Calendar.DAY_OF_MONTH) * 1000000
                        + restaurantId * 100000000;
                getLoaderManager().initLoader((int) loaderId, args, new LoaderManager.LoaderCallbacks<Cursor>() {

                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        return new CursorLoader(getContext(), MEALS_URI, new String[]{MealDao.ID.getName(), MealDao.TITLE.getName(), MealDao.DESCRIPTION.getName(), MealDao.PRICE.getName()},
                                String.format("m.%s = ? and (a.%s = ? OR a.%s is null) and (a.%s = ? OR a.%s is null)" +
                                                " and (a.%s = ? OR a.%s is null) and (a.%s = ? OR a.%s is null)",
                                        MealDao.RESTAURANT.getName(),
                                        AvailabilityDao.YEAR.getName(), AvailabilityDao.YEAR.getName(),
                                        AvailabilityDao.MONTH.getName(), AvailabilityDao.MONTH.getName(),
                                        AvailabilityDao.DAY.getName(), AvailabilityDao.DAY.getName(),
                                        AvailabilityDao.DOW.getName(), AvailabilityDao.DOW.getName()
                                ),
                                new String[]{
                                        String.format("%d", restaurantId),
                                        String.format("%d", cal.get(Calendar.YEAR)),
                                        String.format("%d", cal.get(Calendar.MONTH)),
                                        String.format("%d", cal.get(Calendar.DAY_OF_MONTH)),
                                        String.format("%d", cal.get(Calendar.DAY_OF_WEEK))
                                }, String.format("%s, %s", MealDao.DISH.getName(), MealDao.CATEGORY.getName()));
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        setChildrenCursor(groupCursor.getPosition(), data);
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {

                    }
                });

                return null;
            }
        };

        dailyMenuList = (ExpandableListView) rootView.findViewById(R.id.menu_list);
        dailyMenuList.setEmptyView(dailyMenuList.findViewById(R.id.empty));

        // set refresh action when swipe down list of days.
        final SwipeRefreshLayout refresh = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload(new Runnable() {
                    @Override
                    public void run() {
                        refresh.setRefreshing(false);
                    }
                });
            }
        });

        getLoaderManager().initLoader(DAYS_LOADER, null, new LoaderManager
                .LoaderCallbacks<Cursor>() {

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getContext(), RESTAURANTS_URI, new String[]{RestaurantDao.ID.getName(), RestaurantDao.NAME.getName()}, null,
                        new String[]{}, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                dailyMenuAdapter.changeCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });



        if (dailyMenuAdapter.isEmpty()) {
            dailyMenuList.setVisibility(View.GONE);
            showEmpty(true, rootView);
            setupEmpty(rootView);
        } else {
            dailyMenuList.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.empty).setVisibility(View.GONE);

        }
        registerForContextMenu(dailyMenuList);
        dailyMenuList.setAdapter(dailyMenuAdapter);

        for (int i = 0; i < dailyMenuAdapter.getGroupCount(); i++) {
            dailyMenuList.expandGroup(i);
        }

        return rootView;
    }

    private void reload(final Runnable onFinishHook) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                getContext().getContentResolver().update(RELOAD_URI, null, null, null);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                onFinishHook.run();
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                onFinishHook.run();
            }
        };

    }

    private void setupEmpty(View rootView) {
        WebView disclaimerView = (WebView) rootView
                .findViewById(R.id.empty_text);
        WebSettings settings = disclaimerView.getSettings();
        settings.setStandardFontFamily("serif");
        disclaimerView.setBackgroundColor(getResources().getColor(
                android.R.color.background_dark));
        disclaimerView
                .loadUrl("file:///android_res/raw/no_restaurants_disclaimer.html");
    }

    public ExpandableListView getMenuList() {
        return dailyMenuList;
    }

    @Override
    public void onResume() {
        super.onResume();
        dailyMenuAdapter.notifyDataSetChanged();
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

        final Cursor r = dailyMenuAdapter.getGroup(ExpandableListView
                .getPackedPositionGroup(info.packedPosition));

        String uri;
        switch (item.getItemId()) {

            case R.id.action_call:

                uri = "tel:" + r.getString(r.getColumnIndex(RestaurantDao.PHONE.getName()));
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                startActivity(intent);

                return true;

            case R.id.action_locate:
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    try {
                        List<android.location.Address> addresses = geocoder.getFromLocationName(
                                r.getString(r.getColumnIndex(RestaurantDao.ADDRESS.getName())) + "," +
                                        r.getString(r.getColumnIndex(RestaurantDao.CITY.getName())) + "," +
                                        r.getString(r.getColumnIndex(RestaurantDao.COUNTRY.getName()))
                                , 1);

                        android.location.Address address = addresses.get(0);
                        uri = "geo:" + address.getLatitude() + ","
                                + address.getLongitude();
                        startActivity(new Intent(
                                android.content.Intent.ACTION_VIEW, Uri.parse(uri)));

                    } catch (IOException e) {
                        throw new JidelakException(getResources().getString(
                                R.string.unable_to_get_location), e);
                    }
                } catch (JidelakException e1) {
                    Toast.makeText(getActivity(), R.string.unable_to_get_location,
                            Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.details:
                startActivity(new Intent(getActivity(), RestaurantActivity.class)
                        .putExtra("restaurant", r.getLong(r.getColumnIndex(RestaurantDao.ID.getName()))));
                return true;

            case R.id.action_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(
                        getResources().getString(R.string.delete_restaurant,
                                r.getString(r.getColumnIndex(RestaurantDao.NAME.getName()))))
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        long restaurantId = r.getLong(r.getColumnIndex(RestaurantDao.ID.getName()));

                                        getContext().getContentResolver().delete(RESTAURANTS_URI,
                                                String.format("%s = ?", RestaurantDao.ID.getName()),
                                                new String[]{String.format("%d", restaurantId)});

                                        getActivity().deleteFile(Restaurant.getTemplateName(restaurantId));

                                    }
                                }).setNegativeButton("No", null)
                        .setCancelable(false).show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private static Calendar prepareDay(Bundle args) {
        Calendar day = Calendar.getInstance(Locale.getDefault());
        Long time = args.getLong(DayFragment.ARG_DAY);
        if (time == null)
            time = System.currentTimeMillis();
        day.setTime(new Date(time));
        return day;
    }

    public SimpleCursorTreeAdapter getAdapter() {
        return dailyMenuAdapter;
    }
}