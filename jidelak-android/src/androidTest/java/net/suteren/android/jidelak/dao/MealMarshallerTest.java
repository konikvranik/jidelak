package net.suteren.android.jidelak.dao;

import android.test.AndroidTestCase;
import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.model.Meal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class MealMarshallerTest extends AndroidTestCase {

    private MealMarshaller sm;
    private DocumentBuilder db;

    public void setUp() throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder();
        sm = new MealMarshaller();

    }

    public void tearDown() throws Exception {
    }

    public void testUnmarshallHelperStringMapOfStringStringSource() {
        // TODO
    }

    public void testClean() {
        // TODO
    }

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

    public void testUnmarshallStringNodeT() throws JidelakException,
            SAXException, IOException {
        Meal s = new Meal();

        Document d = db.newDocument();
        prepareDocument(RestaurantMarshallerTest.prepareDocument(d));

        d = db.parse(this.getClass().getResourceAsStream("/debug.result"));

        sm.unmarshall("#document.jidelak.config.restaurant.menu",
                d.getDocumentElement(), s);

    }

    public void testProcessElementHook() {
        // TODO
    }

    public void testGetNextNodeNode() {
        // TODO
    }

    public void testGetNextNodeNodeBoolean() {
        // TODO
    }

    public void testPath() {
        // TODO
    }

    public void testProcessAttributes() {
        // TODO
    }

}
