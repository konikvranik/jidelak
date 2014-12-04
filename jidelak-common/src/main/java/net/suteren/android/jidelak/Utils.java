package net.suteren.android.jidelak;

import java.util.Locale;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(Utils.class);

	public Utils() {
	}

	public static Locale stringToLocale(String s) {
		if (s == null)
			return null;
		StringTokenizer tempStringTokenizer = new StringTokenizer(s, "_");
		String l = null;
		if (tempStringTokenizer.hasMoreTokens())
			l = (String) tempStringTokenizer.nextElement();
		String c = null;
		if (tempStringTokenizer.hasMoreTokens())
			c = (String) tempStringTokenizer.nextElement();
		return new Locale(l, c);
	}

	public static <T extends Comparable<T>> int compare(T o1, T o2) {
		if (o1 == null) {
			if (o2 == null)
				return 0;
			else
				return -1;
		} else {
			if (o2 == null)
				return 1;
			else
				return o1.compareTo((T) o2);
		}
	}

}