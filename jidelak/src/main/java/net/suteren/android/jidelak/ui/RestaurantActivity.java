/**
 * 
 */
package net.suteren.android.jidelak.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.SortedSet;

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

import net.suteren.android.jidelak.AndroidUtils;
import net.suteren.android.jidelak.JidelakDbHelper;
import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.dao.AvailabilityDao;
import net.suteren.android.jidelak.dao.MealDao;
import net.suteren.android.jidelak.dao.RestaurantDao;
import net.suteren.android.jidelak.dao.RestaurantMarshaller;
import net.suteren.android.jidelak.dao.SourceDao;
import net.suteren.android.jidelak.model.Address;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.Toast;

/**
 * @author Petr
 * 
 */
public class RestaurantActivity extends AbstractJidelakActivity {

	private static Logger log = LoggerFactory
			.getLogger(RestaurantActivity.class);

	private ExpandableListContextMenuInfo lastMenuInfo;

	@SuppressWarnings("unused")
	private Menu mainMenu;

	private Restaurant restaurant;

	private void buildAddress(StringBuffer sb, Address address) {
		if (address != null) {

			sb.append("<div class='contact'>");
			sb.append("<h2>");
			sb.append(getResources().getString(R.string.contact));
			sb.append("</h2>");

			sb.append("<address class='phone'>");
			buildParagraph(sb, "phone", parsePhone(address.getPhone()));
			sb.append("</address>");

			sb.append("<address class='internet'>");
			buildParagraph(
					sb,
					"email",
					makeLink(
							"mailto:",
							address.getExtras() == null ? null : address
									.getExtras().get(
											RestaurantDao.E_MAIL.getName())));
			buildParagraph(sb, "web", makeLink(null, address.getUrl()));
			sb.append("</address>");

			sb.append("<address class='postal'>");

			buildParagraph(sb, "featurename", address.getFeatureName());
			buildParagraph(sb, "premises", address.getPremises());

			if (address.getMaxAddressLineIndex() > -1) {
				sb.append("<p class='address'>");

				for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
					if (i > 0)
						sb.append("<br/>");
					sb.append(address.getAddressLine(i));
				}
				sb.append("</p>");
			}

			buildParagraph(sb, "thoroughfare", address.getThoroughfare());
			buildParagraph(sb, "subthoroughfare", address.getSubThoroughfare());
			buildParagraph(sb, "sublocality", address.getSubLocality());
			buildParagraph(sb, "locality", address.getLocality());
			buildParagraph(sb, "zip", address.getPostalCode());
			buildParagraph(sb, "subadmin", address.getSubAdminArea());
			buildParagraph(sb, "admin", address.getAdminArea());
			buildParagraph(sb, "country", address.getCountryName());
			buildParagraph(sb, "countrycode", address.getCountryCode());

			sb.append("</address>");
			sb.append("</div>");
		}
	}

	private String parsePhone(String phone) {
		String[] phones = phone.split("[,;]");
		StringBuffer sb = new StringBuffer();
		for (String s : phones) {
			sb.append(makeLink("tel:", s));
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length() - 1);
		return sb.toString();
	}

	private String makeLink(String string, String string2) {
		if (string2 == null)
			return null;
		StringBuffer sb = new StringBuffer("<a href='");
		if (string != null)
			sb.append(string);
		sb.append(string2);
		sb.append("'>");
		sb.append(string2);
		sb.append("</a>");

		return sb.toString();
	}

	protected void buildParagraph(StringBuffer sb, String clz, String content) {
		if (content != null) {
			sb.append("<p class='");
			sb.append(clz);
			sb.append("'>");
			sb.append(content);
			sb.append("</p>");
		}
	}

	private void buildOpeningHours(StringBuffer sb,
			SortedSet<Availability> openingHours) {

		if (openingHours == null || openingHours.isEmpty())
			return;
		sb.append("<div class='serving'>");
		sb.append("<h2>");
		sb.append(getResources().getString(R.string.opening_hours));
		sb.append("</h2>");
		sb.append("<table class='serving'>");
		sb.append("<tr class='header'><th></th><th>");
		sb.append(getResources().getString(R.string.from));
		sb.append("</th><th>");
		sb.append(getResources().getString(R.string.to));
		sb.append("</th></tr>");

		for (Availability a : openingHours) {

			log.debug("Opening hours: " + a);

			sb.append("<tr><th>");
			if (a.getDow() != null) {

				Calendar cal = Calendar.getInstance(Locale.getDefault());
				cal.set(Calendar.DAY_OF_WEEK, a.getDow());
				sb.append(new SimpleDateFormat("E", Locale.getDefault())
						.format(cal.getTime()));

			} else if (a.getYear() != null && a.getMonth() != null
					&& a.getDay() != null) {

				sb.append(DateFormat.getDateInstance(DateFormat.SHORT,
						Locale.getDefault()).format(a.getCalendar().getTime()));
			}

			if (a.getClosed() != null && a.getClosed()) {
				sb.append("</th><td colspan='2' class='closed'>");
				sb.append(getResources().getString(R.string.closed));
			} else {

				sb.append("</th><td class='from'>");
				if (a.getFrom() != null)
					sb.append(a.getFrom());
				sb.append("</td><td class='to'>");
				if (a.getTo() != null)
					sb.append(a.getTo());
			}
			sb.append("</td></tr>");

		}

		sb.append("</table>");
		sb.append("</div>");

	}

	protected void buildMetaInfo(StringBuffer sb, Restaurant restaurant) {

		sb.append("<div class='meta'>");
		sb.append("<table class='meta'>");

		sb.append("<tr class='code'><th>");
		sb.append(getResources().getString(R.string.code));
		sb.append("</th><td>");
		sb.append(restaurant.getCode());
		sb.append("</td></tr>");

		sb.append("<tr class='version'><th>");
		sb.append(getResources().getString(R.string.template_version));
		sb.append("</th><td>");
		sb.append(restaurant.getVersion());
		sb.append("</td></tr>");

		sb.append("<tr class='imported'><th>");
		sb.append(getResources().getString(R.string.last_modified));
		sb.append("</th><td>");
		File f = getFileStreamPath(restaurant.getTemplateName());
		long lm = f.lastModified();
		sb.append(getResources().getString(
				R.string.date_time,
				DateFormat
						.getDateInstance(DateFormat.LONG, Locale.getDefault())
						.format(lm),
				DateFormat.getDateInstance(DateFormat.SHORT,
						Locale.getDefault()).format(lm)));
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("</div>");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restaurant_view);

		restaurant = new Restaurant(getIntent().getLongExtra("restaurant", 0));

		log.debug("Restaurant id: " + restaurant.getId());

		try {
			restaurant = retrieveRestaurant(restaurant);
		} catch (JidelakException e) {
			// TODO Auto-generated catch block
		}

		log.debug("restaurant x name: " + restaurant.getName());
		log.debug("restaurant x code: " + restaurant.getCode());
		log.debug("restaurant x version: " + restaurant.getVersion());

		StringBuffer sb = new StringBuffer("<html><head>");
		sb.append("<link rel='stylesheet' href='restaurant.css' type='text/css' />");
		// sb.append("<meta name=\"viewport\" content=\"target-densitydpi=device-dpi\" />");
		sb.append("</head><body>");
		sb.append("<h1>");
		sb.append(restaurant.getName());
		sb.append("</h1>");

		buildOpeningHours(sb, restaurant.getOpeningHours());

		buildAddress(sb, restaurant.getAddress());
		buildMetaInfo(sb, restaurant);

		sb.append("</body></html>");

		WebView restaurantView = (WebView) getWindow().findViewById(
				R.id.restaurant);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			AndroidUtils.transparencyHack(getApplicationContext(),
					restaurantView);
		else
			restaurantView.setBackgroundColor(getResources().getColor(
					android.R.color.black));

		restaurantView.getSettings().setDefaultFontSize(11);
		restaurantView.getSettings().setSupportMultipleWindows(true);

		restaurantView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				try {
					Intent intent = Intent.parseUri(url,
							Intent.URI_INTENT_SCHEME);
					view.getContext().startActivity(intent);
				} catch (URISyntaxException e) {
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(R.string.malformed_url,
									url), Toast.LENGTH_SHORT).show();
				} catch (ActivityNotFoundException e) {
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(
									R.string.activity_not_found, url),
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});
		restaurantView.loadDataWithBaseURL(
				String.format("file:///android_res/raw/restaurant?id=%d",
						restaurant.getId()), sb.toString(), "text/html",
				"UTF-8", "");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mainMenu = menu;
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restaurant_detail_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
				.getMenuInfo();

		if (info == null) {
			info = lastMenuInfo;
		} else {
			lastMenuInfo = info;
		}

		log.debug("Item: " + item);
		log.debug("Info: " + info);

		String uri;
		switch (item.getItemId()) {

		case R.id.action_call:

			uri = "tel:" + restaurant.getAddress().getPhone();
			log.debug("Opening dialer: " + uri);

			try {
				startActivity(Intent.parseUri(uri, Intent.URI_INTENT_SCHEME));
			} catch (URISyntaxException e) {
				log.warn(e.getMessage(), e);
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.malformed_url, uri),
						Toast.LENGTH_SHORT).show();
			} catch (ActivityNotFoundException e) {
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(R.string.activity_not_found,
								uri), Toast.LENGTH_SHORT).show();
			}

			return true;

		case R.id.action_locate:

			addressToSearchString(restaurant.getAddress());

			uri = "geo:"
					+ (restaurant.getAddress().hasLongitude()
							&& restaurant.getAddress().hasLatitude() ? restaurant
							.getAddress().getLatitude()
							+ ","
							+ restaurant.getAddress().getLongitude() : "0,0")
					+ "?q=" + addressToSearchString(restaurant.getAddress());

			log.debug("Opening map: " + uri);

			try {
				startActivity(Intent.parseUri(uri, Intent.URI_INTENT_SCHEME));
			} catch (URISyntaxException e) {
				log.warn(e.getMessage(), e);
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.malformed_url, uri),
						Toast.LENGTH_SHORT).show();
			} catch (ActivityNotFoundException e) {
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(R.string.activity_not_found,
								uri), Toast.LENGTH_SHORT).show();
			}

			return true;

		case R.id.action_delete:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					getResources().getString(R.string.delete_restaurant,
							restaurant.getName()))
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									MealDao mdao = new MealDao(getDbHelper());
									mdao.delete(restaurant);
									AvailabilityDao adao = new AvailabilityDao(
											getDbHelper());
									adao.delete(restaurant);
									SourceDao sdao = new SourceDao(
											getDbHelper());
									sdao.delete(restaurant);
									RestaurantDao rdao = new RestaurantDao(
											getDbHelper());
									rdao.delete(restaurant);
									RestaurantActivity.this
											.deleteFile(restaurant
													.getTemplateName());
									getDbHelper().notifyDataSetChanged();

									RestaurantActivity.this.finish();

								}

							}).setNegativeButton("No", null)
					.setCancelable(false).show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private String addressToSearchString(Address a) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i <= a.getMaxAddressLineIndex(); i++) {
			sb.append(a.getAddressLine(i));
			sb.append(",");
		}

		if (a.getFeatureName() != null) {
			sb.append(a.getFeatureName());
			sb.append(",");
		}
		if (a.getPremises() != null) {
			sb.append(a.getPremises());
			sb.append(",");
		}
		if (a.getSubThoroughfare() != null) {
			sb.append(a.getSubThoroughfare());
			sb.append(",");
		}
		if (a.getThoroughfare() != null) {
			sb.append(a.getThoroughfare());
			sb.append(",");
		}
		if (a.getSubLocality() != null) {
			sb.append(a.getSubLocality());
			sb.append(",");
		}
		if (a.getPostalCode() != null) {
			sb.append(a.getPostalCode());
			if (a.getLocality() != null)
				sb.append(" ");
			else
				sb.append(", ");

		}
		if (a.getLocality() != null) {
			sb.append(a.getLocality());
			sb.append(",");
		}
		if (a.getAdminArea() != null) {
			sb.append(a.getAdminArea());
			sb.append(",");
		}
		if (a.getSubAdminArea() != null) {
			sb.append(a.getSubAdminArea());
			sb.append(",");
		}
		if (a.getCountryName() != null) {
			sb.append(a.getCountryName());
			sb.append(",");
		}
		if (a.getCountryCode() != null) {
			sb.append(a.getCountryCode());
		}
		return sb.toString();
	}

	protected Restaurant retrieveRestaurant(Restaurant restaurant)
			throws JidelakException {

		restaurant = new RestaurantDao(
				JidelakDbHelper.getInstance(getApplicationContext()))
				.findById(restaurant);

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

			rm.setUpdateOh(false);
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

}
