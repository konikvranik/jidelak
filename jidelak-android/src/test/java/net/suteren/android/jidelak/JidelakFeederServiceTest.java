package net.suteren.android.jidelak;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;

import android.content.Intent;
import android.test.ServiceTestCase;

public class JidelakFeederServiceTest extends
		ServiceTestCase<JidelakFeederService> {

	public JidelakFeederServiceTest() {
		super(JidelakFeederService.class);
	}

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
	public void testUpdateData() throws FileNotFoundException {
		
		startService(new Intent());
		
//		getService().updateData();

		fail("Not yet implemented");
	}

	@Test
	public void testRetrieve() throws MalformedURLException, FileNotFoundException, IOException, TransformerException, ParserConfigurationException {

//		startService(new Intent());
//
//		Node n = getService()
//				.retrieve(
//						new URL(
//								"http://lgavenir.cateringmelodie.cz/cz/denni-menu-tisk.php"),
//						getService().openFileInput("restaurant-1.xsl"));
//
//		fail("Not yet implemented");
	}

}
