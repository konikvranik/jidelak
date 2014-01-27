package net.suteren.android.jidelak;

import android.content.res.Resources;

public class Constants {

	public static final String DEFAULT_PREFERENCES = "default";

	public static final String LAST_UPDATED_KEY = Resources.getSystem()
			.getString(R.string.last_updated_key);

	public static final String CATEGORY_BACKGROUND_KEY = Resources.getSystem()
			.getString(R.string.category_background_key);

	public static final int DEFAULT_UPDATE_INTERVAL = Resources.getSystem()
			.getInteger(R.integer.default_update_interval);
	public static final String UPDATE_INTERVAL_KEY = Resources.getSystem()
			.getString(R.string.update_interval_key);

}
