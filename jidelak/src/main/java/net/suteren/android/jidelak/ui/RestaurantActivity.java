/**
 * 
 */
package net.suteren.android.jidelak.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Locale;

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

import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.RestaurantMarshaller;
import net.suteren.android.jidelak.model.Restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.annotation.TargetApi;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

/**
 * @author Petr
 * 
 */
public class RestaurantActivity extends ActionBarActivity {

	private ActionBar ab;

	private static Logger log = LoggerFactory
			.getLogger(RestaurantActivity.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restaurant_view);
		setupActionBar();

		Restaurant restaurant = new Restaurant(getIntent().getLongExtra(
				"restaurant", 0));

		log.debug("Restaurant id: " + restaurant.getId());

		try {
			restaurant = retrieveRestaurant(restaurant);
		} catch (JidelakException e) {
			// TODO Auto-generated catch block
		}

		log.debug("restaurant x name: " + restaurant.getName());
		log.debug("restaurant x code: " + restaurant.getCode());
		log.debug("restaurant x version: " + restaurant.getVersion());

		StringBuffer sb = new StringBuffer(
				"<html><head><link rel='stylesheet' href='restaurant.css' type='text/css' /></head><body><h1>");
		sb.append(restaurant.getName());
		sb.append("</h1>");

		restaurant.getOpeningHours();

		Address address = restaurant.getAddress();
		if (address != null) {
			sb.append("<address>");
			sb.append(address.toString());
			sb.append("</address>");
		}
		metaInfo(restaurant, sb);

		sb.append("</body></html>");

		WebView restaurantView = (WebView) getWindow().findViewById(
				R.id.restaurant);
		if (Build.VERSION.SDK_INT >= 3.0)
			transparencyHack(restaurantView);

		restaurantView.loadDataWithBaseURL(
				String.format("file:///android_res/raw/restaurant?id=%d",
						restaurant.getId()), sb.toString(), "text/html",
				"UTF-8", "");

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void transparencyHack(WebView webView) {
		webView.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
	}

	protected void metaInfo(Restaurant restaurant, StringBuffer sb) {

		sb.append("<table><tr><th>");
		sb.append(getResources().getString(R.string.version));
		sb.append("</th><td>");
		sb.append(restaurant.getVersion());
		sb.append("</td></tr><tr><th>");
		sb.append(getResources().getString(R.string.code));
		sb.append("</th><td>");
		sb.append(restaurant.getCode());
		sb.append("</td></tr><tr><th>");
		sb.append(getResources().getString(R.string.last_modified));
		sb.append("</th><td>");

		File f = getFileStreamPath(restaurant.getTemplateName());
		long lm = f.lastModified();
		sb.append(DateFormat.getDateInstance(DateFormat.LONG,
				Locale.getDefault()).format(lm));
		sb.append(" ");
		sb.append(DateFormat.getTimeInstance(DateFormat.LONG,
				Locale.getDefault()).format(lm));

		sb.append("</td></tr></table>");
	}

	protected Restaurant retrieveRestaurant(Restaurant restaurant)
			throws JidelakException {

		restaurant = new RestaurantDao(new JidelakDbHelper(
				getApplicationContext())).findById(restaurant);

		log.debug("restaurant name: " + restaurant.getName());
		log.debug("restaurant template: " + restaurant.getTemplateName());

		try {
			RestaurantMarshaller rm = new RestaurantMarshaller();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document d = db.newDocument();

			Node n = d.appendChild(d.createElement("jidelak"));
			n.appendChild(d.createElement("config"));
			Transformer tr = TransformerFactory.newInstance().newTransformer(
					new StreamSource(
							openFileInput(restaurant.getTemplateName())));
			DOMResult res = new DOMResult(DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument());
			tr.transform(new DOMSource(d), res);
			rm.unmarshall("#document.jidelak.config", res.getNode(), restaurant);

			log.debug("restaurant name: " + restaurant.getName());
			log.debug("restaurant code: " + restaurant.getCode());
			log.debug("restaurant version: " + restaurant.getVersion());

		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
		}
		return restaurant;
	}

	private void setupActionBar() {
		ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
	}
}
