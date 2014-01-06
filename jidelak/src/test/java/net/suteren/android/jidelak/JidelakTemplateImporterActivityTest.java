package net.suteren.android.jidelak;

import net.suteren.android.jidelak.JidelakTemplateImporterActivity;
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

		// fail("Not yet implemented");

	}
}
