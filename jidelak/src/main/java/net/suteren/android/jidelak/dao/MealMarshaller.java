package net.suteren.android.jidelak.dao;

import java.util.Map;

import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;
import net.suteren.android.jidelak.model.TimeType;

public class MealMarshaller extends BaseMarshaller<Meal> {

	private Source source;

	@Override
	protected void unmarshallHelper(String prefix, Map<String, String> data,
			Meal meal) {

		Restaurant restaurant = meal.getRestaurant();

		meal.setTitle(data.get(prefix + "meal.title"));
		meal.setDescription(data.get(prefix + "meal.description"));
		meal.setPrice(data.get(prefix + "meal.price"));
		meal.setCategory(data.get(prefix + "meal@category"));
		meal.setDish(data.get(prefix + "meal@dish"));

		String x = data.get(prefix + "meal@time");
		if (x != null) {
			Availability availability = new Availability();

			if (meal.getSource().getTimeType() == TimeType.RELATIVE) {

				String y = data.get(prefix + "meal@ref-time");

				if (y != null) {
				}
			}

		}
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Source getSource() {
		return source;
	}
}
