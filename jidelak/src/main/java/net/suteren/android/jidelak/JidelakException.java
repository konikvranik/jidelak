package net.suteren.android.jidelak;

public class JidelakException extends Exception {

	private int resource;
	private String[] args;

	public JidelakException(int messageToUser, Throwable e) {
		super(e);
		setResource(messageToUser);
	}

	public JidelakException(int messageToUser) {
		super();
		setResource(messageToUser);
	}

	public JidelakException(int messageToUser, String[] strings) {
		this(messageToUser);
		args = strings;
	}

	public void setResource(int messageToUser) {
		resource = messageToUser;
	}

	public int getResource() {
		return resource;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public String[] getArgs() {
		return args;
	}

	private static final long serialVersionUID = -5345112897072655374L;

}
