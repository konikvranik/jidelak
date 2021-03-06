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

import android.content.res.Resources;
import net.suteren.android.jidelak.AndroidUtils;
import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Address;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Dish;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.Geocoder;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DayFragment extends Fragment {

    @SuppressWarnings("unused")
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
            dbHelper = JidelakDbHelper.getInstance(ctx);

            updateRestaurants();
        }

        @Override
        public Meal getChild(int paramInt1, int paramInt2) {
            List<Meal> ml = new ArrayList<Meal>(getGroup(paramInt1).getMenu());
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

            if (meal == null)
                return null;

            boolean showDish = false;
            if (paramInt2 > 0) {
                Meal prevMeal = getChild(paramInt1, paramInt2 - 1);
                Dish dish1 = meal.getDish();
                Dish dish2 = prevMeal == null ? null : prevMeal.getDish();
                showDish = !(dish1 != null && dish2 != null && dish1 == dish2)
                        || (dish1 == null && dish2 == null);
            } else {
                showDish = true;
            }

            TextView dishView = (TextView) paramView.findViewById(R.id.dish);

            if (showDish && meal.getDish() != null) {
                int dishResourceId = dishView.getResources().getIdentifier(meal.getDish().getResourceName(), "string",
                        ctx.getPackageName());
                if (dishResourceId > 0)
                    dishView.setText(dishResourceId);
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
                    } else if (cat.matches(".*fried.*")) {
                        color = getResources().getColor(R.color.fried_meal);
                    } else if (cat.matches(".*fish.*")) {
                        color = getResources().getColor(R.color.fish_meal);
                    } else if (cat.matches(".*(caffee|tee).*")) {
                        color = getResources()
                                .getColor(R.color.warm_drink_meal);
                    } else if (cat.matches(".*coctail.*")) {
                        color = getResources().getColor(R.color.coctail_meal);
                    } else if (cat.matches(".*juice.*")) {
                        color = getResources().getColor(R.color.juice_meal);
                    } else if (cat.matches(".*red-wine.*")) {
                        color = getResources().getColor(R.color.red_wine_meal);
                    } else if (cat.matches(".*beer.*")) {
                        color = getResources().getColor(R.color.beer_meal);
                    } else if (cat.matches(".*white-wine.*")) {
                        color = getResources()
                                .getColor(R.color.white_wine_meal);
                    } else if (cat.matches(".*rose-wine.*")) {
                        color = getResources().getColor(R.color.rose_wine_meal);
                    } else if (cat.matches(".*(ladies|child|half).*")) {
                        color = getResources().getColor(R.color.half_meal);
                    } else if (cat.matches(".*(steak|superior).*")) {
                        color = getResources().getColor(R.color.steak_meal);
                    }

                    if (color != null) {
                        if (background instanceof ShapeDrawable) {
                            ((ShapeDrawable) background).getPaint().setColor(
                                    color);
                        } else if (background instanceof GradientDrawable) {
                            ((GradientDrawable) background).setColor(color);
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
        public View getGroupView(final int paramInt, boolean paramBoolean,
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
                    startActivity(new Intent(getActivity(),
                            RestaurantActivity.class).putExtra("restaurant",
                            getGroupId(paramInt)));
                    // act.openContextMenu(v);
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

            showEmpty(!(getChildrenCount(paramInt) > 0), paramView);

            Restaurant restaurant = getGroup(paramInt);

            TextView nameView = (TextView) paramView.findViewById(R.id.name);
            nameView.setText(restaurant.getName());

            TextView openingView = (TextView) paramView.findViewById(R.id.open);
            openingView.setText(AndroidUtils.openingHoursToString(
                    getActivity(), day, restaurant));

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
            updateMeals();
            notifyAdapter();
        }

        protected void updateMeals() {

            restaurants = new ArrayList<Restaurant>(
                    new RestaurantDao(dbHelper).findAll());

            MealDao mdao = new MealDao(dbHelper);

            AvailabilityDao adao = new AvailabilityDao(dbHelper);

            for (Restaurant restaurant : restaurants) {
                restaurant
                        .setMenu(mdao.findByDayAndRestaurant(day, restaurant));
                restaurant.setOpeningHours(new TreeSet<Availability>(adao
                        .findByRestaurant(restaurant)));
            }

        }

        private void notifyAdapter() {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dailyMenuAdapter != null)
                        dailyMenuAdapter.notifyDataSetChanged();
                }
            });
        }

        public int countAbsolutePosition(int paramInt) {
            if (paramInt > getGroupCount() - 1)
                paramInt = getGroupCount() - 2;
            int count = 0;
            for (int i = 0; i < paramInt; i++)
                count += getChildrenCount(i) + 1;
            return count;
        }

    }

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

    public DailyMenuAdapter getAdapter() {
        return dailyMenuAdapter;
    }

    public static final String ARG_DAY = "day";
    private DailyMenuAdapter dailyMenuAdapter;
    private ExpandableListView dailyMenuList;
    private FragmentActivity act;

    private ExpandableListContextMenuInfo lastMenuInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();

        act = getActivity();
        View rootView = inflater.inflate(R.layout.day, container, false);

        dailyMenuAdapter = new DailyMenuAdapter(getActivity()
                .getApplicationContext(), prepareDay(args));

        dailyMenuList = (ExpandableListView) rootView
                .findViewById(R.id.menu_list);
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

        final MainActivity act = (MainActivity) getActivity();
        act.getDbHelper().registerObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                dailyMenuAdapter.updateRestaurants();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                dailyMenuAdapter.updateRestaurants();
            }
        });

        return rootView;
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

        final Restaurant r = dailyMenuAdapter.getGroup(ExpandableListView
                .getPackedPositionGroup(info.packedPosition));

        String uri;
        switch (item.getItemId()) {

            case R.id.action_call:

                uri = "tel:" + r.getAddress().getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                startActivity(intent);

                return true;

            case R.id.action_locate:
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    try {
                        Address addr = new Address(r.getAddress().getLocale());

                        AndroidUtils.cloneAddress(r.getAddress(), addr);

                        List<android.location.Address> addresses = geocoder
                                .getFromLocationName(addr.toString(), 1);

                        if (addresses.isEmpty()) {

                            addr.setCountryName(null);
                            addr.setPostalCode(null);
                            addr.setPhone(null);
                            addr.setExtras(null);
                            addr.setUrl(null);
                            addr.setLocality(addr.getLocality().replaceAll("\\d*",
                                    ""));

                            geocoder.getFromLocationName(addr.toString(), 1);
                            if (addresses.isEmpty()) {
                                throw new JidelakException(getResources()
                                        .getString(R.string.unable_to_get_location));
                            }
                        }
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

    public void notifyDataSetChanged() {
        if (dailyMenuAdapter != null)
            dailyMenuAdapter.notifyAdapter();
    }

    public void notifyDataSetInvalidated() {
        if (dailyMenuAdapter != null)
            dailyMenuAdapter.notifyAdapter();
    }
}