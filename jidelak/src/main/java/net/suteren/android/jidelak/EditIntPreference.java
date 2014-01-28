package net.suteren.android.jidelak;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditIntPreference extends DialogPreference {

	private SeekBar myView;

	public EditIntPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EditIntPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onBindDialogView(final View view) {
		super.onBindDialogView(view);

		myView = (SeekBar) view.findViewById(R.id.value);

		SharedPreferences sharedPreferences = getSharedPreferences();
		myView.setProgress(getMultiplier()
				/ (sharedPreferences.getInt(getKey(), 0) + 1));

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
				TextView display = (TextView) view.findViewById(R.id.display);
				display.setText("" + paramInt);
			}
		});
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (!positiveResult)
			return;

		int result = getMultiplier() / (myView.getProgress() + 1);
		Editor e = getEditor();
		e.putInt(getKey(), result);
		e.commit();
	}

	protected int getMultiplier() {
		return 3600000 * 24 * 7 * myView.getMax();
	}
}
