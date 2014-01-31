/**
 * 
 */
package net.suteren.android.jidelak.ui;

import static net.suteren.android.jidelak.Constants.DEFAULT_PREFERENCES;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.R.xml;
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

		getPreferenceManager().setSharedPreferencesName(DEFAULT_PREFERENCES);
		addPreferencesFromResource(R.xml.main_prefs);

	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		return false;
	}

}
