package net.suteren.android.jidelak.dao;

import java.util.Map;

import net.suteren.android.jidelak.model.Restaurant;

public class RestaurantMarshaller extends BaseMarshaller<Restaurant> {

	@Override
	protected Restaurant marshallHelper(String prefix, Map<String, String> data) {

		if (prefix == null)
			prefix = "";
		if (prefix.length() > 0 && prefix.charAt(prefix.length()) != '.')
			prefix += ".";

		Restaurant restaurant = new Restaurant();
		restaurant.setName(data.get(prefix + "restaurant.name"));

		return restaurant;
	}

}
