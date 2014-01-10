package net.suteren.android.jidelak.dao;

import java.util.Map;

import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;

import org.w3c.dom.Element;

public class RestaurantMarshaller extends BaseMarshaller<Restaurant> {

	@Override
	protected void unmarshallHelper(String prefix, Map<String, String> data,
			Restaurant restaurant) {

		restaurant.setName(data.get(prefix + "restaurant.name"));

	}

	@Override
	protected boolean processElementHook(Element n, Restaurant restaurant) {

		if ("source".equals(n.getNodeName())) {
			Source source = new Source();
			source.setRestaurant(restaurant);
			restaurant.addSource(source);

			new SourceMarshaller().unmarshall(n, source);
			return false;
		}else if ("meal".equals(n.getNodeName())) {
			Meal meal = new Meal();
			meal.setRestaurant(restaurant);
			restaurant.addMenu(meal);

			new MealMarshaller().unmarshall(n, meal);
			return false;
		}
		return true;
	}
}
