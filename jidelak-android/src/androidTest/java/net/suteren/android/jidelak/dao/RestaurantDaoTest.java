package net.suteren.android.jidelak.dao;

import android.content.ContentValues;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.ui.MainActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;

public class RestaurantDaoTest extends
        ActivityInstrumentationTestCase2<MainActivity> {

    private RestaurantDao dao;
    private boolean emptyDb;
    private static Logger log = LoggerFactory.getLogger(RestaurantDaoTest.class);

    public RestaurantDaoTest() {
        super(MainActivity.class);

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

        log.info("begin testGetValuesRestaurant");

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Pokusný restaurant 1");
        ContentValues values = dao.getValues(restaurant, true);
        assertEquals("Pokusný restaurant 1", values.get("name"));
        log.info("end testGetValuesRestaurant");

    }

    public void testInsert() {
        log.info("begin testInsert");

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

        log.info("end testInsert");

    }

    public void testTableCreateClausule() {
        log.info("begin testTableCreateClausule");
        assertEquals(
                "create table restaurant(id integer primary key,city text,address text,longitude real,latitude real," +
                        "zip integer,country text,phone text,web text,email text,name text)",
                RestaurantDao.getTable().createClausule());
        log.info("end testTableCreateClausule");
    }

    public void testUpdate() {
        log.info("begin testUpdate");
        Restaurant restaurant = new Restaurant();
        restaurant.setId(Long.valueOf(1));
        restaurant = dao.findById(restaurant);

        restaurant.setName("Restaurant test update");

        dao.update(restaurant);

        assertEquals(Long.valueOf(1), restaurant.getId());

        restaurant = dao.findById(restaurant);

        assertNotNull(restaurant);
        assertEquals("Restaurant test update", restaurant.getName());
        log.info("end testUpdate");
    }

    public void testDelete() {
        log.info("begin testDelete");
        Restaurant restaurant = new Restaurant();
        restaurant.setId(Long.valueOf(1));
        dao.delete(restaurant);

        assertNull(dao.findById(restaurant));
        log.info("end testDelete");
    }

    public void testFindAll() {
        log.info("begin testFindAll");
        SortedSet<Restaurant> restaurants = dao.findAll();

        assertFalse(restaurants.isEmpty());
        log.info("end testFindAll");
    }

    public void testFindByIdT() {
        log.info("begin testFindByIdT");
        Restaurant restaurant = new Restaurant();
        restaurant.setId((long) 1);
        restaurant = dao.findById(restaurant);

        assertNotNull(restaurant);
        assertNotNull(restaurant.getName());
        log.info("end testFindByIdT");
    }

    public void testFindByIdLong() {
        log.info("begin testFindByIdLong");

        Restaurant restaurant = dao.findById(2);

        assertNotNull(restaurant);
        log.info("end testFindByIdLong");
    }

}
