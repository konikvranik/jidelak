package net.suteren.android.jidelak.dao;

import java.util.Map;

import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;

import org.w3c.dom.Element;

import android.location.Address;
import android.os.Bundle;

public class RestaurantMarshaller extends BaseMarshaller<Restaurant> {

	private Source source;

	@Override
	protected void unmarshallHelper(String prefix, Map<String, String> data,
			Restaurant restaurant) {

		restaurant.setName(data.get(prefix + "restaurant.name"));

		Address addr = new Address(source.getLocale());
		String addrString = data.get(prefix + "restaurant.address");
		String[] addrLines = addrString.split("\n");
		for (int i = 0; i < addrLines.length; i++) {
			addr.setAddressLine(i, addrLines[i]);
		}
		addr.setCountryName(data.get("restaurant.country"));
		addr.setLocality(data.get("restaurant.city"));
		addr.setPostalCode(data.get("restaurant.zip"));
		addr.setPhone(data.get("restaurant.phone"));
		addr.setUrl(data.get("restaurant.web"));
		Bundle e = new Bundle();
		e.putString(RestaurantDao.E_MAIL.getName(),
				data.get("restaurant.e-mail"));
		addr.setExtras(e);
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
