package net.suteren.android.jidelak;

import static net.suteren.android.jidelak.Constants.EXCEPTION;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.StringTokenizer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

public class Utils {

	public Utils() {
	}

	public static Locale stringToLocale(String s) {
		if (s == null)
			return null;
		StringTokenizer tempStringTokenizer = new StringTokenizer(s, "_");
		String l = null;
		if (tempStringTokenizer.hasMoreTokens())
			l = (String) tempStringTokenizer.nextElement();
		String c = null;
		if (tempStringTokenizer.hasMoreTokens())
			c = (String) tempStringTokenizer.nextElement();
		return new Locale(l, c);
	}

	public static String getPlural(Resources res, int key, long count) {
		int[] plurals = res.getIntArray(R.array.plurals);
		int position = 0;
		for (position = 0; position < plurals.length
				&& plurals[position] <= Math.abs(count); position++)
			;
		if (position > plurals.length)
			position = plurals.length - 1;

		return res.getStringArray(key)[position];
	}

	public static void makeNotification(Context ctx, Class<?> clz,
			int notifyID, JidelakException e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		Intent intent = new Intent(ctx, clz);
		intent.getExtras().putSerializable(EXCEPTION, e);
		makeNotification(ctx, clz, notifyID, e.getResource(), sw.toString(),
				intent);
	}

	public static void makeNotification(Context ctx, Class<?> clz,
			int notifyID, int title, String description) {
		makeNotification(ctx, clz, notifyID, title, description, new Intent(
				ctx, clz));
	}

	public static void makeNotification(Context ctx, Class<?> clz,
			int notifyID, int title, String description, Intent intent) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				ctx).setSmallIcon(android.R.drawable.alert_dark_frame)
				.setContentTitle(ctx.getResources().getString(title))
				.setContentText(description);

		Notification notification = mBuilder.build();
		notification.contentIntent = PendingIntent.getActivity(ctx, 0, intent,
				0);
		((NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
				notifyID, notification);
	}
}