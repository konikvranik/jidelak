package net.suteren.android.jidelak.test;

import android.test.ActivityInstrumentationTestCase2;
import net.suteren.android.jidelak.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<JidelakMainActivity> {

    public HelloAndroidActivityTest() {
        super(JidelakMainActivity.class); 
    }

    public void testActivity() {
        JidelakMainActivity activity = getActivity();
        assertNotNull(activity);
    }
}

