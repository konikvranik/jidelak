package net.suteren.android.jidelak;

public class Constants {

	public static final String DEFAULT_PREFERENCES = "default";

	public static final String LAST_UPDATED_KEY = "last_updated";

	public static final String CATEGORY_BACKGROUND_KEY = "category_background";
	public static final String UPDATE_INTERVAL_KEY = "update_interval";
	public static final String UPDATE_INTERVAL_STRING_KEY = "update_interval_string";

	public static final long SECOND_IN_MILLIS = 1000;
	public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
	public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
	public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
	public static final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;

	public static final long DEFAULT_UPDATE_INTERVAL = HOUR_IN_MILLIS;

}
