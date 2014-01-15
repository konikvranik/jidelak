package net.suteren.android.jidelak;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.Calendar;
import java.util.List;

import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;

import org.junit.Test;

import android.content.Intent;
import android.net.Uri;
import android.provider.OpenableColumns;
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

	@Test
	public void testParser() throws Exception {
		JidelakTemplateImporterActivity activity = getActivity();

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
				JidelakTemplateImporterActivity.class);
		intent.setData(Uri.fromFile(new File("/sdcard/lg_ave.jidelak.xsl")));
		intent.putExtra("force", true);
		getActivity().startActivity(intent);

		Log.d("Test", "activity: " + activity);
		Restaurant restaurant = new Restaurant();
		// activity.parseConfig(
		// this.getClass().getResourceAsStream("/lg_ave.jidelak.xsl"),
		// restaurant);

		RestaurantDao rdao = new RestaurantDao(new JidelakDbHelper(
				getActivity()));

		restaurant.setId((long) 1);
		rdao.findById(restaurant);

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
			Log.d(LOG_TAG, "Availability count: "
					+ new AvailabilityDao(dbh).findAll().size());
			Log.d(LOG_TAG, "Meal count: " + new MealDao(dbh).findAll().size());
			Log.d(LOG_TAG, "Source count: "
					+ new SourceDao(dbh).findAll().size());
		}

	}
}
