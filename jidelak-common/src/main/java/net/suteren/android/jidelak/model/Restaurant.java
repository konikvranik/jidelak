package net.suteren.android.jidelak.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Restaurant implements Identificable<Restaurant> {

    private static final long serialVersionUID = -7093047343111371686L;
    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(Restaurant.class);
    private String name;
    private Address address;
    private Integer position;

    public Restaurant(Long id) {
        setId(id);
    }

    public Restaurant() {
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    SortedSet<Availability> openingHours;
    SortedSet<Meal> menu = new TreeSet<Meal>();
    private Long id;
    private Set<Source> source;
    private String version;
    private String code;

    public SortedSet<Availability> getOpeningHours() {
        return openingHours;
    }

    public Set<Availability> getOpeningHours(Calendar day) {
        Set<Availability> av = new TreeSet<Availability>();

        for (Availability availability : openingHours) {
            if (testDay(day, availability)) {
                if (availability.isClosed()) {
                    av = new TreeSet<Availability>();
                    av.add(availability);
                    return av;
                }
                av.add(availability);
            }
        }
        return av;
    }

    public static boolean testDay(Calendar day, Availability availability) {

        if (day == null)
            return true;

        if (availability == null)
            return false;

        if (availability.getDay() != null
                && day.get(Calendar.DAY_OF_MONTH) != availability.getDay())
            return false;

        if (availability.getMonth() != null
                && day.get(Calendar.MONTH) + 1 != availability.getMonth())
            return false;

        if (availability.getYear() != null
                && day.get(Calendar.YEAR) != availability.getYear())
            return false;

        if (availability.getDow() != null
                && day.get(Calendar.DAY_OF_WEEK) != availability.getDow())
            return false;

        return true;
    }

    public void setOpeningHours(SortedSet<Availability> openingHours) {
        this.openingHours = openingHours;
    }

    public void addOpeningHours(Availability oh) {
        if (openingHours == null)
            openingHours = new TreeSet<Availability>();
        openingHours.add(oh);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMenu(SortedSet<Meal> menu) {
        this.menu = menu;
    }

    public SortedSet<Meal> getMenu() {
        return menu;
    }

    public void addMenu(Meal meal) {
        if (menu == null)
            menu = new TreeSet<Meal>();
        menu.add(meal);
    }

    public void addMenuAll(Collection<Meal> meal) {
        if (menu == null)
            menu = new TreeSet<Meal>();
        menu.addAll(meal);
    }

    public SortedSet<Meal> getMenu(Calendar day) {
        SortedSet<Meal> dailyMenu = new TreeSet<Meal>();

        for (Meal meal : menu) {
            if (testDay(day, meal.getAvailability()))
                dailyMenu.add(meal);
        }
        return dailyMenu;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Source> getSource() {
        return source;
    }

    public void setSource(Set<Source> source) {
        this.source = source;
    }

    public void addSource(Source source) {
        if (this.source == null)
            this.source = new HashSet<Source>();
        this.source.add(source);
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getTemplateName() {
        return "restaurant-" + getId() + ".template.xsl";
    }

    @Override
    public int compareTo(Restaurant another) {

        if (another == null)
            return 1;

        int r = getPosition() != null ? getPosition().compareTo(
                another.getPosition() == null ? 0 : another.getPosition())
                : (another.getPosition() == null ? 0 : -1);
        if (r != 0)
            return r;

        r = getId() != null ? getId().compareTo(another.getId()) : (another
                .getId() == null ? 0 : -1);
        if (r != 0)
            return r;

        r = getName() != null ? getName().compareTo(another.getName())
                : (another.getName() == null ? 0 : -1);
        if (r != 0)
            return r;

        return r;
    }

    public void setCode(String string) {
        code = string;
    }

    public String getCode() {
        return code;
    }

    public void setVersion(String string) {
        version = string;
    }

    public String getVersion() {
        return version;
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
        StringBuffer sb = new StringBuffer("\n== BEGIN Restaurant ==\n");
        appendKey(sb, "name", getName());
        if (getId() != null)
            appendKey(sb, "id", String.valueOf(getId()));
        appendKey(sb, "code", getCode());
        appendKey(sb, "position", String.valueOf(getPosition()));
        appendKey(sb, "version", getVersion());
        appendKey(sb, "template name", getTemplateName());

        sb.append("\n -- SOURCE:\n");
        for (Source s : getSource()) {
            sb.append(s.toString());
            sb.append("\n");
        }

        sb.append("\n -- ADDRESS:\n");
        sb.append(getAddress().toString());
        sb.append("\n");

        sb.append("\n -- OPENING HOURS:\n");
        for (Availability s : getOpeningHours()) {
            sb.append(s.toString());
            sb.append("\n");
        }

        sb.append("\n -- MENU:\n");
        for (Meal s : getMenu()) {
            sb.append(s.toString());
            sb.append("\n");
        }

        sb.append("== END Restaurant ==");

        return sb.toString();
    }

    private void appendKey(StringBuffer sb, String key, String value) {
        if (value == null)
            return;
        sb.append(key);
        sb.append(": ");
        sb.append(value);
        sb.append("\n");

    }
}
