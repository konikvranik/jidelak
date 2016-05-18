package net.suteren.android.jidelak.provider;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import net.suteren.android.jidelak.*;
import net.suteren.android.jidelak.dao.*;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static net.suteren.android.jidelak.Constants.*;

/**
 * Created by vranikp on 8.4.16.
 *
 * @author vranikp
 */
public class JidelakProvider extends ContentProvider {

    private static Logger log = LoggerFactory.getLogger(JidelakProvider.class);

    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher = new UriMatcher(0);
    /**
     * jidelak provider uri base.
     */
    public static final String URI_BASE = "net.suteren.jidelak.provider";
    private static final String DAY_PATH = "day";
    private static final int MATCHED_DAY = 1;
    private static final String RESTAURANT_PATH = "restaurant";
    private static final int MATCHED_RESTAURANT = 2;
    private static final String MEAL_PATH = "meal";
    private static final int MATCHED_MEAL = 3;
    private static final String AVAILABILITY_PATH = "availability";
    private static final int MATCHED_AVAILABILITY = 4;
    private static final String SOURCE_PATH = "source";
    private static final int MATCHED_SOURCE = 5;
    private static final String RELOAD_PATH = "reload";
    private static final int MATCHED_RELOAD = 0;
    public static final String SCHEME_CONTENT = "content";

    public static final Uri RELOAD_URI = new Uri.Builder().scheme(SCHEME_CONTENT).authority(URI_BASE).appendPath(RELOAD_PATH).build();
    /**
     * Restaurants uri.
     */
    public static final Uri RESTAURANTS_URI = new Uri.Builder().scheme(SCHEME_CONTENT)
            .authority(URI_BASE)
            .appendPath(RESTAURANT_PATH).build();
    /**
     * Meals uri.
     */
    public static final Uri MEALS_URI = new Uri.Builder().scheme(SCHEME_CONTENT)
            .authority(URI_BASE)
            .appendPath(MEAL_PATH).build();


    private JidelakDbHelper dbHelper;


    /**
     * Name of default column with row id.
     */
    public static final String ID_COLUMN_NAME = "ROWID _id";

    @Override
    public boolean onCreate() {
        sUriMatcher.addURI(URI_BASE, DAY_PATH, MATCHED_DAY);
        sUriMatcher.addURI(URI_BASE, RESTAURANT_PATH, MATCHED_RESTAURANT);
        sUriMatcher.addURI(URI_BASE, MEAL_PATH, MATCHED_MEAL);
        sUriMatcher.addURI(URI_BASE, AVAILABILITY_PATH, MATCHED_AVAILABILITY);
        sUriMatcher.addURI(URI_BASE, SOURCE_PATH, MATCHED_SOURCE);
        sUriMatcher.addURI(URI_BASE, RELOAD_PATH, MATCHED_RELOAD);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case MATCHED_DAY:
                cursor = loadDayFromDb(projection, selection, selectionArgs, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), RELOAD_URI);
                return cursor;

            case MATCHED_RESTAURANT:
                cursor = loadRestaurantsFromDb(projection, selection, selectionArgs, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), RESTAURANTS_URI);
                return cursor;

            case MATCHED_MEAL:
                cursor = loadMealsFromDb(projection, selection, selectionArgs, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), MEALS_URI);
                return cursor;

            default:

        }
        return null;
    }

    private Cursor loadDayFromDb(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (projection == null) {
            return null;
        }
        List<String> proj = Arrays.asList(projection);
        if (!proj.contains(ID_COLUMN_NAME)) {
            proj = new ArrayList<>(Arrays.asList(projection));
            proj.add(0, ID_COLUMN_NAME);
            projection = proj.toArray(new String[proj.size()]);
        }
        SQLiteDatabase readableDatabase = getDbHelper().getReadableDatabase();
        return readableDatabase.query(
                String.format("%s m, %s a, %s r", MealDao.getTable().getName(), AvailabilityDao.getTable().getName(),
                        RestaurantDao.getTable().getName()), projection,
                String.format("m.%s = r.%s ", MealDao.RESTAURANT, RestaurantDao.ID)
                        + " and "
                        + String.format("m.%s = a.%s ", MealDao.AVAILABILITY, AvailabilityDao.ID)
                        + (selection == null || selection.isEmpty() ? "" : " and " + selection), selectionArgs,
                null, null, sortOrder);
    }

    private Cursor loadRestaurantsFromDb(String[] projection, String selection, String[] selectionArgs, String
            sortOrder) {
        if (projection == null) {
            return null;
        }
        List<String> proj = Arrays.asList(projection);
        if (!proj.contains(ID_COLUMN_NAME)) {
            proj = new ArrayList<>(Arrays.asList(projection));
            proj.add(0, ID_COLUMN_NAME);
            projection = proj.toArray(new String[proj.size()]);
        }
        SQLiteDatabase readableDatabase = getDbHelper().getReadableDatabase();
        return readableDatabase.query(
                RestaurantDao.getTable().getName(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor loadMealsFromDb(String[] projection, String selection, String[] selectionArgs, String
            sortOrder) {
        if (projection == null) {
            return null;
        }
        List<String> proj = Arrays.asList(projection);
        if (!proj.contains(ID_COLUMN_NAME)) {
            proj = new ArrayList<>(Arrays.asList(projection));
            proj.add(0, ID_COLUMN_NAME);
            projection = proj.toArray(new String[proj.size()]);
        }
        SQLiteDatabase readableDatabase = getDbHelper().getReadableDatabase();
        return readableDatabase.query(
                String.format("%s m, %s a", MealDao.getTable().getName(), AvailabilityDao.getTable().getName()),
                projection, String.format("m.%s = a.%s", MealDao.AVAILABILITY.getName(), AvailabilityDao.ID.getName())
                        + (selection == null || selection.isEmpty() ? "" : " and " + selection), selectionArgs, null,
                null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase wd = getDbHelper().getWritableDatabase();
        wd.beginTransaction();

        int cnt = 0;
        switch (sUriMatcher.match(uri)) {
            case MATCHED_RESTAURANT:
                wd.delete(MealDao.TABLE_NAME, String.format("%s = ?", MealDao.RESTAURANT.getName()), selectionArgs);
                wd.delete(AvailabilityDao.TABLE_NAME, String.format("%s = ?", AvailabilityDao.RESTAURANT.getName()),
                        selectionArgs);
                wd.delete(SourceDao.TABLE_NAME, String.format("%s = ?", SourceDao.RESTAURANT.getName()), selectionArgs);
                cnt = wd.delete(RestaurantDao.TABLE_NAME, String.format("%s = ?", RestaurantDao.ID.getName()),
                        selectionArgs);
                wd.setTransactionSuccessful();
                break;

            case MATCHED_MEAL:
                Cursor r = wd.query(MealDao.TABLE_NAME, new String[]{MealDao.AVAILABILITY.getName()},
                        String.format("%s = ?", MealDao.ID.getName()), selectionArgs, null, null, null);
                r.moveToFirst();
                long aId = r.getLong(0);
                cnt = wd.delete(MealDao.TABLE_NAME, String.format("%s = ?", MealDao.ID.getName()), selectionArgs);
                wd.delete(AvailabilityDao.TABLE_NAME, String.format("%s = ?", AvailabilityDao.ID.getName()),
                        new String[]{String.format("%d", aId)});
                wd.setTransactionSuccessful();
                break;
        }
        wd.endTransaction();

        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case MATCHED_RELOAD:
                try {
                    updateData();
                    getContext().getContentResolver().notifyChange(MEALS_URI, null);
                    getContext().getContentResolver().notifyChange(RESTAURANTS_URI, null);
                } catch (JidelakException e) {
                    log.error(e.getMessage(), e);
                }
                break;
        }


        return 0;
    }

    void updateData() throws JidelakException {

        SQLiteDatabase db = getDbHelper().getWritableDatabase();

        SourceDao sdao = new SourceDao(db);
        MealDao mdao = new MealDao(db);
        RestaurantDao rdao = new RestaurantDao(db);
        AvailabilityDao adao = new AvailabilityDao(db);

        RestaurantMarshaller rm = new RestaurantMarshaller();
        Restaurant restaurant = new Restaurant();
        boolean notFullyUpdated = false;
        for (Source source : sdao.findAll()) {
            try {

                try {
                    restaurant = source.getRestaurant();

                    InputStream template = getContext().openFileInput(restaurant.getTemplateName());

                    Node result = Utils.retrieve(source, template);

                    rm.unmarshall("#document.jidelak.config", result, restaurant);

                    Set<Availability> avs = new HashSet<Availability>();
                    for (Meal meal : restaurant.getMenu()) {
                        avs.add(meal.getAvailability());
                    }

                    for (Availability av : avs) {
                        SortedSet<Meal> atd = mdao.findByDayAndRestaurant(av.getCalendar(), restaurant);
                        mdao.delete(atd);
                    }

                    for (Meal meal : restaurant.getMenu()) {
                        adao.insert(meal.getAvailability());
                        mdao.insert(meal);
                    }

                    if (!(restaurant.getOpeningHours() == null || restaurant
                            .getOpeningHours().isEmpty())) {
                        Restaurant savedRestaurant = rdao.findById(restaurant);
                        if (savedRestaurant != null
                                && savedRestaurant.getOpeningHours() != null)
                            adao.delete(savedRestaurant.getOpeningHours());
                        adao.insert(restaurant.getOpeningHours());
                    }
                    rdao.update(restaurant, false);

                } catch (IOException e) {
                    throw new JidelakException(getContext().getResources().getString(
                            R.string.feeder_io_exception), e).setSource(source)
                            .setRestaurant(rdao.findById(restaurant))
                            .setHandled(true).setErrorType(ErrorType.NETWORK);
                } catch (TransformerException e) {
                    throw new JidelakTransformerException(getContext().getResources()
                            .getString(R.string.transformer_exception), rdao
                            .findById(restaurant).getTemplateName(), source
                            .getUrl().toString(), e).setSource(source)
                            .setRestaurant(rdao.findById(restaurant))
                            .setHandled(true).setErrorType(ErrorType.PARSING);
                } catch (ParserConfigurationException e) {
                    throw new JidelakException(getContext().getResources().getString(
                            R.string.parser_configuration_exception), e)
                            .setSource(source)
                            .setRestaurant(rdao.findById(restaurant))
                            .setHandled(true).setErrorType(ErrorType.PARSING);
                } catch (JidelakException e) {
                    throw e.setSource(source).setRestaurant(
                            rdao.findById(restaurant));
                }

            } catch (JidelakException e) {
                log.error(e.getMessage(), e);
                notFullyUpdated = true;
                // NotificationUtils.makeNotification(getApplicationContext(),
                // e);
            }
        }
        SharedPreferences prefs = getContext().getSharedPreferences(DEFAULT_PREFERENCES, Context.MODE_PRIVATE);
        if (notFullyUpdated) {
            prefs.edit().putLong(LAST_UPDATED_KEY, System.currentTimeMillis()).apply();
        }

        long delay = prefs.getLong(DELETE_DELAY_KEY, DEFAULT_DELETE_DELAY);
        if (delay >= 0) {
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.setTimeInMillis(System.currentTimeMillis() - delay);
            mdao.deleteOlder(cal);
        }
    }

    private JidelakDbHelper getDbHelper() {
        if (dbHelper == null)
            dbHelper = JidelakDbHelper.getInstance(getContext());
        return dbHelper;
    }
}
