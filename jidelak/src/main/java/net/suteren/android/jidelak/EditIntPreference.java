package net.suteren.android.jidelak;

import static net.suteren.android.jidelak.Constants.DAY_IN_MILLIS;
import static net.suteren.android.jidelak.Constants.HOUR_IN_MILLIS;
import static net.suteren.android.jidelak.Constants.MINUTE_IN_MILLIS;
import static net.suteren.android.jidelak.Constants.WEEK_IN_MILLIS;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditIntPreference extends DialogPreference {

	private SeekBar myView;
	private TypedArray values;
	private int titleRes;
	private static Logger log = LoggerFactory
			.getLogger(EditIntPreference.class);

	public EditIntPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(attrs);
	}

	public EditIntPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup(attrs);
	}

	private void setup(AttributeSet attrs) {
		values = getContext().getResources().obtainTypedArray(
				attrs.getAttributeResourceValue("suteren", "values",
						R.array.update_times));
		titleRes = attrs.getAttributeResourceValue("android", "title",
				R.string.refresh_period_title);
	}

	@Override
	protected void onBindDialogView(final View view) {
		super.onBindDialogView(view);

		myView = (SeekBar) view.findViewById(R.id.value);

		myView.setMax(values.length() - 1);
		SharedPreferences sharedPreferences = getSharedPreferences();
		long sv = sharedPreferences.getLong(getKey(), 0);
		for (int i = 0; i < values.length(); i++) {
			long v = Math.round(values.getFloat(i, 0));
			if (sv <= v) {
				myView.setProgress(i);
				break;
			}
		}

		updateDisplay(view, sv);

		myView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar paramSeekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar paramSeekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar paramSeekBar, int paramInt,
					boolean paramBoolean) {
				long value = Math.round(values.getFloat(paramInt, 0));

				updateDisplay(view, value);
			}

		});
	}

	private void updateDisplay(final View view, long value) {
		TextView displayValue = (TextView) view.findViewById(R.id.display);
		String valueString = prettyPrintValue(value);
		displayValue.setText(valueString);
	}

	private String prettyPrintValue(long value) {
		String valueString = "-";
		if (value >= WEEK_IN_MILLIS && (value % WEEK_IN_MILLIS) == 0) {
			valueString = String.format(Locale.getDefault(), "%d %s", value
					/ WEEK_IN_MILLIS, Utils.getPlural(getContext()
					.getResources(), R.array.weeks, value / WEEK_IN_MILLIS));

		} else if (value >= DAY_IN_MILLIS && (value % DAY_IN_MILLIS) == 0) {
			valueString = String.format(Locale.getDefault(), "%d %s", value
					/ DAY_IN_MILLIS, Utils.getPlural(getContext()
					.getResources(), R.array.days, value / DAY_IN_MILLIS));
		} else if (value >= HOUR_IN_MILLIS && (value % HOUR_IN_MILLIS) == 0) {
			valueString = String.format(Locale.getDefault(), "%d %s", value
					/ HOUR_IN_MILLIS, Utils.getPlural(getContext()
					.getResources(), R.array.hours, value / HOUR_IN_MILLIS));
		} else if (value >= MINUTE_IN_MILLIS && (value % MINUTE_IN_MILLIS) == 0) {
			valueString = String
					.format(Locale.getDefault(), "%d %s", value
							/ MINUTE_IN_MILLIS, Utils.getPlural(getContext()
							.getResources(), R.array.minutes, value
							/ MINUTE_IN_MILLIS));
		}
		return valueString;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (!positiveResult)
			return;

		long result = Math.round(values.getFloat(myView.getProgress(), 0));

		Editor e = getEditor();
		e.putLong(getKey(), result);
		e.commit();
		values.recycle();
		notifyChanged();
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		TextView title = (TextView) view.findViewById(android.R.id.title);

		title.setText(getContext().getResources().getString(titleRes) + ": "
				+ prettyPrintValue(getSharedPreferences().getLong(getKey(), 0)));
		// view.findViewById(android.R.id.summary);
		// view.findViewById(android.R.id.widget_frame);
		// view.findViewById(android.R.id.icon);

		// debugViewIds(view);
	}

	public static View debugViewIds(View view) {
		log.debug("traversing: " + view.getClass().getSimpleName() + ", id: "
				+ view.getId());
		if (view.getParent() != null && (view.getParent() instanceof ViewGroup)) {
			return debugViewIds((View) view.getParent());
		} else {
			debugChildViewIds(view, 0);
			return view;
		}
	}

	private static void debugChildViewIds(View view, int spaces) {
		if (view instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++) {
				View child = group.getChildAt(i);
				log.debug(padString("view: " + child.getClass().getSimpleName()
						+ "(" + child.getId() + ")", spaces));
				debugChildViewIds(child, spaces + 1);
			}
		}
	}

	private static String padString(String str, int noOfSpaces) {
		if (noOfSpaces <= 0) {
			return str;
		}
		StringBuilder builder = new StringBuilder(str.length() + noOfSpaces);
		for (int i = 0; i < noOfSpaces; i++) {
			builder.append(' ');
		}
		return builder.append(str).toString();
	}

}
