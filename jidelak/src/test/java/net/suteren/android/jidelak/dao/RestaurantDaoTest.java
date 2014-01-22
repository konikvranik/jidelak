package net.suteren.android.jidelak.dao;

import java.util.SortedSet;

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.JidelakMainActivity;
import net.suteren.android.jidelak.model.Restaurant;
import android.content.ContentValues;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class RestaurantDaoTest extends
		ActivityInstrumentationTestCase2<JidelakMainActivity> {

	private RestaurantDao dao;
	private boolean emptyDb;

	public RestaurantDaoTest() {
		super(JidelakMainActivity.class);

		emptyDb = true;
	}

	@Override
	protected void setUp() throws Exception {
		if (emptyDb)
			getActivity().deleteDatabase(JidelakDbHelper.DATABASE_NAME);
		dao = new RestaurantDao(getActivity().getDbHelper());
	}

	@Override
	protected void tearDown() throws Exception {
		getActivity().deleteDatabase(JidelakDbHelper.DATABASE_NAME);
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
		if (emptyDb) {
			restaurant.setId(null);
			dao.insert(restaurant);
		}
		assertNotNull(restaurant.getId());

		Log.d("test", "Restaurant id: " + restaurant.getId());

	}

	public void testTableCreateClausule() {
		assertEquals(
				"create table restaurant(id integer primary key,city text,address text,longitude real,latitude real,zip integer,country text,phone text,web text,email text,name text)",
				RestaurantDao.getTable().createClausule());
	}

	public void testUpdate() {

		Restaurant restaurant = new Restaurant();
		restaurant.setId(Long.valueOf(1));
		restaurant = dao.findById(restaurant);

		restaurant.setName("Restaurant test update");

		dao.update(restaurant);

		assertEquals(Long.valueOf(1), restaurant.getId());

		restaurant = dao.findById(restaurant);

		assertNotNull(restaurant);
		assertEquals("Restaurant test update", restaurant.getName());

	}

	public void testDelete() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId(Long.valueOf(1));
		dao.delete(restaurant);

		assertNull(dao.findById(restaurant));
	}

	public void testFindAll() {

		SortedSet<Restaurant> restaurants = dao.findAll();

		assertFalse(restaurants.isEmpty());

	}

	public void testFindByIdT() {
		Restaurant restaurant = new Restaurant();
		restaurant.setId((long) 1);
		restaurant = dao.findById(restaurant);

		assertNotNull(restaurant);
		assertNotNull(restaurant.getName());
	}

	public void testFindByIdLong() {

		Restaurant restaurant = dao.findById(2);

		assertNotNull(restaurant);

	}

}
