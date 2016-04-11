package net.suteren.jidelak.tdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.suteren.android.jidelak.Utils;
import net.suteren.android.jidelak.dao.RestaurantMarshaller;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;

import org.apache.log4j.Level;
import org.apache.pdfbox.util.PDFStreamEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class TemplateValidator {

	private Restaurant restaurant;
	private File template;
	private static Logger log = LoggerFactory
			.getLogger(TemplateValidator.class);

	public TemplateValidator(File template) {
		this.template = template;
	}

	public static void main(String[] args) throws Exception {

		int templatePos = 0;

		if ("-d".equals(args[0])) {
			org.apache.log4j.Logger.getRootLogger().setLevel(Level.DEBUG);
			org.apache.log4j.Logger.getLogger(PDFStreamEngine.class).setLevel(Level.WARN);
			templatePos++;
		}

		new TemplateValidator(new File(args[templatePos])).validateImport();
	}

	private void validateImport() throws Exception {

		TransformerFactory trf = TransformerFactory.newInstance();
		Transformer tr = trf.newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");

		RestaurantMarshaller rm = new RestaurantMarshaller();
		restaurant = new Restaurant();

		log.debug(String.format("Parsing template %s", this.template));
		FileInputStream template = openFileInput(this.template);
		Utils.parseConfig(template, restaurant);
		log.debug(String.format("Template %s parsed", this.template));

		log.debug("Processing sources...");
		Set<Source> sources = restaurant.getSource();
		for (Source source : sources) {

			log.debug(String.format("Processing source %s", source.getUrl()));

			template = openFileInput(this.template);

			log.debug(String.format("Retrieving %s", source.getUrl()));
			Node result = Utils.retrieve(source, template);
			log.debug(String.format("Retrieved %s", source.getUrl()));

			StringWriter sw = new StringWriter();
			tr.transform(new DOMSource(result), new StreamResult(sw));

			log.debug("===================================================================================");
			log.debug(sw.toString());
			log.debug("===================================================================================");

			log.debug(String.format("Unmarshalling %s", source.getUrl()));
			rm.unmarshall("#document.jidelak.config", result, restaurant);
			log.debug(String.format("Unmarshalled %s", source.getUrl()));
			log.debug("===================================================================================");

			log.info(restaurant.toString());
			

			log.debug(String.format("Source %s processed", source.getUrl()));
		}

	}

	private FileInputStream openFileInput(File templateName)
			throws FileNotFoundException {
		return new FileInputStream(templateName);
	}

}
