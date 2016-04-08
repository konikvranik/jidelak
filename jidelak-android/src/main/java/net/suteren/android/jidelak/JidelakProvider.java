package net.suteren.android.jidelak;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by vranikp on 8.4.16.
 *
 * @author vranikp
 */
public class JidelakProvider extends ContentProvider {

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
    private static final String RELOAD_PATH = "reload";
    private static final int MATCHED_RELOAD = 3;
    private static final Uri ALL_DATA_URI = new Uri.Builder().scheme("content").authority(URI_BASE).build();

    @Override
    public boolean onCreate() {
        sUriMatcher.addURI(URI_BASE, DAY_PATH, MATCHED_DAY);
        sUriMatcher.addURI(URI_BASE, RESTAURANT_PATH, MATCHED_RESTAURANT);
        sUriMatcher.addURI(URI_BASE, RELOAD_PATH, MATCHED_RELOAD);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (sUriMatcher.match(uri)) {
            case MATCHED_DAY:
                break;

            case MATCHED_RESTAURANT:
                break;

            case MATCHED_RELOAD:

                getContext().getContentResolver().notifyChange(ALL_DATA_URI, null);
                break;

            default:

        }
        return null;
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
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
