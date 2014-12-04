package net.suteren.android.jidelak;

import javax.xml.transform.TransformerException;

public class JidelakTransformerException extends JidelakException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5014659618064331293L;

	public JidelakTransformerException(String messageToUser,
			String templateName, String string, TransformerException e) {
		super(messageToUser, e);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TransformerException getCause() {
		return (TransformerException) super.getCause();
	}

	public String toString() {

		getCause().getMessageAndLocation();

		return String.format(getResource(), getCause().getMessageAndLocation())
				+ getCause().getLocalizedMessage();
	}
}
