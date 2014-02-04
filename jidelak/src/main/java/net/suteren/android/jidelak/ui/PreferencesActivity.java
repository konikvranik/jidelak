/**
 * 
 */
package net.suteren.android.jidelak.ui;

import static net.suteren.android.jidelak.Constants.DEFAULT_PREFERENCES;
import net.suteren.android.jidelak.R;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

/**
 * @author jd39426
 * 
 */
public class PreferencesActivity extends PreferenceActivity implements
		OnPreferenceClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			setupActionBar();

		getPreferenceManager().setSharedPreferencesName(DEFAULT_PREFERENCES);
		addPreferencesFromResource(R.xml.main_prefs);

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private ActionBar setupActionBar() {
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		return ab;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		return false;
	}

}
