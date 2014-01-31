package net.suteren.android.jidelak;

import java.text.ParseException;

import android.content.Context;

public class JidelakParseException extends JidelakException {

	private static final long serialVersionUID = -8213065110502170934L;
	private String date;
	private String format;

	public JidelakParseException(int messageResource, String format,
			String date, ParseException e) {
		super(messageResource, e);

		setFormat(format);
		setDate(date);

	}

	private void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	private void setFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	@Override
	public ParseException getCause() {
		return (ParseException) super.getCause();
	}

	public String toString(Context ctx) {
		return ctx.getResources().getString(getResource(), getFormat(),
				getDate())
				+ getCause().getLocalizedMessage()
				+ " @"
				+ getCause().getErrorOffset();
	}

}
