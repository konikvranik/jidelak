package net.suteren.android.jidelak.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.suteren.android.jidelak.model.Source;
import net.suteren.android.jidelak.model.TimeType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SourceMarshallerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private SourceMarshaller sm;
	private DocumentBuilder db;

	@Before
	public void setUp() throws Exception {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		db = dbf.newDocumentBuilder();
		sm = new SourceMarshaller();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUnmarshallHelperStringMapOfStringStringSource() {
		fail("Not yet implemented");
	}

	@Test
	public void testClean() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnmarshallNodeT() throws MalformedURLException {

		Source s = new Source();

		Node d = db.newDocument();
		d = RestaurantMarshallerTest.prepareDocument(d);
		d = prepareDocument(d);

		Node n = d;
		sm.unmarshall(n, s);

		assertEquals(Integer.valueOf(Calendar.MONDAY), s.getFirstdayofweek());
		assertEquals(TimeType.RELATIVE, s.getTimeType());
		assertEquals("week", s.getOffsetBase());
		assertEquals(Integer.valueOf(0), s.getOffset());
		assertEquals("cp1250", s.getEncoding());
		assertEquals(new SimpleDateFormat("dd. mmm. yyyy", new Locale("cs",
				"CZ")), s.getDateFormat());
		assertEquals(new Locale("cs", "CZ"), s.getLocale());
		assertEquals(new URL(
				"http://lgavenir.cateringmelodie.cz/cz/denni-menu-tisk.php"),
				s.getUrl());
	}

	public static Node prepareDocument(Node n) {

		Document doc;
		if (n instanceof Document) {
			doc = (Document) n;
		} else {
			doc = n.getOwnerDocument();
		}

		Element sn = (Element) n.appendChild(doc.createElement("source"));

		sn.setAttribute("time", "relative");
		sn.setAttribute("base", "week");
		sn.setAttribute("firstDayOfWeek", "Po");
		sn.setAttribute("timeOffset", "0");
		sn.setAttribute("encoding", "cp1250");
		sn.setAttribute("dateFormat", "dd. mmm. yyyy");
		sn.setAttribute("locale", "cs_CZ");
		sn.setAttribute("url",
				"http://lgavenir.cateringmelodie.cz/cz/denni-menu-tisk.php");
		return sn;
	}

	@Test
	public void testUnmarshallStringNodeT() throws MalformedURLException {
		Source s = new Source();

		Node d = db.newDocument();
		prepareDocument(RestaurantMarshallerTest.prepareDocument(d));

		sm.unmarshall("jidelak.config.restaurant", d, s);

		assertEquals(Integer.valueOf(Calendar.MONDAY), s.getFirstdayofweek());
		assertEquals(TimeType.RELATIVE, s.getTimeType());
		assertEquals("week", s.getOffsetBase());
		assertEquals(Integer.valueOf(0), s.getOffset());
		assertEquals("cp1250", s.getEncoding());
		assertEquals(new SimpleDateFormat("dd. mmm. yyyy", new Locale("cs",
				"CZ")), s.getDateFormat());
		assertEquals(new Locale("cs", "CZ"), s.getLocale());
		assertEquals(new URL(
				"http://lgavenir.cateringmelodie.cz/cz/denni-menu-tisk.php"),
				s.getUrl());

	}

	@Test
	public void testProcessElementHook() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNextNodeNode() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNextNodeNodeBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testPath() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessAttributes() {
		fail("Not yet implemented");
	}

}
