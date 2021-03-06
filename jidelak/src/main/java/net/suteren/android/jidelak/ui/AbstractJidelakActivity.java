package net.suteren.android.jidelak.ui;

import java.lang.reflect.Field;

import net.suteren.android.jidelak.JidelakDbHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewConfiguration;

public class AbstractJidelakActivity extends ActionBarActivity {
	protected ActionBar actionBar;
	protected static Logger log = LoggerFactory
			.getLogger(AbstractJidelakActivity.class);

	private JidelakDbHelper dbHelper;

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

	public JidelakDbHelper getDbHelper() {
		if (dbHelper == null)
			dbHelper = JidelakDbHelper.getInstance(getApplicationContext());
		return dbHelper;
	}

	protected SharedPreferences getSharedPreferences() {
		return getSharedPreferences("default", Context.MODE_PRIVATE);
	}
}
