package net.suteren.android.jidelak;

import static net.suteren.android.jidelak.Constants.EXCEPTION;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.suteren.android.jidelak.ui.ErrorViewActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationUtils {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory
			.getLogger(NotificationUtils.class);

	public NotificationUtils() {
	}

	public static void makeNotification(Context ctx, int notifyID,
			JidelakException e) {
		makeNotification(ctx, ErrorViewActivity.class, notifyID,
				R.drawable.ic_action_warning, e);
	}

	public static void makeNotification(Context ctx, Class<?> clz,
			int notifyID, Integer icon, JidelakException e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		Intent intent = new Intent(ctx, clz);
		Bundle b = new Bundle();
		b.putSerializable(EXCEPTION, e);
		intent.putExtras(b);
		makeNotification(ctx, clz, notifyID, icon, R.string.app_name,
				e.toString(ctx), intent);
	}

	public static void makeNotification(Context ctx, Integer icon,
			Class<?> clz, int notifyID, int title, String description) {
		makeNotification(ctx, clz, notifyID, icon, title, description,
				new Intent(ctx, clz));
	}

	public static void makeNotification(Context ctx, Class<?> clz,
			int notifyID, Integer icon, int title, String description,
			Intent intent) {

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		stackBuilder.addParentStack(clz);
		stackBuilder.addNextIntent(intent);

		if (icon == null)
			icon = R.drawable.ic_action_warning;
		Notification notification = new NotificationCompat.Builder(ctx)
				.setSmallIcon(icon)
				.setContentTitle(ctx.getResources().getString(title))
				.setContentText(description)
				.setContentIntent(
						stackBuilder.getPendingIntent(0,
								PendingIntent.FLAG_UPDATE_CURRENT)).build();

		((NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
				notifyID, notification);

	}

}