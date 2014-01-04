package net.suteren.android.jidelak;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class JidelakFeederService extends Service {
	static final String LOGGING_TAG = "JidelakFeederService";

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

	public Node retrieve(URL url, InputStream inXsl) throws IOException,
			TransformerException, ParserConfigurationException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
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