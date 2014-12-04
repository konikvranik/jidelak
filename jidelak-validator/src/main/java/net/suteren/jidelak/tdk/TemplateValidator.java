package net.suteren.jidelak.tdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.suteren.android.jidelak.Utils;
import net.suteren.android.jidelak.dao.RestaurantMarshaller;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;

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
		new TemplateValidator(new File(args[0])).validateImport();
	}

	private void validateImport() throws Exception {

		TransformerFactory trf = TransformerFactory.newInstance();
		Transformer tr = trf.newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");

		RestaurantMarshaller rm = new RestaurantMarshaller();
		restaurant = new Restaurant();

		log.info(String.format("Parsing template %s", this.template));
		FileInputStream template = openFileInput(this.template);
		Utils.parseConfig(template, restaurant);
		log.info(String.format("Template %s parsed", this.template));

		log.info("Processing sources...");
		Set<Source> sources = restaurant.getSource();
		for (Source source : sources) {

			log.info(String.format("Processing source %s", source.getUrl()));

			template = openFileInput(this.template);
			
			log.info(String.format("Retrieving %s", source.getUrl()));
			Node result = Utils.retrieve(source, template);
			log.info(String.format("Retrieved %s", source.getUrl()));
			
			StringWriter sw = new StringWriter();
			tr.transform(new DOMSource(result), new StreamResult(sw));

			log.info("===================================================================================");
			log.info(sw.toString());
			log.info("===================================================================================");

			log.info(String.format("Unmarshalling %s", source.getUrl()));
			rm.unmarshall("#document.jidelak.config", result, restaurant);
			log.info(String.format("Unmarshalled %s", source.getUrl()));

			log.info(restaurant.toString());
			for (Meal meal : restaurant.getMenu()) {
				log.info(meal.toString());
				log.info("-----------------------------------------------------------------------------------");
			}
			log.info("===================================================================================");
			
			log.info(String.format("Source %s processed", source.getUrl()));
		}

	}

	private FileInputStream openFileInput(File templateName)
			throws FileNotFoundException {
		return new FileInputStream(templateName);
	}

}
