package net.suteren.android.jidelak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.model.Restaurant;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.terlici.dragndroplist.DragNDropAdapter;
import com.terlici.dragndroplist.DragNDropListView;

/**
 * 
 */

/**
 * @author Petr
 * 
 */
public class RestaurantManagerActivity extends Activity {

	private class DragNDropRestaurantListAdapter extends BaseAdapter implements
			DragNDropAdapter {

		int mPosition[];
		private List<Restaurant> restaurants;

		public DragNDropRestaurantListAdapter(Collection<Restaurant> restaurants) {
			super();
			this.restaurants = new ArrayList<Restaurant>(restaurants);
			setup(getCount());
		}

		private void setup(int size) {
			mPosition = new int[size];

			for (int i = 0; i < size; ++i)
				mPosition[i] = i;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			setup(getCount());
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
		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {

			if (paramView == null) {
				paramView = View.inflate(getApplicationContext(),
						R.layout.restaurant, null);
			}

			ImageButton ib = (ImageButton) paramView
					.findViewById(R.id.btn_menu);

			ib.setVisibility(View.INVISIBLE);

			Restaurant restaurant = getItem(paramInt);

			TextView nameView = (TextView) paramView.findViewById(R.id.name);
			nameView.setText(restaurant.getName());

			TextView openingView = (TextView) paramView.findViewById(R.id.open);
			openingView.setText(restaurant.openingHoursToString());

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
			int position = mPosition[startPosition];

			if (startPosition < endPosition)
				for (int i = startPosition; i < endPosition; ++i)
					mPosition[i] = mPosition[i + 1];
			else if (endPosition < startPosition)
				for (int i = startPosition; i > endPosition; --i)
					mPosition[i] = mPosition[i - 1];

			mPosition[endPosition] = position;
		}

		@Override
		public int getDragHandler() {
			return R.id.header;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.restaurants_manager);

		DragNDropListView ddlv = (DragNDropListView) getWindow().findViewById(
				R.id.restaurants);

		DragNDropAdapter ddsa = (DragNDropAdapter) new DragNDropRestaurantListAdapter(
				new RestaurantDao(new JidelakDbHelper(this)).findAll());
		ddlv.setDragNDropAdapter(ddsa);

		// TODO Auto-generated method stub

	}

}
