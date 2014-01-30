package net.suteren.android.jidelak;

import static net.suteren.android.jidelak.Constants.EXCEPTION;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class Utils {

	private static Logger log = LoggerFactory.getLogger(Utils.class);

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

	public static void makeNotification(Context ctx, int notifyID,
			JidelakException e) {
		makeNotification(ctx, ErrorViewActivity.class, notifyID, e);
	}

	public static void makeNotification(Context ctx, Class<?> clz,
			int notifyID, JidelakException e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		Intent intent = new Intent(ctx, clz);
		Bundle b = new Bundle();
		b.putSerializable(EXCEPTION, e);
		intent.putExtras(b);
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

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		stackBuilder.addParentStack(clz);
		stackBuilder.addNextIntent(intent);

		Notification notification = new NotificationCompat.Builder(ctx)
				.setSmallIcon(android.R.drawable.alert_dark_frame)
				.setContentTitle(ctx.getResources().getString(title))
				.setContentText(description)
				.setContentIntent(
						stackBuilder.getPendingIntent(0,
								PendingIntent.FLAG_UPDATE_CURRENT)).build();

		((NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
				notifyID, notification);

	}

	public static InputStream streamFromUrl(URL url) throws JidelakException {

		try {
			if (url.getProtocol().startsWith("file")) {

				return url.openStream();

			} else if (url.getProtocol().startsWith("http")) {

				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.connect();
				if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
					log.warn(String.format("Error %d: %s",
							con.getResponseCode(), con.getResponseMessage()));
					throw new JidelakException(R.string.http_error_response,
							new String[] {
									Integer.valueOf(con.getResponseCode())
											.toString(),
									con.getResponseMessage() });
				}
				return con.getInputStream();

			} else {
				throw new JidelakException(R.string.unsupported_protocol);
			}
		} catch (IOException e) {
			throw new JidelakException(R.string.unexpected_exception, e);
		}
	}

	public static InputStream streamFromUrl(Uri uri) throws JidelakException {
		try {
			return streamFromUrl(new URL(uri.toString()));
		} catch (MalformedURLException e) {
			throw new JidelakException(R.string.malformed_url, e);
		}
	}

}