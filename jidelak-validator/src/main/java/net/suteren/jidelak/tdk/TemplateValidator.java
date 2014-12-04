package net.suteren.jidelak.tdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
		File template = null;
		TemplateValidator tv = new TemplateValidator(template);
		tv.validateImport();
	}

	private void validateImport() throws Exception {

		TransformerFactory trf = TransformerFactory.newInstance();
		Transformer tr = trf.newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");

		InputStream template = openFileInput(this.template);

		restaurant = new Restaurant();
		Utils.parseConfig(template, restaurant);
		Set<Source> sources = restaurant.getSource();
		RestaurantMarshaller rm = new RestaurantMarshaller();
		for (Source source : sources) {
			Node result = Utils.retrieve(source, template);
			StringWriter sw = new StringWriter();
			tr.transform(new DOMSource(result), new StreamResult(sw));

			log.info("===================================================================================");
			log.info(sw.toString());
			log.info("===================================================================================");

			rm.unmarshall("#document.jidelak.config", result, restaurant);
			Set<Availability> avs = new HashSet<Availability>();

			log.info(restaurant.toString());
			for (Meal meal : restaurant.getMenu()) {
				log.info(meal.toString());
				log.info("-----------------------------------------------------------------------------------");
			}
			log.info("===================================================================================");
		}

	}

	private InputStream openFileInput(File templateName)
			throws FileNotFoundException {
		return new FileInputStream(templateName);
	}

}
