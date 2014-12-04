package net.suteren.android.jidelak.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.suteren.android.jidelak.AndroidUtils;
import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.model.Restaurant;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
public class RestaurantManagerActivity extends AbstractJidelakActivity {

	private class DragNDropRestaurantListAdapter extends BaseAdapter implements
			DragNDropAdapter {

		private List<Restaurant> restaurants;

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
		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {

			if (paramView == null) {
				paramView = View.inflate(getApplicationContext(),
						R.layout.draggable_restaurant, null);
			}

			Restaurant restaurant = getItem(paramInt);

			TextView nameView = (TextView) paramView.findViewById(R.id.name);
			nameView.setText(restaurant.getName());

			TextView openingView = (TextView) paramView.findViewById(R.id.open);
			openingView.setText(AndroidUtils.openingHoursToString(
					getApplicationContext(), restaurant));

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

		}

		@Override
		public int getDragHandler() {
			return R.id.handler;
		}

		public List<Restaurant> getRestaurants() {
			return restaurants;
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

		Button cancel = (Button) getWindow().findViewById(R.id.cancel);
		cancel.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		DragNDropListView ddlv = (DragNDropListView) getWindow().findViewById(
				R.id.restaurants);

		final DragNDropRestaurantListAdapter ddsa = new DragNDropRestaurantListAdapter(
				new RestaurantDao(JidelakDbHelper.getInstance(this)).findAll());
		ddlv.setDragNDropAdapter(ddsa);

		Button save = (Button) getWindow().findViewById(R.id.save);
		save.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				JidelakDbHelper dbHelper = JidelakDbHelper
						.getInstance(getApplication());
				new RestaurantDao(dbHelper).update(ddsa.getRestaurants());
				dbHelper.notifyDataSetChanged();
				finish();
			}
		});
	}

}
