package net.suteren.android.jidelak;

import java.util.Date;

import android.app.Activity;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class JidelakMainActivity extends Activity {

	public static class DailyMenuAdapter implements ListAdapter {

		private final DataSetObservable mDataSetObservable = new DataSetObservable();

		public DailyMenuAdapter(Date day) {
			// TODO Auto-generated constructor stub
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		public void registerDataSetObserver(DataSetObserver observer) {
			mDataSetObservable.registerObserver(observer);
		}

		public void unregisterDataSetObserver(DataSetObserver observer) {
			mDataSetObservable.unregisterObserver(observer);
		}

		public void notifyDataSetChanged() {
			mDataSetObservable.notifyChanged();
		}

		public boolean areAllItemsEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isEnabled(int arg0) {
			// TODO Auto-generated method stub
			return false;
		}

	}

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static class DayPagerAdapter extends FragmentStatePagerAdapter {

		public DayPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			return getItem(getDateByPosition(position));
		}

		private Date getDateByPosition(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Fragment getItem(Date day) {
			Fragment fragment = new DayFragment();
			Bundle args = new Bundle();
			args.putLong(DayFragment.ARG_DAY, day.getTime());
			fragment.setArguments(args);
			return fragment;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	public static class DayFragment extends Fragment {
		public static final String ARG_DAY = "day";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			Bundle args = getArguments();

			View rootView = inflater.inflate(R.layout.day, container, false);
			ListView menuList = (ListView) rootView
					.findViewById(R.id.menu_list);
			Date day = null;
			menuList.setAdapter(new DailyMenuAdapter(day));
			return rootView;
		}
	}
}
