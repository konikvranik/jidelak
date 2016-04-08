package net.suteren.android.jidelak.dao;

import android.test.AndroidTestCase;
import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.model.Source;
import net.suteren.android.jidelak.model.TimeType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SourceMarshallerTest extends AndroidTestCase {


    private SourceMarshaller sm;
    private DocumentBuilder db;

    public void setUp() throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder();
        sm = new SourceMarshaller();

    }

    public void tearDown() throws Exception {
    }

    public void testUnmarshallHelperStringMapOfStringStringSource() {
        fail("Not yet implemented");
    }

    public void testClean() {
        fail("Not yet implemented");
    }

    public void testUnmarshallNodeT() throws MalformedURLException, JidelakException {

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

    public void testUnmarshallStringNodeT() throws MalformedURLException, JidelakException {
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

    public void testProcessElementHook() {
        fail("Not yet implemented");
    }

    public void testGetNextNodeNode() {
        fail("Not yet implemented");
    }

    public void testGetNextNodeNodeBoolean() {
        fail("Not yet implemented");
    }

    public void testPath() {
        fail("Not yet implemented");
    }

    public void testProcessAttributes() {
        fail("Not yet implemented");
    }

}
