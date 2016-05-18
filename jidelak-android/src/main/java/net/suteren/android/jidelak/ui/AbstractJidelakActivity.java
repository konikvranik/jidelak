package net.suteren.android.jidelak.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewConfiguration;
import net.suteren.android.jidelak.JidelakDbHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class AbstractJidelakActivity extends ActionBarActivity {
	protected ActionBar actionBar;
	protected static Logger log = LoggerFactory
			.getLogger(AbstractJidelakActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
		forceThreeDots();
	}

	protected ActionBar setupActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		return actionBar;
	}

	protected void forceThreeDots() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected SharedPreferences getSharedPreferences() {
		return getSharedPreferences("default", Context.MODE_PRIVATE);
	}
}
