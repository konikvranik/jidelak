package net.suteren.android.jidelak.dao;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.JidelakParseException;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Dish;
import net.suteren.android.jidelak.model.Meal;
import net.suteren.android.jidelak.model.Source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MealMarshaller extends BaseMarshaller<Meal> {

	private Source source;
	private static Logger log = LoggerFactory.getLogger(MealMarshaller.class);

	@Override
	protected void unmarshallHelper(String prefix, Map<String, String> data,
			Meal meal) throws JidelakException {

		meal.setTitle(data.get(prefix + "meal.title"));
		meal.setDescription(data.get(prefix + "meal.description"));
		meal.setPrice(data.get(prefix + "meal.price"));
		meal.setCategory(data.get(prefix + "meal@category"));
		String dishString = data.get(prefix + "meal@dish");
		try {
			if (dishString != null && !"".equals(dishString.trim()))
				meal.setDish(Dish.valueOf(dishString
						.toUpperCase(Locale.ENGLISH)));

			log.debug("Dish set to: " + meal.getDish().name());
		} catch (IllegalArgumentException e) {
			throw new JidelakException(R.string.invalid_dish, e).setMeal(meal)
					.setRestaurant(meal.getRestaurant())
					.setSource(meal.getSource())
					.setArgs(new String[] { dishString }).setHandled(true);
		}
		String o = data.get(prefix + "meal@order");
		if (o != null)
			meal.setPosition(Integer.parseInt(o));

		String x = data.get(prefix + "meal@time");
		try {
			Calendar cal = Calendar.getInstance(getSource().getLocale());
			cal.setTimeInMillis(System.currentTimeMillis());
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

				log.debug("Parsing " + x + " by "
						+ getSource().getDateFormatString());
				if (x != null)
					cal.setTime(getSource().getDateFormat().parse(x));
				break;

			default:
				break;
			}

			meal.setAvailability(new Availability(cal));

		} catch (ParseException e) {
			throw new JidelakParseException(R.string.meal_invalid_date_format,
					getSource().getDateFormatString(), x, e).setMeal(meal)
					.setRestaurant(meal.getRestaurant())
					.setSource(meal.getSource()).setHandled(true);
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
