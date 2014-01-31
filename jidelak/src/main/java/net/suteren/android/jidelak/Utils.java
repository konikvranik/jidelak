package net.suteren.android.jidelak;

import static net.suteren.android.jidelak.Constants.EXCEPTION;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import net.suteren.android.jidelak.ui.ErrorViewActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;

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

	public static NetworkInfo getNetworkInfo(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	/**
	 * Check if there is any connectivity
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		return (info != null && info.isConnected());
	}

	/**
	 * Check if there is any connectivity to a Wifi network
	 * 
	 * @param context
	 * @param type
	 * @return
	 */
	public static boolean isConnectedWifi(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * Check if there is any connectivity to a mobile network
	 * 
	 * @param context
	 * @param type
	 * @return
	 */
	public static boolean isConnectedMobile(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
	}

	/**
	 * Check if there is fast connectivity
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnectedFast(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		return (info != null && info.isConnected() && isConnectionFast(
				info.getType(), info.getSubtype()));
	}

	/**
	 * Check if the connection is fast
	 * 
	 * @param type
	 * @param subType
	 * @return
	 */
	public static boolean isConnectionFast(int type, int subType) {
		if (type == ConnectivityManager.TYPE_WIFI) {
			return true;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return false; // ~ 14-64 kbps
			case TelephonyManager.NETWORK_TYPE_EDGE:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				return true; // ~ 400-1000 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				return true; // ~ 600-1400 kbps
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return false; // ~ 100 kbps
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				return true; // ~ 2-14 Mbps
			case TelephonyManager.NETWORK_TYPE_HSPA:
				return true; // ~ 700-1700 kbps
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				return true; // ~ 1-23 Mbps
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return true; // ~ 400-7000 kbps
				/*
				 * Above API level 7, make sure to set android:targetSdkVersion
				 * to appropriate level to use these
				 */
			case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
				return true; // ~ 1-2 Mbps
			case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
				return true; // ~ 5 Mbps
			case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
				return true; // ~ 10-20 Mbps
			case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
				return false; // ~25 kbps
			case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
				return true; // ~ 10+ Mbps
				// Unknown
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			default:
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isServiceRunning(Context ctx, String serviceClassName) {
		final ActivityManager activityManager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		final List<RunningServiceInfo> services = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo runningServiceInfo : services) {
			if (runningServiceInfo.service.getClassName().equals(
					serviceClassName)) {
				return true;
			}
		}
		return false;
	}
}