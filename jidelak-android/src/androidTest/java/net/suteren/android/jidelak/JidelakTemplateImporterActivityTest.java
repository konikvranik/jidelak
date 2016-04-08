package net.suteren.android.jidelak;

import android.content.Intent;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;
import net.suteren.android.jidelak.ui.TemplateImporterActivity;
import org.junit.Test;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

public class JidelakTemplateImporterActivityTest extends
		ActivityInstrumentationTestCase2<TemplateImporterActivity> {

	private static final String LOG_TAG = "JTIAT";

	public JidelakTemplateImporterActivityTest() {
		super(TemplateImporterActivity.class);
	}

	public void testActivity() {
		TemplateImporterActivity activity = getActivity();
		assertNotNull(activity);
	}

	@Test
	public void testParser() throws Exception {
		TemplateImporterActivity activity = getActivity();

		// InputStream r = this.getClass().getResourceAsStream(
		// "/lg_ave.jidelak.xsl");
		//
		// CharBuffer cb = CharBuffer.allocate(64);
		//
		// BufferedReader br = new BufferedReader(new InputStreamReader(r));
		//
		// BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
		// getActivity().openFileOutput("test.xsl",
		// getActivity().MODE_WORLD_READABLE)));
		//
		// while (br.read(cb) != -1) {
		// bw.write(cb.array());
		// }
		// br.close();
		// bw.close();

		Intent intent = new Intent(getActivity(),
				TemplateImporterActivity.class);
		intent.setData(Uri.fromFile(new File("/sdcard/lg_ave.jidelak.xsl")));
		intent.putExtra("force", true);
		getActivity().startActivity(intent);

		Log.d("Test", "activity: " + activity);
		Restaurant restaurant = new Restaurant();
		// activity.parseConfig(
		// this.getClass().getResourceAsStream("/lg_ave.jidelak.xsl"),
		// restaurant);

		RestaurantDao rdao = new RestaurantDao(JidelakDbHelper.getInstance(
				getActivity()));

		restaurant.setId((long) 1);
		rdao.findById(restaurant);

		assertEquals("Lunch Garden Avenir", restaurant.getName());
		assertEquals("cp1250", restaurant.getSource().iterator().next().getEncoding());

		SortedSet<Availability> oh = restaurant.getOpeningHours();
		assertEquals(6, oh.size());

		Iterator<Availability> it = oh.iterator();
		Availability av = it.next();
		assertEquals(Integer.valueOf(Calendar.MONDAY), av.getDow());
		assertEquals("8:00", av.getFrom());
		assertEquals("17:00", av.getTo());

		av = it.next();
		assertEquals(Integer.valueOf(Calendar.TUESDAY), av.getDow());
		assertEquals("8:00", av.getFrom());
		assertEquals("17:00", av.getTo());

		av = it.next();
		av = it.next();
		av = it.next();
		assertEquals(Integer.valueOf(1), av.getDay());
		assertEquals(Integer.valueOf(1), av.getMonth());
		assertEquals(Integer.valueOf(2010), av.getYear());
		assertEquals(Boolean.valueOf(true), av.getClosed());

	}

	public void testResults() {
		TemplateImporterActivity activity = getActivity();
		JidelakDbHelper dbh = JidelakDbHelper.getInstance(activity);

		RestaurantDao rdao = new RestaurantDao(dbh);

		SortedSet<Restaurant> rests = rdao.findAll();

		assertTrue(rests.size() > 0);

		for (Restaurant rest : rests) {

			Log.d(LOG_TAG, "Name: " + rest.getName());
			Log.d(LOG_TAG, "TemplateName: " + rest.getTemplateName());

			Set<Source> srcs = rest.getSource();
			if (srcs != null)
				for (Source src : srcs) {
					Log.d(LOG_TAG, "\t:src: " + src.getUrl().toString());
				}

			Set<Availability> oh = rest.getOpeningHours();
			if (oh != null)
				for (Availability av : oh) {
					Log.d(LOG_TAG, "\t av: " + av.getFrom());
				}

			Log.d(LOG_TAG, "Restauranrs count: " + rdao.findAll().size());
			Log.d(LOG_TAG, "Availability count: "
					+ new AvailabilityDao(dbh).findAll().size());
			Log.d(LOG_TAG, "Meal count: " + new MealDao(dbh).findAll().size());
			Log.d(LOG_TAG, "Source count: "
					+ new SourceDao(dbh).findAll().size());
		}

	}
}
