package net.suteren.android.jidelak.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import net.suteren.android.jidelak.model.Availability;
import net.suteren.android.jidelak.model.Source;

public class AvailabilityMarshaller extends BaseMarshaller<Availability> {

	private Source source;

	@Override
	protected void unmarshallHelper(String prefix, Map<String, String> data,
			Availability avail) {

		Calendar cal = Calendar.getInstance(getSource().getLocale());
		String x = data.get(prefix + "term@date");
		if (x != null)
			try {
				cal.setTime(getSource().getDateFormat().parse(x));
				avail.setDay(cal.get(Calendar.DAY_OF_MONTH));
				avail.setMonth(cal.get(Calendar.MONTH) + 1);
				avail.setYear(cal.get(Calendar.YEAR));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		x = data.get(prefix + "term@day-of-week");
		if (x != null)
			try {
				DateFormat df = new SimpleDateFormat("E", getSource()
						.getLocale());
				cal.setTime(df.parse(x));
				avail.setDow(cal.get(Calendar.DAY_OF_WEEK));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		avail.setFrom(data.get(prefix + "term@from"));
		avail.setTo(data.get(prefix + "term@to"));

		avail.setClosed(Boolean.parseBoolean(data.get(prefix + "term@closed")));
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
