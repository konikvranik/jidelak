package net.suteren.android.jidelak;

import android.content.Intent;
import android.test.ServiceTestCase;
import net.suteren.android.jidelak.ui.FeederService;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class JidelakFeederServiceTest extends
        ServiceTestCase<FeederService> {

    public JidelakFeederServiceTest() {
        super(FeederService.class);
    }


    public void setUp() throws Exception {
    }

    public void tearDown() throws Exception {
    }

    public void testUpdateData() throws FileNotFoundException {

        startService(new Intent());

//		getService().updateData();

        fail("Not yet implemented");
    }

    public void testRetrieve() throws MalformedURLException, FileNotFoundException, IOException,
            TransformerException, ParserConfigurationException {

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
