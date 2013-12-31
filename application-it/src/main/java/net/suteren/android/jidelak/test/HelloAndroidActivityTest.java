package net.suteren.android.jidelak.test;

import net.suteren.android.jidelak.HelloAndroidActivity;
import android.test.ActivityInstrumentationTestCase2;
import net.suteren.android.jidelak.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

    public HelloAndroidActivityTest() {
        super(HelloAndroidActivity.class);
    }

    public void testActivity() {
        HelloAndroidActivity activity = getActivity();
        assertNotNull(activity);
    }
}

