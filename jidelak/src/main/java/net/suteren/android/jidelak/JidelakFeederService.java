package net.suteren.android.jidelak;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
import org.w3c.tidy.Tidy;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class JidelakFeederService extends Service {
	static final String LOGGING_TAG = "JidelakFeederService";
	private JidelakDbHelper dbHelper;
	private static final int DURATION = 3000;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(LOGGING_TAG, "JidelakFeederService.onStartCommand()");
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(LOGGING_TAG, "JidelakFeederService.onCreate()");

		registerReceiver(new JidelakFeederReceiver(), new IntentFilter(
				Intent.ACTION_TIME_TICK));
	}

	private void updateData() {

		RestaurantDao restaurantDao = new RestaurantDao(getDbHelper());
		for (Restaurant restaurant : restaurantDao.findAll()) {
			URL sourceUrl = restaurant.getSourceUrl();
			InputStream template = new ByteArrayInputStream(
					restaurant.getTemplate());

			try {
				Node result = retrieve(sourceUrl, template);

				// TODO Auto-generated method stub
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(),
						getResources().getText(R.string.download_failed),
						DURATION);
				Log.e(LOGGING_TAG, e.getMessage(), e);
			} catch (TransformerException e) {
				Toast.makeText(getApplicationContext(),
						getResources().getText(R.string.unable_to_parse),
						DURATION);
				Log.e(LOGGING_TAG, e.getMessage(), e);
			} catch (ParserConfigurationException e) {
				Toast.makeText(
						getApplicationContext(),
						getResources().getText(
								R.string.parser_configuration_error), DURATION);
				Log.e(LOGGING_TAG, e.getMessage(), e);
			}

		}

	}

	private JidelakDbHelper getDbHelper() {
		if (dbHelper == null)
			dbHelper = new JidelakDbHelper(getApplicationContext());
		return dbHelper;
	}

	public Node retrieve(URL url, InputStream inXsl) throws IOException,
			TransformerException, ParserConfigurationException {
		URLConnection con = url.openConnection();
		Document d = tidy(con.getInputStream(), con.getContentEncoding());
		DOMResult res = transform(d, inXsl);
		return res.getNode();
	}

	private Document tidy(InputStream is, String enc) throws IOException {
		Tidy t = new Tidy();
		t.setInputEncoding(enc == null ? "cp1250" : enc);
		t.setNumEntities(true);
		t.setXmlOut(true);
		t.setShowWarnings(false);
		t.setTrimEmptyElements(true);
		// t.setQuoteNbsp(true);
		Document d = t.parseDOM(is, null);
		is.close();
		return d;
	}

	private DOMResult transform(Document d, InputStream inXsl)
			throws IOException, TransformerConfigurationException,
			TransformerFactoryConfigurationError, ParserConfigurationException,
			TransformerException {
		Transformer tr = TransformerFactory.newInstance().newTransformer(
				new StreamSource(inXsl));
		DOMResult res = new DOMResult(DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument());
		tr.transform(new DOMSource(d), res);
		return res;
	}
}