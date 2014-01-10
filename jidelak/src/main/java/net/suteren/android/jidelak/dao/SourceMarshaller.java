package net.suteren.android.jidelak.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import net.suteren.android.jidelak.model.Source;
import net.suteren.android.jidelak.model.TimeType;

public class SourceMarshaller extends BaseMarshaller<Source> {

	@Override
	protected void unmarshallHelper(String prefix, Map<String, String> data,
			Source source) {

		source.setLocale(data.get(prefix + "source@locale"));

		String x = data.get(prefix + "source@dateFormat");
		if (x != null) {
			source.setDateFormat(x);
		}

		source.setEncoding(data.get(prefix + "source@encoding"));

		x = data.get(prefix + "source@timeOffset");
		if (x != null)
			source.setOffset(Integer.parseInt(x));

		x = data.get(prefix + "source@time");
		if (x != null)
			source.setTimeType(TimeType.valueOf(x.toUpperCase(Locale.ENGLISH)));
		try {
			source.setUrl(new URL(data.get(prefix + "source@url")));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		}

		x = data.get(prefix + "source@firstDayOfWeek");
		if (x != null)
			try {
				source.setFirstdayofweek(x);
			} catch (ParseException e1) {
				e1.printStackTrace();
				// TODO Auto-generated catch block
			}

		x = data.get(prefix + "source@base");
		if (x != null)
			source.setBaseDate(x);

	}

}
