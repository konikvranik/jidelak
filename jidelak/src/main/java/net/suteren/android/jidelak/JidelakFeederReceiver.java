/**
 *
 */
package net.suteren.android.jidelak;

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
		SharedPreferences prefs = context.getSharedPreferences("default",
				Context.MODE_PRIVATE);
		long schedule = prefs.getLong(JidelakFeederService.LAST_UPDATED, -1);
		long time = System.currentTimeMillis();

		if (schedule != -1)
			schedule += prefs.getLong(JidelakFeederService.UPDATE_INTERVAL,
					JidelakFeederService.DEFAULT_UPDATE_INTERVAL);

		if (time > schedule) {
			prefs.edit().putLong(JidelakFeederService.LAST_UPDATED, time);
			return true;
		} else
			return false;
	}

}
