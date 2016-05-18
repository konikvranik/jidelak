package net.suteren.android.jidelak.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import net.suteren.android.jidelak.model.Address;
import net.suteren.android.jidelak.model.Restaurant;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

public class RestaurantDao extends BaseDao<Restaurant> {

    public static final String TABLE_NAME = "restaurant";

    public static final Column NAME = new Column("name", SQLiteDataTypes.TEXT);
    public static final Column POSITION = new Column("position",
            SQLiteDataTypes.INTEGER);
    public static final Column COUNTRY = new Column("country",
            SQLiteDataTypes.TEXT);
    public static final Column CITY = new Column("city", SQLiteDataTypes.TEXT);
    public static final Column ADDRESS = new Column("address",
            SQLiteDataTypes.TEXT);
    public static final Column LONGITUDE = new Column("longitude",
            SQLiteDataTypes.REAL);
    public static final Column LATITUDE = new Column("latitude",
            SQLiteDataTypes.REAL);
    public static final Column ZIP = new Column("zip", SQLiteDataTypes.INTEGER);
    public static final Column PHONE = new Column("phone", SQLiteDataTypes.TEXT);
    public static final Column WEB = new Column("web", SQLiteDataTypes.TEXT);
    public static final Column E_MAIL = new Column("email",
            SQLiteDataTypes.TEXT);

    static {

        registerTable(TABLE_NAME);

        getTable().addColumn(ID);
        getTable().addColumn(POSITION);
        getTable().addColumn(CITY);
        getTable().addColumn(ADDRESS);
        getTable().addColumn(LONGITUDE);
        getTable().addColumn(LATITUDE);
        getTable().addColumn(ZIP);
        getTable().addColumn(COUNTRY);
        getTable().addColumn(PHONE);
        getTable().addColumn(WEB);
        getTable().addColumn(E_MAIL);
        getTable().addColumn(NAME);

    }

    public RestaurantDao(SQLiteDatabase db) {
        super(db);
    }

    public RestaurantDao() {
        super();
    }

    @Override
    public Restaurant parseRow(Cursor cursor) {
        Restaurant restaurant = new Restaurant(unpackColumnValue(cursor, ID, Long.class));
        restaurant.setName(unpackColumnValue(cursor, NAME, String.class));
        restaurant.setPosition(unpackColumnValue(cursor, POSITION, Integer.class));
        restaurant.setOpeningHours(new TreeSet<>(new AvailabilityDao(getDatabase()).findByRestaurant(restaurant)));

        Address address = new Address(Locale.getDefault());
        address.setCountryName(unpackColumnValue(cursor, COUNTRY, String.class));
        address.setLocality(unpackColumnValue(cursor, CITY, String.class));
        String add = unpackColumnValue(cursor, ADDRESS, String.class);
        if (add != null) {
            String[] addrLines = add.split("\n");
            for (int i = 0; i < addrLines.length; i++) {
                address.setAddressLine(i, addrLines[i]);
            }
        }

        Double lat = unpackColumnValue(cursor, LATITUDE, Double.class);
        if (lat != null)
            address.setLatitude(lat);
        Double lon = unpackColumnValue(cursor, LONGITUDE, Double.class);
        if (lon != null)
            address.setLongitude(lon);
        Integer zip = unpackColumnValue(cursor, ZIP, Integer.class);
        if (zip != null)
            address.setPostalCode(Integer.toString(zip));
        address.setUrl(unpackColumnValue(cursor, WEB, String.class));
        address.setPhone(unpackColumnValue(cursor, PHONE, String.class));
        Map<String, String> b = new HashMap<>();
        b.put(E_MAIL.getName(), unpackColumnValue(cursor, E_MAIL, String.class));
        address.setExtras(b);
        restaurant.setAddress(address);
        return restaurant;
    }

    @Override
    protected String getTableName() {
        return getTable().getName();
    }

    @Override
    protected String[] getColumnNames() {
        return getTable().getColumnNames();
    }

    @Override
    protected ContentValues getValues(Restaurant obj, boolean updateNull) {
        ContentValues values = new ContentValues();
        if (obj.getId() != null || updateNull)
            values.put(ID.getName(), obj.getId());
        if (obj.getName() != null || updateNull)
            values.put(NAME.getName(), obj.getName());
        if (obj.getPosition() != null || updateNull)
            values.put(POSITION.getName(), obj.getPosition());

        Address addr = obj.getAddress();
        if (addr != null) {
            StringBuffer address = null;
            if (addr.getMaxAddressLineIndex() >= 0) {
                address = new StringBuffer();
                String line = addr.getAddressLine(0);
                for (int i = 0; line != null; line = addr.getAddressLine(++i)) {
                    address.append(line);
                }
            }
            if (address != null || updateNull)
                values.put(ADDRESS.getName(), address != null ? address.toString() : null);
            if (addr.getLocality() != null || updateNull)
                values.put(CITY.getName(), addr.getLocality());
            if (addr.getCountryName() != null || updateNull)
                values.put(COUNTRY.getName(), addr.getCountryName());
            if (addr.getPostalCode() != null || updateNull)
                values.put(ZIP.getName(), addr.getPostalCode() == null ? null
                        : addr.getPostalCode().replaceAll("\\s", ""));
            if (addr.getPhone() != null || updateNull)
                values.put(PHONE.getName(), addr.getPhone());
            if (addr.getUrl() != null || updateNull)
                values.put(WEB.getName(), addr.getUrl());
            if (addr.getExtras() != null)
                if (addr.getExtras().get(E_MAIL.getName()) != null
                        || updateNull)
                    values.put(E_MAIL.getName(),
                            addr.getExtras().get(E_MAIL.getName()));
            if (addr.hasLatitude() || updateNull)
                values.put(LATITUDE.getName(),
                        addr.hasLatitude() ? addr.getLatitude() : null);
            if (addr.hasLongitude() || updateNull)
                values.put(LONGITUDE.getName(),
                        addr.hasLongitude() ? addr.getLongitude() : null);
        }
        return values;
    }

    public static Table getTable() {
        return getTable(TABLE_NAME);
    }

}
