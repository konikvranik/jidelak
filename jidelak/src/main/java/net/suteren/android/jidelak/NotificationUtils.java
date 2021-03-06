package net.suteren.android.jidelak;

import static net.suteren.android.jidelak.Constants.EXCEPTION;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.suteren.android.jidelak.ui.ErrorViewActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;

public class NotificationUtils {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory
			.getLogger(NotificationUtils.class);

	public NotificationUtils() {
	}

	public static void makeNotification(Context ctx, JidelakException e) {
		makeNotification(ctx, e.isHandled() ? null : ErrorViewActivity.class,
				e.isHandled() ? R.drawable.ic_action_warning
						: R.drawable.ic_action_error, e);
	}

	public static void makeNotification(Context ctx, Class<?> clz,
			Integer icon, JidelakException e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		Intent intent = null;
		if (!e.isHandled()) {
			intent = new Intent(ctx, clz);
			Bundle b = new Bundle();
			b.putSerializable(EXCEPTION, e);
			intent.putExtras(b);
		}
		makeNotification(ctx, clz, getNotificationId(e), icon,
				R.string.app_name, e.toString(), intent);
	}

	private static int getNotificationId(JidelakException e) {
		int id = e.getErrorType() == null ? 0 : e.getErrorType().ordinal();
		if (e.getRestaurant() != null && e.getRestaurant().getId() != null)
			id += e.getRestaurant().getId() * 10000000;
		else if (e.getSource() != null && e.getSource().getId() != null)
			id += e.getSource().getId() * 100;
		return id;
	}

	public static void makeNotification(Context ctx, Integer icon,
			Class<?> clz, int notifyID, int title, String description) {
		makeNotification(ctx, clz, notifyID, icon, title, description,
				new Intent(ctx, clz));
	}

	public static void makeNotification(Context ctx, Class<?> clz,
			int notifyID, Integer icon, int title, String description,
			Intent intent) {

		if (icon == null)
			icon = R.drawable.ic_action_warning;

		Builder builder = new NotificationCompat.Builder(ctx)
				.setTicker(ctx.getResources().getString(R.string.app_name))
				.setSmallIcon(icon)
				.setStyle(
						new NotificationCompat.BigTextStyle()
								.bigText(description))
				.setContentTitle(ctx.getResources().getString(title))
				.setContentText(description).setAutoCancel(true);
		if (clz != null) {
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
			stackBuilder.addParentStack(clz);
			stackBuilder.addNextIntent(intent);
			builder.setContentIntent(stackBuilder.getPendingIntent(0,
					PendingIntent.FLAG_UPDATE_CURRENT));
			builder.setAutoCancel(false);
		}

		((NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
				notifyID, builder.build());

	}
}