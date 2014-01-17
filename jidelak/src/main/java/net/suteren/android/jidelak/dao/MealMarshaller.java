package net.suteren.android.jidelak.dao;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Dish;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Source;
import android.util.Log;

public class MealMarshaller extends BaseMarshaller<Meal> {

	private static final String LOGGER_TAG = "MEalMArshaller";
	private Source source;

	@Override
	protected void unmarshallHelper(String prefix, Map<String, String> data,
			Meal meal) throws JidelakException {

		meal.setTitle(data.get(prefix + "meal.title"));
		meal.setDescription(data.get(prefix + "meal.description"));
		meal.setPrice(data.get(prefix + "meal.price"));
		meal.setCategory(data.get(prefix + "meal@category"));
		meal.setDish(Dish.valueOf(data.get(prefix + "meal@dish").toUpperCase(
				Locale.ENGLISH)));

		Log.d(LOGGER_TAG, "Dish set to: " + meal.getDish().name());

		String o = data.get(prefix + "meal@order");
		if (o != null)
			meal.setPosition(Integer.parseInt(o));

		String x = data.get(prefix + "meal@time");
		try {
			Calendar cal = Calendar.getInstance(getSource().getLocale());
			switch (getSource().getTimeType()) {
			case RELATIVE:

				if (x != null) {

					String y = data.get(prefix + "meal@ref-time");

					cal.setTime(getSource().getDateFormat().parse(y));
					cal.add(getSource().getOffsetBase().getType(), getSource()
							.getOffset());

					cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(x));

				}
				break;

			case ABSOLUTE:

				Log.d(LOGGER_TAG, "Parsing " + x + " by "
						+ getSource().getDateFormatString());
				if (x != null)
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
		if (source == null)
			return new Source();
		return source;
	}
}
