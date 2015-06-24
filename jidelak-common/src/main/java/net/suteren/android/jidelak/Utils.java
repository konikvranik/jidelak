package net.suteren.android.jidelak;

import net.suteren.android.jidelak.dao.RestaurantMarshaller;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFText2HTML;
import org.fit.cssbox.pdf.CSSBoxTree;
import org.fit.pdfdom.PDFDomTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

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
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

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
            Transformer tr = TransformerFactory.newInstance().newTransformer(new StreamSource(fileStream));
            DOMResult res = new DOMResult(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            tr.transform(new DOMSource(d), res);

            RestaurantMarshaller rm = new RestaurantMarshaller();
            // rm.setSource(source);

            rm.setUpdateOh(restaurant.getOpeningHours() == null
                    || restaurant.getOpeningHours().isEmpty());
            rm.unmarshall("#document.jidelak.config", res.getNode(), restaurant);

        } finally {
            fileStream.close();
        }
    }

    public static Node retrieve(Source source, InputStream inXsl)
            throws IOException, TransformerException,
            ParserConfigurationException, JidelakException, SAXException {

        System.setProperty("http.agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Ubuntu Chromium/43.0.2357.81 Chrome/43.0.2357.81 Safari/537.36");


        HttpURLConnection con = (HttpURLConnection) source.getUrl().openConnection();
        con.connect();
        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new JidelakException("http_error_response (%5$s): %6$s", new String[]{
                    Integer.valueOf(con.getResponseCode()).toString(),
                    con.getResponseMessage()}).setSource(source)
                    .setRestaurant(source.getRestaurant()).setHandled(true)
                    .setErrorType(ErrorType.NETWORK);
        }

        log.debug("Response code: " + con.getResponseCode());

        if (log.isDebugEnabled()) {
            InputStream dis = con.getInputStream();
            FileOutputStream fw = new FileOutputStream(new File("site.xml"));
            byte[] buffer = new byte[1024]; // Adjust if you want
            int bytesRead;
            while ((bytesRead = dis.read(buffer)) != -1) {
                fw.write(buffer, 0, bytesRead);
            }
            dis.close();
            fw.close();
            con = (HttpURLConnection) source.getUrl().openConnection();
            con.connect();
        }


        ByteArrayOutputStream debugos = null;
        if (log.isDebugEnabled()) {
            debugos = new ByteArrayOutputStream();
        }


        Document d = getDocument(con, source, debugos);
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

    private static Document getDocument(HttpURLConnection con, Source source, ByteArrayOutputStream debugos) throws
            IOException, ParserConfigurationException, SAXException, TransformerConfigurationException {
        InputStream is = con.getInputStream();

        String enc = source.getEncoding();
        if (enc == null)
            enc = con.getContentEncoding();

        if (con.getContentType().contains("pdf")) {


            PDFParser parser = new PDFParser(is);
            parser.parse();
            COSDocument cosDoc = parser.getDocument();
            PDDocument pdDoc = new PDDocument(cosDoc);

            if (false) { // PDFDomTree implementation - returns strange results, so disabled. In other case,
            // PDFText2HTML creates String from PDF and then parses it into XML so it could be memory eating.
                PDFDomTree domParser = new PDFDomTree();
                domParser.setAddMoreFormatting(false);
                domParser.setForceParsing(true);
                domParser.setShouldSeparateByBeads(false);
                domParser.setDisableGraphics(true);
                domParser.setDisableImageData(true);
                domParser.setDisableImages(true);
                domParser.setDropThreshold(0f);
                domParser.setIndentThreshold(0f);
                domParser.setAverageCharTolerance(0f);
                domParser.setSpacingTolerance(0f);
                domParser.setArticleStart("\n ARTICLE START!!! \n");
                domParser.setArticleEnd("\n ARTICLE END!!! \n");
                domParser.setParagraphStart("\n PARA START!!! \n");
                domParser.setParagraphEnd("\n PARA END!!! \n");
                domParser.setWordSeparator("\n WORD!!! \n");
                domParser.setLineSeparator("\n LINE!!! \n");

                domParser.processDocument(pdDoc);
                return domParser.getDocument();
            }

            PDFText2HTML pdfStripper = new PDFText2HTML("utf8");
            pdfStripper.setAddMoreFormatting(false);
            is = new ByteArrayInputStream(pdfStripper.getText(pdDoc).getBytes());
        }
        return getTidy(enc).parseDOM(is, debugos);

    }

    public static Tidy getTidy(String enc) {

        log.debug("Enc: " + enc);

        Tidy t = new Tidy();

        t.setInputEncoding(enc == null ? "UTF-8" : enc);
        t.setOutputEncoding("UTF-8");
//		t.setPrintBodyOnly(true); // only print the content
        // t.setXmlOut(true); // to XML
        // t.setSmartIndent(true);
        t.setBreakBeforeBR(true);
        t.setPrintBodyOnly(false);
        t.setXHTML(true);

        // t.setNumEntities(false);
        // t.setQuoteMarks(false);
        // t.setQuoteAmpersand(false);
        // t.setRawOut(true);
        // t.setHideEndTags(true);
        // t.setXmlTags(false);
        // t.setXHTML(true);
        t.setShowWarnings(log.isDebugEnabled());
        // t.setTrimEmptyElements(true);
        t.setQuiet(!log.isDebugEnabled());
        // t.setQuoteNbsp(true);
        ;
        Properties props = new Properties();

        // suppport of several HTML5 tags due to lunchtime.
        props.put("new-blocklevel-tags",
                "header,nav,section,article,aside,footer");

        t.getConfiguration().addProps(props);

        return t;
    }

    public static DOMResult transform(Document d, InputStream inXsl) throws IOException,
            TransformerFactoryConfigurationError, ParserConfigurationException, TransformerException {

        TransformerFactory trf = TransformerFactory.newInstance();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);

        Transformer tr = trf.newTransformer(new StreamSource(inXsl));
        DOMResult res = new DOMResult(dbf.newDocumentBuilder().newDocument());
        tr.transform(new DOMSource(d), res);

        return res;
    }

    public static DOMResult transform(InputStream inXsl) throws IOException, TransformerFactoryConfigurationError,
            ParserConfigurationException, TransformerException {

        TransformerFactory trf = TransformerFactory.newInstance();
        Transformer tr = trf.newTransformer();
        DOMResult res = new DOMResult();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.transform(new StreamSource(inXsl), res);
        return res;
    }
}
