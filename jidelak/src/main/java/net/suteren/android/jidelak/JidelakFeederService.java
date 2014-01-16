package net.suteren.android.jidelak;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
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
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.RestaurantMarshaller;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
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

public class JidelakFeederService extends Service {
	static final String LOGGING_TAG = "JidelakFeederService";
	private JidelakDbHelper dbHelper;
	private boolean force = false;

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
		RestaurantDao rdao = new RestaurantDao(getDbHelper());
		AvailabilityDao adao = new AvailabilityDao(getDbHelper());

		RestaurantMarshaller rm = new RestaurantMarshaller();

		for (Source source : sdao.findAll()) {

			try {
				InputStream template = openFileInput(source.getRestaurant()
						.getTemplateName());

				Node result = retrieve(source.getUrl(), template);

				Restaurant restaurant = source.getRestaurant();

				rm.unmarshall("#document.jidelak.config", result, restaurant);

				Set<Availability> avs = new HashSet<Availability>();
				for (Meal meal : restaurant.getMenu()) {
					avs.add(meal.getAvailability());
				}

				for (Availability av : avs) {
					List<Meal> atd = mdao.findByDayAndRestaurant(
							av.getCalendar(), restaurant);
					mdao.delete(atd);
				}

				for (Meal meal : restaurant.getMenu()) {
					adao.insert(meal.getAvailability());
					mdao.insert(meal);
				}

				Restaurant savedRestaurant = rdao.findById(restaurant);
				if (savedRestaurant != null
						&& savedRestaurant.getOpeningHours() != null)
					adao.delete(savedRestaurant.getOpeningHours());

				adao.insert(restaurant.getOpeningHours());
				rdao.update(restaurant);

				// TODO Auto-generated method stub
			} catch (IOException e) {
				throw new JidelakException(e);
			} catch (TransformerException e) {
				throw new JidelakException(e);
			} catch (ParserConfigurationException e) {
				throw new JidelakException(e);
			} finally {
				getDbHelper().notifyDataSetChanged();
			}
		}

	}

	private JidelakDbHelper getDbHelper() {
		if (dbHelper == null)
			dbHelper = new JidelakDbHelper(getApplicationContext());
		return dbHelper;
	}

	Node retrieve(URL url, InputStream inXsl) throws IOException,
			TransformerException, ParserConfigurationException {
		URLConnection con = url.openConnection();
		InputStream is = con.getInputStream();
		Document d = getTidy(con.getContentEncoding()).parseDOM(is, null);
		is.close();
		DOMResult res = transform(d, inXsl);
		return res.getNode();
	}

	private Tidy getTidy(String enc) throws IOException {

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

		return t;
	}

	private DOMResult transform(Document d, InputStream inXsl)
			throws IOException, TransformerConfigurationException,
			TransformerFactoryConfigurationError, ParserConfigurationException,
			TransformerException {

		TransformerFactory trf = TransformerFactory.newInstance();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);

		Transformer tr = trf.newTransformer();

		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.transform(
				new DOMSource(d),
				new StreamResult(openFileOutput("debug.source",
						MODE_WORLD_READABLE)));

		tr = trf.newTransformer(new StreamSource(inXsl));
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