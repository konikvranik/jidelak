package net.suteren.android.jidelak.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

		try {
			String x = data.get(prefix + "meal@time");
			Calendar cal = Calendar.getInstance(getSource().getLocale());
			switch (getSource().getTimeType()) {
			case RELATIVE:

				if (x != null) {
					Availability availability = new Availability();

					String y = data.get(prefix + "meal@ref-time");

					cal.setTime(getSource().getDateFormat().parse(y));
					
					switch (source.getOffsetBase()) {
					case value:
						
						break;

					default:
						break;
					}

					if (y != null) {
						source.getOffsetBase();

					}

				}

			case ABSOLUTE:

				break;

			default:
				break;
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Source getSource() {
		return source;
	}
}
