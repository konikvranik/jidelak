package net.suteren.android.jidelak;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

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

	public JidelakException(int messageToUser, String... strings) {
		this(messageToUser);
		args = strings;
	}

	public JidelakException(int malformedUrl, Exception e, String... strings) {
		this(malformedUrl, e);
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

	private void readObject(ObjectInputStream aInputStream)
			throws ClassNotFoundException, IOException {
		aInputStream.defaultReadObject();
	}

	private void writeObject(ObjectOutputStream aOutputStream)
			throws IOException {
		// perform the default serialization for all non-transient, non-static
		// fields
		aOutputStream.defaultWriteObject();
		// aOutputStream.writeObject(resource);
		// aOutputStream.writeObject(args);
	}

	public String toString(Context ctx) {
		return ctx.getResources()
				.getString(getResource(), (Object[]) getArgs());
	}
}
