package net.suteren.android.jidelak.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import net.suteren.android.jidelak.model.Source;
import net.suteren.android.jidelak.model.TimeType;

public class SourceMarshaller extends BaseMarshaller<Source> {

	@Override
	protected void unmarshallHelper(String prefix, Map<String, String> data,
			Source source) {

		if (source == null)
			source = new Source();

		source.setLocale(data.get(prefix + "source@locale"));

		DateFormat df = new SimpleDateFormat(data.get(prefix
				+ "source@dateFormat"), source.getLocale());

		source.setDateFormat(df);
		source.setEncoding(data.get(prefix + "source@encoding"));

		source.setOffset(Integer.parseInt(data
				.get(prefix + "source@timeOffset")));
		source.setTimeType(TimeType.valueOf(data.get(prefix + "source@time")
				.toUpperCase(Locale.ENGLISH)));
		try {
			source.setUrl(new URL(data.get(prefix + "source@url")));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		}

		source.setFirstdayofweek(Integer.parseInt(data.get(prefix
				+ "source@firstDayOfWeek")));

		try {
			Calendar cal = Calendar.getInstance(Locale.getDefault());
			cal.setTime(df.parse(data.get(prefix + "source@base")));
			source.setBaseDate(cal);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}

	}

}
