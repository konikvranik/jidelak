package net.suteren.android.jidelak.dao;

import java.util.List;

import net.suteren.android.jidelak.JidelakMainActivity;
import net.suteren.android.jidelak.model.Restaurant;
import android.content.ContentValues;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class RestaurantDaoTest extends
		ActivityInstrumentationTestCase2<JidelakMainActivity> {

	private RestaurantDao dao;

	public RestaurantDaoTest() {
		super(JidelakMainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		dao = new RestaurantDao(getActivity().getDbHelper());
	}

	public void testGetValuesRestaurant() {

		Restaurant restaurant = new Restaurant();
		restaurant.setName("Pokusný restaurant 1");
		ContentValues values = dao.getValues(restaurant);
		assertEquals("Pokusný restaurant 1", values.get("name"));

	}

	public void testInsert() {

		Restaurant restaurant = new Restaurant();
		restaurant.setName("Pokusný restaurant 1");
		restaurant.setId(Long.valueOf(1));

		dao.insert(restaurant);

		assertNotNull(restaurant.getId());

		Log.d("test", "Restaurant id: " + restaurant.getId());

	}

	public void testUpdate() {

		Restaurant restaurant = new Restaurant();
		restaurant.setId(Long.valueOf(1));
		restaurant = dao.findById(restaurant);

		restaurant.setName("Restaurant test update");

		dao.update(restaurant);

		assertEquals(Long.valueOf(1), restaurant.getId());

		restaurant = dao.findById(restaurant);

		assertEquals("Restaurant test update", restaurant.getName());

	}

	public void testDelete() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId(Long.valueOf(1));
		dao.delete(restaurant);
	}

	public void testFindAll() {

		List<Restaurant> restaurants = dao.findAll();

		assertFalse(restaurants.isEmpty());

	}

	public void testFindByIdT() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId((long) 2);
		restaurant = dao.findById(restaurant);

		assertNotNull(restaurant.getName());
	}

	public void testFindByIdLong() {

		Restaurant restaurant = dao.findById(2);

		assertNotNull(restaurant);

	}

}
