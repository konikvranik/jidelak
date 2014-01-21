package net.suteren.android.jidelak;

public class JidelakException extends Exception {

	private int resource;

	public JidelakException(int messageToUser, Throwable e) {
		super(e);
		setResource(messageToUser);
	}

	public JidelakException(int messageToUser) {
		super();
		setResource(messageToUser);
	}

	public void setResource(int messageToUser) {
		resource = messageToUser;
	}

	public int getResource() {
		return resource;
	}

	private static final long serialVersionUID = -5345112897072655374L;

}
