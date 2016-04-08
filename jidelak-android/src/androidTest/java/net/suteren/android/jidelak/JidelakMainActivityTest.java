package net.suteren.android.jidelak;

import android.test.ActivityInstrumentationTestCase2;
import net.suteren.android.jidelak.ui.MainActivity;

public class JidelakMainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public JidelakMainActivityTest() {
		super(MainActivity.class);
	}

	public void testActivity() {
		MainActivity activity = getActivity();
		assertNotNull(activity);
	}

}
