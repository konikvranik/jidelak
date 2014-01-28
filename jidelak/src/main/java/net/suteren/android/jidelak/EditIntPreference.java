package net.suteren.android.jidelak;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditIntPreference extends DialogPreference {

	public EditIntPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EditIntPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected View onCreateDialogView() {
		final View result = super.onCreateDialogView();
		SeekBar sb = (SeekBar) result.findViewById(R.id.value);
		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar paramSeekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar paramSeekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar paramSeekBar, int paramInt,
					boolean paramBoolean) {
				TextView display = (TextView) result.findViewById(R.id.display);
				display.setText(paramInt);
			}
		});
		return result;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		SeekBar value = (SeekBar) getDialog().getCurrentFocus().findViewById(
				R.id.value);
		int result = 3600000 * 24 * 7 * value.getMax() / value.getProgress();
		getEditor().putInt(getKey(), result);

	}
}
