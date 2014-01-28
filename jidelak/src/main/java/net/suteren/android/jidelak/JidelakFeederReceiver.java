/**
 *
 */
package net.suteren.android.jidelak;

import static net.suteren.android.jidelak.Constants.DEFAULT_PREFERENCES;
import static net.suteren.android.jidelak.Constants.DEFAULT_UPDATE_INTERVAL;
import static net.suteren.android.jidelak.Constants.LAST_UPDATED_KEY;
import static net.suteren.android.jidelak.Constants.UPDATE_INTERVAL_KEY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * @author Petr
 */
public class JidelakFeederReceiver extends BroadcastReceiver {

	private static Logger log = LoggerFactory
			.getLogger(JidelakFeederReceiver.class);

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().compareTo(Intent.ACTION_BOOT_COMPLETED) == 0) {
			log.debug("DemoReceiver.onReceive(ACTION_BOOT_COMPLETED)");
			context.startService(new Intent(context, JidelakFeederService.class));
		} else if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
			log.debug("DemoReceiver.onReceive(ACTION_TIME_TICK)");

			if (decideIfStart(context))
				context.startService(new Intent(context,
						JidelakFeederService.class));
		} else
			log.debug("DemoReceiver.onReceive(" + intent.getAction() + ")");
	}

	private boolean decideIfStart(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(
				DEFAULT_PREFERENCES, Context.MODE_PRIVATE);
		long schedule = prefs.getLong(LAST_UPDATED_KEY, -1);
		long time = System.currentTimeMillis();

		try {
			if (schedule != -1)
				schedule += prefs.getLong(UPDATE_INTERVAL_KEY,
						DEFAULT_UPDATE_INTERVAL);
		} catch (ClassCastException e) {
			prefs.edit().putLong(UPDATE_INTERVAL_KEY, DEFAULT_UPDATE_INTERVAL);
		}

		if (time > schedule) {
			prefs.edit().putLong(LAST_UPDATED_KEY, time);
			return true;
		} else
			return false;
	}

}
