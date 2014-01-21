/**
 * 
 */
package net.suteren.android.jidelak;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
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
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.RestaurantMarshaller;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Restaurant;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Instances;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Petr
 * 
 */
public class JidelakTemplateImporterActivity extends Activity {

	private static final String LOGGING_TAG = "JidelakTemplateImporterService";
	private Uri sourceUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			sourceUri = intent.getData();
		} else if (Intent.ACTION_SEND.equals(intent.getAction())
				|| Intent.ACTION_SENDTO.equals(intent.getAction())) {
			sourceUri = bundle != null ? (Uri) bundle.get(Intent.EXTRA_STREAM)
					: null;
		}

		// showIntent();
		if (sourceUri != null)
			ask();

	}

	private void ask() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// Yes button clicked
					try {
						importTemplate();
					} catch (JidelakException e) {
						Log.e(LOGGING_TAG, e.getMessage(), e);
						// TODO Auto-generated catch block
					}
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
				getIntent().getAction() + " - Are you sure to import "
						+ sourceUri + "?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener)
				.setCancelable(false).show();
	}

	void importTemplate() throws JidelakException {

		Toast.makeText(getApplicationContext(),
				"Importing " + sourceUri.toString() + "...", Toast.LENGTH_LONG)
				.show();

		JidelakDbHelper dbh = new JidelakDbHelper(getApplicationContext());
		RestaurantDao restaurantDao = new RestaurantDao(dbh);

		Restaurant restaurant = new Restaurant();

		try {
			restaurantDao.insert(restaurant);

			String fileName = saveLocally(sourceUri, restaurant);

			parseConfig(openFileInput(fileName), restaurant);

			restaurantDao.update(restaurant);

			new SourceDao(dbh).insert(restaurant.getSource());

			new AvailabilityDao(dbh).insert(restaurant.getOpeningHours());

		} catch (Exception e) {
			deleteFile(restaurant.getTemplateName());
			restaurantDao.delete(restaurant);
			if (e instanceof JidelakException)
				throw (JidelakException) e;
			else
				throw new JidelakException(R.string.unexpected_exception, e);
		} finally {
			dbh.notifyDataSetChanged();
		}
	}

	@SuppressLint("WorldReadableFiles")
	void parseConfig(InputStream fileStream, Restaurant restaurant)
			throws JidelakException {

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document d = db.newDocument();

			Node n = d.appendChild(d.createElement("jidelak"));
			n.appendChild(d.createElement("config"));
			Transformer tr = TransformerFactory.newInstance().newTransformer(
					new StreamSource(fileStream));
			DOMResult res = new DOMResult(DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument());
			tr.transform(new DOMSource(d), res);

			try {
				FileOutputStream os = openFileOutput("debug.xsl",
						MODE_WORLD_READABLE);
				StreamResult deb = new StreamResult(os);
				tr = TransformerFactory.newInstance().newTransformer();
				tr.transform(new DOMSource(res.getNode()), deb);
				os.close();
			} catch (Throwable e) {
				if (e instanceof JidelakException)
					throw (JidelakException) e;
				else
					throw new JidelakException(R.string.unexpected_exception, e);
			}

			RestaurantMarshaller rm = new RestaurantMarshaller();
			// rm.setSource(source);
			rm.unmarshall("#document.jidelak.config", res.getNode(), restaurant);

		} catch (ParserConfigurationException e) {
			throw new JidelakException(R.string.parser_configuration_exception,
					e);
		} catch (TransformerConfigurationException e) {
			throw new JidelakException(
					R.string.transformer_configuration_exception, e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new JidelakException(
					R.string.transformer_factory_configuration_exception, e);
		} catch (TransformerException e) {
			throw new JidelakException(R.string.transformer_exception, e);
		} finally {
			try {
				fileStream.close();
			} catch (IOException e) {
				throw new JidelakException(R.string.unexpected_exception, e);
			}
		}
	}

	String saveLocally(Uri uri, Restaurant restaurant) throws IOException {

		String fileName = restaurant.getTemplateName();
		InputStream sourceStream = new URL(uri.toString()).openStream();
		FileOutputStream out = openFileOutput(fileName, MODE_PRIVATE);

		Writer bw = new BufferedWriter(new OutputStreamWriter(out));
		try {
			Reader br = new BufferedReader(new InputStreamReader(sourceStream));
			try {
				CharBuffer buf = CharBuffer.allocate(64);
				while (br.read(buf) >= 0) {
					bw.append((CharSequence) buf.flip());
					buf.clear();
				}
			} finally {
				br.close();
			}
		} finally {
			bw.close();
		}
		return fileName;
	}
}
