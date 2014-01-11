package net.suteren.android.jidelak;

import java.util.Calendar;
import java.util.List;

import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class JidelakTemplateImporterActivityTest extends
		ActivityInstrumentationTestCase2<JidelakTemplateImporterActivity> {

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
}
