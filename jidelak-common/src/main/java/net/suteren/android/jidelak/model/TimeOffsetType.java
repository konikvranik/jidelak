package net.suteren.android.jidelak.model;

import java.util.Calendar;

public enum TimeOffsetType {
	DAY(Calendar.DAY_OF_MONTH), WEEK(Calendar.WEEK_OF_YEAR), MONTH(
			Calendar.MONTH), YEAR(Calendar.YEAR);

	private int type;

	TimeOffsetType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
