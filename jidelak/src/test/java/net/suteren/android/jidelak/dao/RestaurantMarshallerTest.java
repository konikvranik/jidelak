package net.suteren.android.jidelak.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.suteren.android.jidelak.model.Restaurant;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class RestaurantMarshallerTest {

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
	public void testMarshallNode() throws ParserConfigurationException {

		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();

		Node n = prepareDocument(doc);
		SourceMarshallerTest.prepareDocument(n);

		Restaurant restaurant = new Restaurant();
		new RestaurantMarshaller().unmarshall(n, restaurant);

		assertEquals("Pokusný", restaurant.getName());

		assertEquals("cp1250", restaurant.getSource().get(0).getEncoding());

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

		return rn;
	}
}
