package net.suteren.android.jidelak.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Restaurant;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class RestaurantMarshallerTest {

	private static Logger log = LoggerFactory.getLogger("RestaurantTest");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testClean() {
		fail("Not yet implemented");
	}

	@Test
	public void testMarshallNode() throws ParserConfigurationException,
			TransformerConfigurationException,
			TransformerFactoryConfigurationError, TransformerException,
			JidelakException {

		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();

		Node n = prepareDocument(doc);
		printNode(n);

		SourceMarshallerTest.prepareDocument(n);

		Restaurant restaurant = new Restaurant();
		new RestaurantMarshaller().unmarshall(n, restaurant);

		assertEquals("Pokusný", restaurant.getName());

		assertEquals("cp1250", restaurant.getSource().iterator().next()
				.getEncoding());

		SortedSet<Availability> oh = restaurant.getOpeningHours();
		assertEquals(6, oh.size());

		Iterator<Availability> it = oh.iterator();
		Availability av = it.next();
		assertEquals(Integer.valueOf(Calendar.MONDAY), av.getDow());
		assertEquals("8:00", av.getFrom());
		assertEquals("17:00", av.getTo());

		av = it.next();
		assertEquals(Integer.valueOf(Calendar.TUESDAY), av.getDow());
		assertEquals("8:00", av.getFrom());
		assertEquals("17:00", av.getTo());

		av = it.next();
		av = it.next();
		av = it.next();
		av = it.next();
		assertEquals(Integer.valueOf(1), av.getDay());
		assertEquals(Integer.valueOf(1), av.getMonth());
		assertEquals(Integer.valueOf(2010), av.getYear());
		assertEquals(true, av.getClosed());
	}

	@Test
	public void testMarshallStringNode() {
		fail("Not yet implemented");
	}

	public static Node prepareDocument(Node n) {

		Document doc;
		if (n instanceof Document) {
			doc = (Document) n;
		} else {
			doc = n.getOwnerDocument();
		}

		n = n.appendChild(doc.createElement("jidelak"));

		n = n.appendChild(doc.createElement("config"));
		Node rn = n.appendChild(doc.createElement("restaurant"));
		n = rn.appendChild(doc.createElement("name"));
		n = n.appendChild(doc.createTextNode("Pokusný"));

		Node on = rn.appendChild(doc.createElement("open"));

		n = on.appendChild(doc.createElement("term"));
		addAttr(n, "day-of-week", "Po");
		addAttr(n, "from", "8:00");
		addAttr(n, "to", "17:00");

		n = on.appendChild(doc.createElement("term"));
		addAttr(n, "day-of-week", "Út");
		addAttr(n, "from", "8:00");
		addAttr(n, "to", "17:00");

		n = on.appendChild(doc.createElement("term"));
		addAttr(n, "day-of-week", "St");
		addAttr(n, "from", "8:00");
		addAttr(n, "to", "17:00");

		n = on.appendChild(doc.createElement("term"));
		addAttr(n, "day-of-week", "Čt");
		addAttr(n, "from", "8:00");
		addAttr(n, "to", "17:00");

		n = on.appendChild(doc.createElement("term"));
		addAttr(n, "day-of-week", "Pá");
		addAttr(n, "from", "8:00");
		addAttr(n, "to", "17:00");

		n = on.appendChild(doc.createElement("term"));
		addAttr(n, "date", "1. 1. 2010");
		addAttr(n, "closed", "true");

		return rn;
	}

	private static void printNode(Node rn)
			throws TransformerConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		StringWriter sb = new StringWriter();
		Transformer tr = TransformerFactory.newInstance().newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.transform(new DOMSource(rn), new StreamResult(sb));
		log.info(sb.toString());
	}

	private static void addAttr(Node n, String name, String value) {
		Attr a = n.getOwnerDocument().createAttribute(name);
		a.setValue(value);
		n.getAttributes().setNamedItem(a);
	}
}
