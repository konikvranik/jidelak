package net.suteren.android.jidelak;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class JidelakFeederService extends Service {
	static final String LOGGING_TAG = "MyDemo";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(LOGGING_TAG, "JidelakFeederService.onStartCommand()");
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(LOGGING_TAG, "JidelakFeederService.onCreate()");

		registerReceiver(new JidelakFeederReceiver(), new IntentFilter(
				Intent.ACTION_TIME_TICK));
	}
}