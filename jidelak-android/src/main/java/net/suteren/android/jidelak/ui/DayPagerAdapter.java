package net.suteren.android.jidelak.ui;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.dao.AvailabilityDao;

import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by vranikp on 18.5.16.
 *
 * @author vranikp
 */
public class DayPagerAdapter extends FragmentPagerAdapter implements SpinnerAdapter {

    private MainActivity mainActivity;
    private Cursor mCursor;

    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected ChangeObserver mChangeObserver;
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected DataSetObserver mDataSetObserver;
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected boolean mDataValid;
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected Context mContext;
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected int mRowIDColumn;

    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public DayPagerAdapter(MainActivity mainActivity, FragmentManager fm, Cursor c) {
        super(fm);
        this.mainActivity = mainActivity;
        init(mainActivity, c);
    }

    void init(Context context, Cursor c) {
        boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;
        mContext = context;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
        mChangeObserver = new ChangeObserver();
        mDataSetObserver = new MyDataSetObserver();

        if (cursorPresent) {
            if (mChangeObserver != null) c.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) c.registerDataSetObserver(mDataSetObserver);
        }
    }


    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (mCursor == null) {
            return convertView;
        } else if (convertView == null) {
            convertView = View.inflate(mainActivity.getApplicationContext(), R.layout.spinner_list, null);
        }

        String value = DateFormat.getDateInstance(DateFormat.FULL,
                Locale.getDefault()).format(getItemId(position));
        ((TextView) convertView.findViewById(R.id.value)).setText(value);

        int pd = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, mainActivity.getResources()
                        .getDisplayMetrics());
        convertView.setPadding(pd, pd, pd, pd);

        return convertView;
    }

    @Override
    public Fragment getItem(int position) {
        if (mCursor==null||!mCursor.move(position)) {
            return null;
        }
        Fragment fragment = new DayFragment();
        Bundle args = new Bundle();
        if (!isEmpty())
            args.putLong(DayFragment.ARG_DAY, getItemId(position));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor==null||!mCursor.move(position)) {
            return -1;
        }
        return getTimeInMillis(mCursor);
    }

    private long getTimeInMillis(Cursor cursor) {
        return new GregorianCalendar(cursor.getInt(cursor.getColumnIndex(AvailabilityDao.YEAR.getName())),
                cursor.getInt(cursor.getColumnIndex(AvailabilityDao.MONTH.getName())),
                cursor.getInt(cursor.getColumnIndex(AvailabilityDao.DAY.getName()))).getTimeInMillis();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault()).format(getItemId(position));

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mainActivity.getApplicationContext(),
                    R.layout.spinner_view, null);
        }

        String value = " - ";
        if (!isEmpty())
            value = DateFormat.getDateInstance(DateFormat.SHORT,
                    Locale.getDefault()).format(getItemId(position));
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
        return mCursor == null || mCursor.getCount() == 0;
    }

    /**
     * Notifies the attached observers that the underlying data is no longer valid
     * or available. Once invoked this adapter is no longer valid and should
     * not report further data set changes.
     */
    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasa not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver);
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyDataSetInvalidated();
        }
        return oldCursor;
    }

    /**
     * Called when the {@link ContentObserver} on the cursor receives a change notification.
     * The default implementation provides the auto-requery logic, but may be overridden by
     * sub classes.
     *
     * @see ContentObserver#onChange(boolean)
     */
    protected void onContentChanged() {
        if (mCursor != null && !mCursor.isClosed()) {
            if (false) Log.v("Cursor", "Auto requerying " + mCursor + " due to update");
            mDataValid = mCursor.requery();
        }
    }

    public int getPositionByDate(long cal) {
        if(mCursor==null){
            return 0;
        }
        mCursor.moveToFirst();
        while (getTimeInMillis(mCursor) < cal && mCursor.moveToNext()) ;
        return mCursor.getPosition();
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            notifyDataSetInvalidated();
        }
    }
}
