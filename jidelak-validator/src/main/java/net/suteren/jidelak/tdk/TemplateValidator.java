package net.suteren.jidelak.tdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.dao.RestaurantMarshaller;
import net.suteren.android.jidelak.model.Restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TemplateValidator {

	private Restaurant restaurant;
	private File template;
	private static Logger log = LoggerFactory
			.getLogger(TemplateValidator.class);

	public TemplateValidator(File template) {
		this.template = template;
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		File template = null;
		try {
			TemplateValidator tv = new TemplateValidator(template);
			tv.validateImport();
			tv.validateRun();
		} catch (JidelakException e) {

		}
	}

	private void validateImport() throws JidelakException {
		restaurant = new Restaurant();

		FileInputStream fileStream = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document d = db.newDocument();

			Node n = d.appendChild(d.createElement("jidelak"));
			n.appendChild(d.createElement("config"));
			Transformer tr = TransformerFactory.newInstance()
					.newTransformer(
							new StreamSource(fileStream = new FileInputStream(
									template)));
			DOMResult res = new DOMResult(DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument());
			tr.transform(new DOMSource(d), res);

			try {
				StringWriter sw = new StringWriter();
				StreamResult deb = new StreamResult(sw);
				tr = TransformerFactory.newInstance().newTransformer();
				tr.transform(new DOMSource(res.getNode()), deb);
				log.debug(sw.toString());
				sw.close();
			} catch (Throwable e) {
				if (e instanceof JidelakException)
					throw (JidelakException) e;
				else
					throw new JidelakException("string.unexpected_exception", e);
			}

			RestaurantMarshaller rm = new RestaurantMarshaller();
			// rm.setSource(source);
			rm.unmarshall("#document.jidelak.config", res.getNode(), restaurant);

		} catch (ParserConfigurationException e) {
			throw new JidelakException("string.parser_configuration_exception",
					e);
		} catch (TransformerConfigurationException e) {
			throw new JidelakException(
					"string.transformer_configuration_exception", e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new JidelakException(
					"string.transformer_factory_configuration_exception", e);
		} catch (TransformerException e) {
			throw new JidelakException("string.transformer_exception", e);
		} catch (FileNotFoundException e) {
			throw new JidelakException("string.transformer_exception", e);
		} finally {
			try {
				if (fileStream != null)
					fileStream.close();
			} catch (IOException e) {
				throw new JidelakException("string.unexpected_exception", e);
			}
		}
		// TODO Auto-generated method stub

	}

	private void validateRun() {
		// TODO Auto-generated method stub

	}

}
