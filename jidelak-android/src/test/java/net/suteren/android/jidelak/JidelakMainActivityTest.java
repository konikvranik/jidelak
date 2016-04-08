package net.suteren.android.jidelak;

import android.test.ActivityInstrumentationTestCase2;
import net.suteren.android.jidelak.*;

public class JidelakMainActivityTest extends
		ActivityInstrumentationTestCase2<JidelakMainActivity> {

	public JidelakMainActivityTest() {
		super(JidelakMainActivity.class);
	}

	public void testActivity() {
		JidelakMainActivity activity = getActivity();
		assertNotNull(activity);
	}

}
