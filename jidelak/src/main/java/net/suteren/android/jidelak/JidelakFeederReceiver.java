/**
 *
 */
package net.suteren.android.jidelak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author Petr
 */
public class JidelakFeederReceiver extends BroadcastReceiver {

	static final String LOGGING_TAG = "JidelakFeederReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().compareTo(Intent.ACTION_BOOT_COMPLETED) == 0) {
			Log.d(LOGGING_TAG, "DemoReceiver.onReceive(ACTION_BOOT_COMPLETED)");
			context.startService(new Intent(context, JidelakFeederService.class));
		} else if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
			Log.d(LOGGING_TAG, "DemoReceiver.onReceive(ACTION_TIME_TICK)");

			if (decideIfStart(context))
				context.startService(new Intent(context,
						JidelakFeederService.class));
		} else
			Log.d(LOGGING_TAG, "DemoReceiver.onReceive(" + intent.getAction()
					+ ")");
	}

	private boolean decideIfStart(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(null,
				Context.MODE_PRIVATE);
		long schedule = prefs.getLong("last_updated", -1);
		long time = System.currentTimeMillis();

		if (schedule != -1)
			schedule += prefs.getLong("update_interval", 3600000);

		if (time > schedule) {
			prefs.edit().putLong("last_updated", time);
			return true;
		} else
			return false;
	}

}
