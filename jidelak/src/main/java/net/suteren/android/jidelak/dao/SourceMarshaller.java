package net.suteren.android.jidelak.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import net.suteren.android.jidelak.JidelakException;
import net.suteren.android.jidelak.R;
import net.suteren.android.jidelak.model.Source;
import net.suteren.android.jidelak.model.TimeOffsetType;
import net.suteren.android.jidelak.model.TimeType;

public class SourceMarshaller extends BaseMarshaller<Source> {

	@Override
	protected void unmarshallHelper(String prefix, Map<String, String> data,
			Source source) throws JidelakException {

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
			throw new JidelakException(R.string.source_malformed_url,e);
		}

		x = data.get(prefix + "source@firstDayOfWeek");
		if (x != null)
			try {
				source.setFirstdayofweek(x);
			} catch (ParseException e) {
				throw new JidelakException(R.string.first_day_of_week_malformed,e);
			}

		x = data.get(prefix + "source@base");
		if (x != null)
			source.setOffsetBase(TimeOffsetType.valueOf(x
					.toUpperCase(Locale.ENGLISH)));

	}

}
