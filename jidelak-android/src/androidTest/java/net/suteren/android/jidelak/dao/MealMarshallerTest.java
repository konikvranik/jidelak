package net.suteren.android.jidelak.dao;

import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.model.Meal;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.fail;

public class MealMarshallerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private MealMarshaller sm;
	private DocumentBuilder db;

	@Before
	public void setUp() throws Exception {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		db = dbf.newDocumentBuilder();
		sm = new MealMarshaller();

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
	public void testUnmarshallNodeT() throws JidelakException,
			ParserConfigurationException, SAXException, IOException {

		Meal s = new Meal();

		Document d = db.newDocument();
		// d = RestaurantMarshallerTest.prepareDocument(d);
		// d = prepareDocument(d);

		d = db.parse(this.getClass().getResourceAsStream("/debug.result"));

		Node n = d.getDocumentElement();
		sm.unmarshall("#document.jidelak.config.restaurant.menu", n, s);

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
	public void testUnmarshallStringNodeT() throws JidelakException,
			SAXException, IOException {
		Meal s = new Meal();

		Document d = db.newDocument();
		prepareDocument(RestaurantMarshallerTest.prepareDocument(d));

		d = db.parse(this.getClass().getResourceAsStream("/debug.result"));

		sm.unmarshall("#document.jidelak.config.restaurant.menu",
				d.getDocumentElement(), s);

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
