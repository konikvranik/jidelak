package net.suteren.android.jidelak;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import net.suteren.android.jidelak.model.Address;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.webkit.WebView;

public class AndroidUtils {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AndroidUtils.class);

	public AndroidUtils() {
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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void transparencyHack(Context ctx, WebView usage) {
		usage.setBackgroundColor(ctx.getResources().getColor(
				android.R.color.transparent));
		usage.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
	}

	public static String openingHoursToString(Context ctx, Restaurant r) {
		return openingHoursToString(ctx, r.getOpeningHours());
	}

	public static String openingHoursToString(Context ctx,
			Collection<Availability> openingHours) {

		if (openingHours == null || openingHours.isEmpty())
			return ctx.getResources().getString(R.string.closed);

		TreeSet<Availability> tm = new TreeSet<Availability>(
				new Comparator<Availability>() {

					@Override
					public int compare(Availability lhs, Availability rhs) {

						if (lhs == null && rhs == null)
							return 0;
						if (lhs != null && rhs == null)
							return 1;
						if (lhs == null && rhs != null)
							return -1;

						int r = 0;

						if ((r = Utils.compare(lhs.getFrom(), rhs.getFrom())) != 0)
							return r;

						r = Utils.compare(lhs.getTo(), rhs.getTo());

						return r;
					}
				});

		for (Availability availability : openingHours) {
			tm.add(availability);
		}
		StringBuffer sb = new StringBuffer();
		for (Availability availability : tm) {

			if (availability.isClosed()) {
				sb.append(ctx.getResources().getString(R.string.closed));
			} else {

				if (availability.getDescription() != null) {
					sb.append(availability.getDescription());
					sb.append(" ");
				}

				if (availability.getFrom() != null
						&& availability.getTo() == null) {
					sb.append(ctx.getResources().getString(R.string.avail_from));
					sb.append(" ");
				}
				if (availability.getTo() != null
						&& availability.getFrom() == null) {
					sb.append(ctx.getResources().getString(R.string.avail_to));
					sb.append(" ");
				}

				if (availability.getFrom() != null)
					sb.append(availability.getFrom());
				if (availability.getFrom() != null
						&& availability.getTo() != null)
					sb.append(" – ");
				if (availability.getTo() != null)
					sb.append(availability.getTo());
			}
			if (availability.getFrom() != null || availability.getTo() != null)
				sb.append(", ");
		}

		if (sb.length() > 1)
			sb.delete(sb.length() - 2, sb.length());

		return sb.toString();
	}

	public static String openingHoursToString(Context ctx, Calendar day,
			Restaurant r) {
		return openingHoursToString(ctx, r.getOpeningHours(day));
	}

	public static void cloneAddress(Address source, Address addr) {
		for (int i = 0; i < source.getMaxAddressLineIndex(); i++)
			addr.setAddressLine(i, source.getAddressLine(i));
		addr.setAdminArea(source.getAdminArea());
		addr.setCountryCode(source.getCountryCode());
		addr.setCountryName(source.getCountryName());
		addr.setExtras(source.getExtras());
		addr.setFeatureName(source.getFeatureName());
		addr.setLocality(source.getLocality());
		addr.setPhone(source.getPhone());
		addr.setPostalCode(source.getPostalCode());
		addr.setPremises(source.getPremises());
		addr.setAdminArea(source.getAdminArea());
		addr.setSubLocality(source.getSubLocality());
		addr.setSubThoroughfare(source.getSubThoroughfare());
		addr.setThoroughfare(source.getThoroughfare());
		addr.setUrl(source.getUrl());
	}

}