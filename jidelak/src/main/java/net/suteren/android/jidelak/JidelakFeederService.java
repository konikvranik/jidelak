package net.suteren.android.jidelak;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.MealMarshaller;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Source;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class JidelakFeederService extends Service {
	static final String LOGGING_TAG = "JidelakFeederService";
	private JidelakDbHelper dbHelper;
	private boolean force = false;
	private static final int DURATION = 3000;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(LOGGING_TAG, "JidelakFeederService.onStartCommand()");
		// this.force = intent.getExtras().getBoolean("force", false);
		new Worker().execute(new Void[0]);
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(LOGGING_TAG, "JidelakFeederService.onCreate()");

		registerReceiver(new JidelakFeederReceiver(), new IntentFilter(
				Intent.ACTION_TIME_TICK));
	}

	void updateData() throws JidelakException {

		SourceDao sdao = new SourceDao(getDbHelper());
		MealDao mdao = new MealDao(getDbHelper());

		for (Source source : sdao.findAll()) {

			try {
				InputStream template = openFileInput(source.getRestaurant()
						.getTemplateName());

				Node result = retrieve(source.getUrl(), template);

				Meal meal = new Meal();
				meal.setRestaurant(source.getRestaurant());
				meal.setSource(source);

				MealMarshaller mm = new MealMarshaller();
				mm.setSource(source);
				mm.unmarshall("#document.jidelak.config.restaurant.menu", result, meal);

				mdao.insert(meal);

				// TODO Auto-generated method stub
			} catch (IOException e) {
				// Toast.makeText(getApplicationContext(),
				// getResources().getText(R.string.download_failed),
				// DURATION).show();
				Log.e(LOGGING_TAG, e.getMessage(), e);
				throw new JidelakException(e);
			} catch (TransformerException e) {
				// Toast.makeText(getApplicationContext(),
				// getResources().getText(R.string.unable_to_parse),
				// DURATION).show();
				Log.e(LOGGING_TAG, e.getMessage(), e);
				throw new JidelakException(e);
			} catch (ParserConfigurationException e) {
				// Toast.makeText(
				// getApplicationContext(),
				// getResources().getText(
				// R.string.parser_configuration_error), DURATION)
				// .show();
				Log.e(LOGGING_TAG, e.getMessage(), e);
				throw new JidelakException(e);
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
		// t.setNumEntities(false);
		// t.setQuoteMarks(false);
		// t.setQuoteAmpersand(false);
		// t.setRawOut(true);
		// t.setHideEndTags(true);
		// t.setXmlTags(false);
		t.setXmlOut(true);
		// t.setXHTML(true);
		t.setOutputEncoding("utf8");
		t.setShowWarnings(false);
		// t.setTrimEmptyElements(true);
		t.setQuiet(true);
		// t.setSmartIndent(true);
		// t.setQuoteNbsp(true);

		Document d = t.parseDOM(is, null);
		is.close();
		return d;
	}

	private DOMResult transform(Document d, InputStream inXsl)
			throws IOException, TransformerConfigurationException,
			TransformerFactoryConfigurationError, ParserConfigurationException,
			TransformerException {

		TransformerFactory trf = TransformerFactory.newInstance();

		Transformer tr = trf.newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.transform(
				new DOMSource(d),
				new StreamResult(openFileOutput("debug.source",
						MODE_WORLD_READABLE)));

		tr = trf.newTransformer(new StreamSource(inXsl));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		DOMResult res = new DOMResult(dbf.newDocumentBuilder().newDocument());
		tr.transform(new DOMSource(d), res);

		tr = trf.newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.transform(new DOMSource(res.getNode()), new StreamResult(
				openFileOutput("debug.result", MODE_WORLD_READABLE)));

		return res;
	}

	private class Worker extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				updateData();

				// Toast.makeText(getApplicationContext(), "data imported",
				// Toast.LENGTH_LONG).show();

			} catch (JidelakException e) {
				Log.e(LOGGING_TAG, e.getMessage(), e);
				// TODO Auto-generated catch block
			}
			return null;
		}

	}
}