package net.suteren.android.jidelak;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
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

import net.suteren.android.jidelak.dao.RestaurantMarshaller;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

public class Utils {

	private static Logger log = LoggerFactory.getLogger(Utils.class);
	
	public Utils() {
	}

	public static Locale stringToLocale(String s) {
		if (s == null)
			return null;
		StringTokenizer tempStringTokenizer = new StringTokenizer(s, "_");
		String l = null;
		if (tempStringTokenizer.hasMoreTokens())
			l = (String) tempStringTokenizer.nextElement();
		String c = null;
		if (tempStringTokenizer.hasMoreTokens())
			c = (String) tempStringTokenizer.nextElement();
		return new Locale(l, c);
	}

	public static <T extends Comparable<T>> int compare(T o1, T o2) {
		if (o1 == null) {
			if (o2 == null)
				return 0;
			else
				return -1;
		} else {
			if (o2 == null)
				return 1;
			else
				return o1.compareTo((T) o2);
		}
	}

	public static void parseConfig(InputStream fileStream, Restaurant restaurant)
			throws Exception {

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

			RestaurantMarshaller rm = new RestaurantMarshaller();
			// rm.setSource(source);

			rm.setUpdateOh(restaurant.getOpeningHours() == null
					|| restaurant.getOpeningHours().isEmpty());
			rm.unmarshall("#document.jidelak.config", res.getNode(), restaurant);

		}

		finally {
			fileStream.close();
		}
	}

	public static Node retrieve(Source source, InputStream inXsl)
			throws IOException, TransformerException,
			ParserConfigurationException, JidelakException {

		HttpURLConnection con = (HttpURLConnection) source.getUrl()
				.openConnection();
		con.connect();
		if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new JidelakException("http_error_response (%5$s): %6$s", new String[] {
					Integer.valueOf(con.getResponseCode()).toString(),
					con.getResponseMessage() }).setSource(source)
					.setRestaurant(source.getRestaurant()).setHandled(true)
					.setErrorType(ErrorType.NETWORK);
		}

		log.debug("Response code: " + con.getResponseCode());

		InputStream is = con.getInputStream();
		String enc = source.getEncoding();
		if (enc == null)
			enc = con.getContentEncoding();

		ByteArrayOutputStream debugos = null;
		if (log.isDebugEnabled()) {
			debugos = new ByteArrayOutputStream();
		}
		Document d = getTidy(enc).parseDOM(is, debugos);
		if (log.isDebugEnabled()) {
			log.debug("== Tidy output =================================================================");
			log.debug(debugos.toString());
			log.debug("================================================================================");
		}

		if (log.isDebugEnabled()) {
			log.debug("== Tidy transformed output =====================================================");
			StringWriter sw = new StringWriter();
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.transform(new DOMSource(d), new StreamResult(sw));
			log.debug(sw.toString());
			log.debug("================================================================================");
		}

		is.close();
		con.disconnect();

		DOMResult res = transform(d, inXsl);

		if (log.isDebugEnabled()) {
			log.debug("== REstaurant transformed output ===============================================");
			StringWriter sw = new StringWriter();
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.transform(new DOMSource(res.getNode()), new StreamResult(sw));
			log.debug(sw.toString());
			log.debug("================================================================================");
		}

		return res.getNode();
	}

	public static Tidy getTidy(String enc) {

		log.debug("Enc: " + enc);

		Tidy t = new Tidy();

		t.setInputEncoding(enc == null ? "UTF-8" : enc);
		t.setOutputEncoding("UTF-8");
//		t.setPrintBodyOnly(true); // only print the content
		t.setXmlOut(true); // to XML
		t.setSmartIndent(true);

		t.setNumEntities(false);
		// t.setQuoteMarks(false);
		// t.setQuoteAmpersand(false);
		t.setRawOut(true);
		// t.setHideEndTags(true);
		// t.setXmlTags(false);
		// t.setXHTML(true);
		t.setShowWarnings(log.isDebugEnabled());
		// t.setTrimEmptyElements(true);
		t.setQuiet(!log.isDebugEnabled());
		// t.setQuoteNbsp(true);

		Properties props = new Properties();

		// suppport of several HTML5 tags due to lunchtime.
		props.put("new-blocklevel-tags",
				"header,nav,section,article,aside,footer");

		Configuration conf = t.getConfiguration();
		conf.addProps(props);

		return t;
	}

	public static DOMResult transform(Document d, InputStream inXsl)
			throws IOException, TransformerConfigurationException,
			TransformerFactoryConfigurationError, ParserConfigurationException,
			TransformerException {

		TransformerFactory trf = TransformerFactory.newInstance();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);

		Transformer tr = trf.newTransformer();

		tr = trf.newTransformer(new StreamSource(inXsl));
		DOMResult res = new DOMResult(dbf.newDocumentBuilder().newDocument());
		tr.transform(new DOMSource(d), res);

		return res;
	}

	public static DOMResult transform(InputStream inXsl) throws IOException,
			TransformerConfigurationException,
			TransformerFactoryConfigurationError, ParserConfigurationException,
			TransformerException {

		TransformerFactory trf = TransformerFactory.newInstance();
		Transformer tr = trf.newTransformer();
		DOMResult res = new DOMResult();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.transform(new StreamSource(inXsl), res);
		return res;
	}
}