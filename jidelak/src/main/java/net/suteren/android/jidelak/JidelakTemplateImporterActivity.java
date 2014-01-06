/**
 * 
 */
package net.suteren.android.jidelak;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.CharBuffer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.model.Restaurant;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

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

		// showIntent();

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

			RestaurantDao restaurantDao = new RestaurantDao(
					new JidelakDbHelper(getApplicationContext()));

			Restaurant restaurant = new Restaurant();
			restaurantDao.insert(restaurant);

			String fileName = saveLocally(source, restaurant);

		Object configNode = parseConfig(openFileInput(fileName));

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Restaurant parseConfig(InputStream fileStream) throws FileNotFoundException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document d = db.newDocument();

			d.appendChild(d.createElement("config"));
			Transformer tr = TransformerFactory.newInstance().newTransformer(
					new StreamSource(fileStream));
			DOMResult res = new DOMResult(DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument());
			tr.transform(new DOMSource(d), res);

			Node n = res.getNode().getFirstChild();

			Log.d(LOGGING_TAG,
					"Node: " + n.getNodeName() + " .. " + n.getNodeType());

			if ("jidelak".equals(n.getNodeName())) {
				n = n.getFirstChild();

				boolean secondLevel = false;

				while (n != null) {
					if (!secondLevel && "config".equals(n.getNodeName())) {
						n = n.getFirstChild();
						secondLevel = true;
					}

					if (secondLevel) {
						if ("id".equals(n.getNodeName())) {

						} else if ("name".equals(n.getNodeName())) {

						} else if ("source".equals(n.getNodeName())) {

						}
					}
					n = n.getNextSibling();
				}

			} else {
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String saveLocally(Uri uri, Restaurant restaurant)
			throws IOException {

		String fileName = "template_" + restaurant.getId();
		InputStream sourceStream = new URL(uri.toString()).openStream();
		FileOutputStream out = openFileOutput(fileName, MODE_PRIVATE);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				sourceStream));
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
			try {
				CharBuffer target = CharBuffer.allocate(1024);
				while (-1 != br.read(target)) {
					bw.write(target.array());
				}
			} finally {
				bw.close();
			}
		} finally {
			br.close();
		}
		return fileName;
	}
}
