package net.suteren.android.jidelak.dao;

import java.util.Map;

import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import android.location.Address;
import android.os.Bundle;

public class RestaurantMarshaller extends BaseMarshaller<Restaurant> {

	private Source source;
	private static Logger log = LoggerFactory
			.getLogger(RestaurantMarshaller.class);

	@Override
	protected void unmarshallHelper(String prefix, Map<String, String> data,
			Restaurant restaurant) {

		if (log.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			for (String key : data.keySet()) {
				sb.append("\t");
				sb.append(key);
				sb.append("=>");
				sb.append(data.get(key));
				sb.append("\n");
			}
			log.debug("Parsed keys:\n" + sb.toString());
		}

		String key = prefix + "restaurant.name";

		restaurant.setName(data.get(key));
		log.debug("Name: " + data.get(key));

		Address addr = new Address(source.getLocale());
		key = prefix + "restaurant.address";
		String addrString = data.get(key);
		String[] addrLines = addrString.split("\n");
		for (int i = 0; i < addrLines.length; i++) {
			addr.setAddressLine(i, addrLines[i]);
		}

		key = prefix + "restaurant.country";
		addr.setCountryName(data.get(key));
		log.debug("Country: " + data.get(key));

		key = prefix + "restaurant.city";
		addr.setLocality(data.get(key));
		log.debug("Locality: " + data.get(key));

		key = prefix + "restaurant.zip";
		addr.setPostalCode(data.get(key));
		log.debug("ZIP: " + data.get(key));

		key = prefix + "restaurant.phone";
		addr.setPhone(data.get(key));
		log.debug("Phone: " + data.get(key));

		key = prefix + "restaurant.web";
		addr.setUrl(data.get(key));

		key = prefix + "restaurant.e-mail";
		Bundle e = new Bundle();
		e.putString(RestaurantDao.E_MAIL.getName(), data.get(key));
		addr.setExtras(e);

		log.debug("addr: " + addr.toString());

		restaurant.setAddress(addr);
	}

	@Override
	protected boolean processElementHook(Element n, Restaurant restaurant)
			throws JidelakException {

		if ("source".equals(n.getNodeName())) {
			source = new Source();
			source.setRestaurant(restaurant);
			restaurant.addSource(source);

			new SourceMarshaller().unmarshall(n, source);
			return false;
		} else if ("meal".equals(n.getNodeName())) {

			Meal meal = new Meal();
			meal.setRestaurant(restaurant);
			meal.setSource(source);
			restaurant.addMenu(meal);

			MealMarshaller mm = new MealMarshaller();
			mm.setSource(source);
			mm.unmarshall(n, meal);
			return false;
		} else if ("term".equals(n.getNodeName())
				&& "open".equals(n.getParentNode().getNodeName())) {
			Availability avail = new Availability();
			avail.setRestaurant(restaurant);

			AvailabilityMarshaller am = new AvailabilityMarshaller();
			am.setSource(source);
			am.unmarshall(n, avail);

			restaurant.addOpeningHours(avail);

			return false;
		}
		return true;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Source getSource() {
		return source;
	}
}
