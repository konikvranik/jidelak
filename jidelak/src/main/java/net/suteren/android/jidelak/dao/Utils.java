package net.suteren.android.jidelak.dao;

import java.util.Locale;
import java.util.StringTokenizer;

public class Utils {
	
	public Utils() {
	}
	public static Locale stringToLocale(String s) {
		StringTokenizer tempStringTokenizer = new StringTokenizer(s, "_");
		String l = null;
		if (tempStringTokenizer.hasMoreTokens())
			l = (String) tempStringTokenizer.nextElement();
		String c = null;
		if (tempStringTokenizer.hasMoreTokens())
			c = (String) tempStringTokenizer.nextElement();
		return new Locale(l, c);
	}
}