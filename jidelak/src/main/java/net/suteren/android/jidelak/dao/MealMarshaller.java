package net.suteren.android.jidelak.dao;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Restaurant;
import net.suteren.android.jidelak.model.Source;

public class MealMarshaller extends BaseMarshaller<Meal> {

	private Source source;

	@Override
	protected void unmarshallHelper(String prefix, Map<String, String> data,
			Meal meal) throws JidelakException {

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

					String y = data.get(prefix + "meal@ref-time");

					cal.setTime(getSource().getDateFormat().parse(y));

					cal.add(getSource().getOffsetBase().getType(),
							Integer.parseInt(x));

				}
				break;

			case ABSOLUTE:
				cal.setTime(getSource().getDateFormat().parse(x));
				break;

			default:
				break;
			}
			meal.setAvailability(new Availability(cal));

		} catch (ParseException e) {
			throw new JidelakException(e);
		}

	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Source getSource() {
		return source;
	}
}
