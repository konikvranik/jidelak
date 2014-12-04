package net.suteren.android.jidelak;

import java.net.MalformedURLException;

public class JidelakMalformedURLException extends JidelakException {

	private String url;

	public JidelakMalformedURLException(String messageToUser, String url,
			MalformedURLException e) {
		super(messageToUser, e);
		setUrl(url);
	}

	private void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public MalformedURLException getCause() {
		return (MalformedURLException) super.getCause();
	}

	public String toString() {
		return String.format(getResource(), getUrl());
	}

	private static final long serialVersionUID = 2640785284710666283L;

}
