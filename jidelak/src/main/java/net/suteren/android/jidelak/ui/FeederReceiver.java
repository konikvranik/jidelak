/**
 *
 */
package net.suteren.android.jidelak.ui;

import static net.suteren.android.jidelak.Constants.DEFAULT_PREFERENCES;
import static net.suteren.android.jidelak.Constants.DEFAULT_UPDATE_INTERVAL;
import static net.suteren.android.jidelak.Constants.DEFAULT_WIFI_ONLY;
import static net.suteren.android.jidelak.Constants.LAST_UPDATED_KEY;
import static net.suteren.android.jidelak.Constants.UPDATE_INTERVAL_KEY;
import static net.suteren.android.jidelak.Constants.WIFI_ONLY_KEY;
import net.suteren.android.jidelak.NetworkUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * @author Petr
 */
public class FeederReceiver extends BroadcastReceiver {

	private static Logger log = LoggerFactory.getLogger(FeederReceiver.class);

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().compareTo(Intent.ACTION_BOOT_COMPLETED) == 0) {
			log.debug("DemoReceiver.onReceive(ACTION_BOOT_COMPLETED)");
			context.startService(new Intent(context, FeederService.class)
					.putExtra("register", true));
		} else if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
			log.debug("DemoReceiver.onReceive(ACTION_TIME_TICK)");

			if (decideIfStart(context)) {
				log.debug("Receiver is starting service...");
				context.startService(new Intent(context, FeederService.class));
			}
		} else
			log.debug("DemoReceiver.onReceive(" + intent.getAction() + ")");
	}

	private boolean decideIfStart(Context context) {

		log.debug("deciding if start");

		if (!NetworkUtils.isConnected(context))
			return false;

		log.debug("deciding if start: network ok");

		SharedPreferences prefs = context.getSharedPreferences(
				DEFAULT_PREFERENCES, Context.MODE_PRIVATE);
		long schedule = prefs.getLong(LAST_UPDATED_KEY, -1);
		long time = System.currentTimeMillis();

		if (prefs.getBoolean(WIFI_ONLY_KEY, DEFAULT_WIFI_ONLY)
				&& NetworkUtils.isConnectedMobile(context))
			return false;

		log.debug("deciding if start: wifi ok");

		try {
			if (schedule != -1)
				schedule += prefs.getLong(UPDATE_INTERVAL_KEY,
						DEFAULT_UPDATE_INTERVAL);
		} catch (ClassCastException e) {
			prefs.edit().putLong(UPDATE_INTERVAL_KEY, DEFAULT_UPDATE_INTERVAL);
		}

		log.debug("deciding if start: " + time + ">" + schedule);

		if (time > schedule) {
			return true;
		} else
			return false;
	}

}
