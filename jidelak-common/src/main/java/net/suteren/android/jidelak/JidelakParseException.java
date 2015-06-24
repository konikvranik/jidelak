package net.suteren.android.jidelak;

import java.text.ParseException;

public class JidelakParseException extends JidelakException {

	private static final long serialVersionUID = -8213065110502170934L;
	private String date;
	private String format;

	public JidelakParseException(String messageResource, String format,
			String date, Throwable e) {
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

	public String toString() {
		return String.format(
				getResource(),
				getFormat() == null ? "" : getFormat(),
				getDate() == null ? "" : getDate(),
				getRestaurant() == null ? ""
						: getRestaurant().getName() == null ? String.format(
								"restaurant id: %d", getRestaurant().getId())
								: getRestaurant().getName());
	}

}
