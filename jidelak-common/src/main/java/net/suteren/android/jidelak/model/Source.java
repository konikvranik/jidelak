package net.suteren.android.jidelak.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import net.suteren.android.jidelak.Utils;

public class Source implements Identificable<Source> {

    private static final long serialVersionUID = -4261642590268604447L;
    private TimeType timeType;
    private TimeOffsetType offsetBase;
    private Integer firstDayOfWeek;
    private Integer offset;
    private URL url;
    private Restaurant restaurant;
    private Long id;
    private Locale locale;
    private DateFormat dateFormat;
    private String dateFormatString;
    private String encoding;
    private List<Meal> menu;
    private Calendar date;

    public TimeType getTimeType() {
        if (timeType == null)
            return TimeType.ABSOLUTE;
        return timeType;
    }

    public void setTimeType(TimeType time) {
        this.timeType = time;
    }

    public TimeOffsetType getOffsetBase() {
        return offsetBase;
    }

    public void setOffsetBase(TimeOffsetType base) {
        this.offsetBase = base;
    }

    public Integer getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setFirstDayOfWeek(String firstDayOfWeek) throws ParseException {
        DateFormat df = new SimpleDateFormat("E", getLocale());
        Calendar cal = Calendar.getInstance();
        cal.setTime(df.parse(firstDayOfWeek));
        setFirstdayofweek(cal.get(Calendar.DAY_OF_WEEK));
    }

    public void setFirstdayofweek(Integer firstdayofweek) {
        this.firstDayOfWeek = firstdayofweek;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant2) {
        this.restaurant = restaurant2;
    }

    @Override
    public Long getId() {
        // TODO Auto-generated method stub
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Locale getLocale() {
        if (locale == null)
            return Locale.getDefault();
        return locale;
    }

    public String getLocaleString() {
        return getLocale().getLanguage() + "_" + getLocale().getCountry();
    }

    public void setLocale(String locale) {
        this.locale = Utils.stringToLocale(locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public DateFormat getDateFormat() {

        if (dateFormat == null) {
            Locale l = getLocale();
            if (l == null)
                l = Locale.getDefault();
            return new SimpleDateFormat("dd.mm.yyyy", l);
        }

        return dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormatString = dateFormat;
        this.dateFormat = new SimpleDateFormat(dateFormat, getLocale());
    }

    public String getDateFormatString() {
        return dateFormatString;
    }

    public void setEncoding(String string) {
        this.encoding = string;
    }

    public String getEncoding() {
        return encoding;
    }

    public List<Meal> getMenu() {
        return menu;
    }

    public void setMenu(List<Meal> menu) {
        this.menu = menu;
    }

    public void addMenu(Meal meal) {
        if (menu == null)
            menu = new ArrayList<Meal>();
        menu.add(meal);
    }

    public void addMenuAll(Collection<Meal> meal) {
        if (menu == null)
            menu = new ArrayList<Meal>();
        menu.addAll(meal);
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    @Override
    public int compareTo(Source another) {

        int r = getId() != null ? getId().compareTo(another.getId()) : (another
                .getId() == null ? 0 : -1);
        if (r != 0)
            return r;

        r = getUrl() != null ? getUrl().toString().compareTo(
                another.getUrl().toString()) : (another.getUrl() == null ? 0
                : -1);
        if (r != 0)
            return r;

        return 0;
    }

    private void readObject(ObjectInputStream aInputStream)
            throws ClassNotFoundException, IOException {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream)
            throws IOException {
        // perform the default serialization for all non-transient, non-static
        // fields
        aOutputStream.defaultWriteObject();
        // aOutputStream.writeObject(resource);
        // aOutputStream.writeObject(args);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Source{");
        fieldToString(stringBuilder, "id", id);
        fieldToString(stringBuilder, "url", url);
        fieldToString(stringBuilder, "dateFormatString", dateFormatString);
        //fieldToString(stringBuilder, "dateFormat", dateFormat);
        fieldToString(stringBuilder, "locale", locale);
        fieldToString(stringBuilder, "encoding", encoding);
        fieldToString(stringBuilder, "date", date);
        fieldToString(stringBuilder, "firstDayOfWeek", firstDayOfWeek);
        fieldToString(stringBuilder, "timeType", timeType);
        fieldToString(stringBuilder, "offsetBase", offsetBase);
        fieldToString(stringBuilder, "offset", offset);
        //fieldToString(stringBuilder, "restaurant", restaurant);
        //fieldToString(stringBuilder, "menu", menu);
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private void fieldToString(StringBuilder stringBuilder, String name, Object value) {
        stringBuilder.append(name);
        stringBuilder.append("=");
        stringBuilder.append(value);
        stringBuilder.append(";");
    }
}
