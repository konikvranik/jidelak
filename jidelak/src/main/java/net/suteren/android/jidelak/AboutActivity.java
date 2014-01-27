/**
 * 
 */
package net.suteren.android.jidelak;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

/**
 * @author Petr
 * 
 */
public class AboutActivity extends ActionBarActivity {

	private static Logger log = LoggerFactory.getLogger(AboutActivity.class);
	private ActionBar ab;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		PackageInfo pInfo;
		String versionName = "undefined";
		int versionCode = -1;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = pInfo.versionName;
			versionCode = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			log.warn(e.getMessage(), e);
		}

		TextView versionView = (TextView) getWindow()
				.findViewById(R.id.version);
		versionView.setText(versionName);

		versionView = (TextView) getWindow().findViewById(R.id.versionCode);
		versionView.setText(String.format("%d", versionCode));

		SharedPreferences prefs = getSharedPreferences("default",
				Context.MODE_PRIVATE);
		Date lastUpdated = new Date(prefs.getLong(
				JidelakFeederService.LAST_UPDATED, 0));
		versionView = (TextView) getWindow().findViewById(R.id.last_updated);
		versionView.setText(String.format("%s %s",
				DateFormat
						.getDateInstance(DateFormat.LONG, Locale.getDefault())
						.format(lastUpdated),
				DateFormat
						.getTimeInstance(DateFormat.LONG, Locale.getDefault())
						.format(lastUpdated)));

		setupActionBar();

	}

	private void setupActionBar() {
		ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
	}
}