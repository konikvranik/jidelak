/**
 * 
 */
package net.suteren.android.jidelak.ui;

import static net.suteren.android.jidelak.Constants.EXCEPTION;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;

import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.JidelakMalformedURLException;
import net.suteren.android.jidelak.JidelakParseException;
import net.suteren.android.jidelak.JidelakTransformerException;
import net.suteren.android.jidelak.R;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebView;

/**
 * @author jd39426
 * 
 */
public class ErrorViewActivity extends ActionBarActivity {

	private ActionBar ab;
	private String text;
	private JidelakException exception;

	private static Logger log = LoggerFactory
			.getLogger(ErrorViewActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		log.debug("ErrorViewActivity onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.errorview);
		setupActionBar();

		Bundle params = getIntent().getExtras();
		JidelakException e = (JidelakException) params
				.getSerializable(EXCEPTION);

		setException(e);

		WebView errorView = (WebView) getWindow().findViewById(R.id.error);

		String text = e.getLocalizedMessage();
		if (text == null || "".equals(text))
			text = e.getMessage();

		setText(getKnownCause(getException()));

		errorView.loadDataWithBaseURL("", getText(), "text/html", "UTF-8", "");

	}

	private void setText(Throwable cause) {
		if (cause instanceof JidelakTransformerException) {
			setText(((JidelakTransformerException) cause)
					.toString(getApplicationContext()));

		} else if (cause instanceof JidelakParseException) {
			setText(((JidelakParseException) cause)
					.toString(getApplicationContext()));

		} else if (cause instanceof JidelakMalformedURLException) {
			setText(((JidelakMalformedURLException) cause)
					.toString(getApplicationContext()));

		} else if (cause instanceof JidelakException) {
			setText(((JidelakException) cause)
					.toString(getApplicationContext()));

		} else if (cause == null) {
			renderStacktrace(getException());
		} else {
			renderStacktrace(cause);

		}
	}

	private void renderStacktrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		StringBuffer sb = new StringBuffer("<h1>");
		sb.append(t.getMessage());
		sb.append("</h1><pre>");
		sb.append(StringEscapeUtils.escapeHtml4(sw.toString()));
		sb.append("</pre>");
		setText(sb.toString());
	}

	private Throwable getKnownCause(Throwable exception) {
		do {
			if (exception instanceof JidelakException)
				return exception;
		} while ((exception = exception.getCause()) != null);
		return exception;
	}

	private void setException(JidelakException e) {
		this.exception = e;
	}

	public JidelakException getException() {
		return exception;
	}

	private void setText(String sb) {
		this.text = sb;
	}

	public String getText() {
		return text;
	}

	public void sendTo(View v) {
		startActivity(new Intent(android.content.Intent.ACTION_SENDTO,
				Uri.parse("exception://"
						+ getException().getClass().getCanonicalName()
						+ "/?message="
						+ URLEncoder.encode(getException().getMessage())))
				.putExtra(Intent.EXTRA_TEXT, getText()).setType("text/plain"));

	}

	public void sendError(View v) throws JidelakException {
		throw getException();
	}

	private void setupActionBar() {
		ab = getSupportActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
	}
}
