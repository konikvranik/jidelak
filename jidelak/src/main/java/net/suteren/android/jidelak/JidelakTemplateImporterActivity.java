/**
 * 
 */
package net.suteren.android.jidelak;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Petr
 * 
 */
public class JidelakTemplateImporterActivity extends Activity {

	private static final String LOGGING_TAG = "JidelakTemplateImporterService";
	private Uri source;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			source = intent.getData();
		} else if (Intent.ACTION_SEND.equals(intent.getAction())
				|| Intent.ACTION_SENDTO.equals(intent.getAction())) {
			source = bundle != null ? (Uri) bundle.get(Intent.EXTRA_STREAM)
					: null;
		}

		showIntent();

		ask();

	}

	private void showIntent() {
		Intent intent = getIntent();

		if (intent == null) {
			Toast.makeText(getApplicationContext(), "No intent",
					Toast.LENGTH_LONG).show();
			return;
		}

		StringBuffer sb = new StringBuffer(
				intent.toUri(Intent.URI_INTENT_SCHEME));
		sb.append("\nAction: ");
		sb.append(intent.getAction());
		sb.append("\nscheme: ");
		sb.append(intent.getScheme());
		sb.append("\ntype: ");
		sb.append(intent.getType());
		sb.append("\ndata: ");
		sb.append(source.toString());

		if (intent.getExtras() != null)
			sb.append(showBundle(intent.getExtras()));

		if (intent.getCategories() != null) {
			sb.append("\n--- Categories: ---");
			for (String category : intent.getCategories()) {
				sb.append("\n\t");
				sb.append(category);
			}
		}
		Log.d(LOGGING_TAG, sb.toString());
		Toast.makeText(getApplicationContext(), sb.toString(),
				Toast.LENGTH_LONG).show();

	}

	private Object showBundle(Bundle extras) {
		StringBuffer sb = new StringBuffer();
		for (String key : extras.keySet()) {
			sb.append("\n");
			sb.append(key);
			sb.append("=>");
			sb.append(extras.get(key));
		}
		return sb.toString();
	}

	private void ask() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// Yes button clicked
					importTemplate();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					break;
				}
				finish();
			}

		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				getIntent().getAction() + " - Are you sure to import " + source
						+ "?").setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener)
				.setCancelable(false).show();
	}

	private void importTemplate() {

		Toast.makeText(getApplicationContext(),
				"Importing " + source.toString() + "...", Toast.LENGTH_LONG)
				.show();
		try {
			URL sourceUrl = new URL(source.toString());

			InputStream sourceStream = sourceUrl.openStream();
			
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
