package net.suteren.android.jidelak;

import java.util.Calendar;
import java.util.List;

import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class JidelakTemplateImporterActivityTest extends
		ActivityInstrumentationTestCase2<JidelakTemplateImporterActivity> {

	private static final String LOG_TAG = "JidelakTemplateImporterActivityTest";

	public JidelakTemplateImporterActivityTest() {
		super(JidelakTemplateImporterActivity.class);
	}

	public void testActivity() {
		JidelakTemplateImporterActivity activity = getActivity();
		assertNotNull(activity);
	}

	public void testParser() throws Exception {
		JidelakTemplateImporterActivity activity = getActivity();
		Log.d("Test", "activity: " + activity);
		Restaurant restaurant = new Restaurant();
		activity.parseConfig(
				this.getClass().getResourceAsStream("/lg_ave.jidelak.xsl"),
				restaurant);

		assertEquals("Lunch Garden Avenir", restaurant.getName());
		assertEquals("cp1250", restaurant.getSource().get(0).getEncoding());

		List<Availability> oh = restaurant.getOpeningHours();
		assertEquals(6, oh.size());

		Availability av = oh.get(0);
		assertEquals(Integer.valueOf(Calendar.MONDAY), av.getDow());
		assertEquals("8:00", av.getFrom());
		assertEquals("17:00", av.getTo());

		av = oh.get(1);
		assertEquals(Integer.valueOf(Calendar.TUESDAY), av.getDow());
		assertEquals("8:00", av.getFrom());
		assertEquals("17:00", av.getTo());

		av = oh.get(5);
		assertEquals(Integer.valueOf(1), av.getDay());
		assertEquals(Integer.valueOf(1), av.getMonth());
		assertEquals(Integer.valueOf(2010), av.getYear());
		assertEquals(Boolean.valueOf(true), av.getClosed());

	}

	public void testResults() {
		JidelakTemplateImporterActivity activity = getActivity();
		JidelakDbHelper dbh = new JidelakDbHelper(activity);

		RestaurantDao rdao = new RestaurantDao(dbh);

		List<Restaurant> rests = rdao.findAll();

		assertTrue(rests.size() > 0);

		for (Restaurant rest : rests) {

			Log.d(LOG_TAG, "Name: " + rest.getName());
			Log.d(LOG_TAG, "TemplateName: " + rest.getTemplateName());

			List<Source> srcs = rest.getSource();
			if (srcs != null)
				for (Source src : srcs) {
					Log.d(LOG_TAG, "\t:src: " + src.getUrl().toString());
				}

			List<Availability> oh = rest.getOpeningHours();
			if (oh != null)
				for (Availability av : oh) {
					Log.d(LOG_TAG, "\t av: " + av.getFrom());
				}

			Log.d(LOG_TAG, "Restauranrs count: " + rdao.findAll().size());
			Log.d(LOG_TAG, "Availability count: " + new AvailabilityDao(dbh).findAll().size());
			Log.d(LOG_TAG, "Meal count: " + new MealDao(dbh).findAll().size());
			Log.d(LOG_TAG, "Source count: " + new SourceDao(dbh).findAll().size());
			}

	}
}
