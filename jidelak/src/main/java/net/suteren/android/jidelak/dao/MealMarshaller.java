package net.suteren.android.jidelak.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import android.util.Log;
import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Meal;
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

					Log.d(getSource().getTimeType().name(),
							"Before: "
									+ new SimpleDateFormat("EE, yyyy-MM-dd")
											.format(cal.getTime()));

					cal.add(getSource().getOffsetBase().getType(),
							Integer.parseInt(x));

					Log.d(getSource().getTimeType().name(),
							"After + "
									+ x
									+ ": "
									+ new SimpleDateFormat("EE, yyyy-MM-dd")
											.format(cal.getTime()));

				}
				break;

			case ABSOLUTE:
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
