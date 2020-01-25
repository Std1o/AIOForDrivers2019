package com.beerdelivery.driver.helper;

/**
 * Created by BeerDelivery on 01.12.2019.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "taxiPref";

    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_MOBILE_NUMBER = "phone_number";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static final String KEY_HASH = "hash";

    private static final String KEY_NAME = "name";
    private static final String KEY_FAMALY = "famaly";

    private static final String KEY_DRIVER_ID = "driver_id";

    private static final String KEY_CAR_MARKA = "car_marka";
    private static final String KEY_CAR_NUMBER = "car_number";
    private static final String KEY_CAR_COLOR = "car_color";

    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_CITY = "city";

    private static final String KEY_IS_CITY = "isCity";

    private static final String KEY_CITY_URL = "urlCity";

    private static final String KEY_CITY_ID = "idCity";


    private static final String MIN_PRICE = "minPrice";
    private static final String PRICE_FOR_KM = "price_for_km";
    private static final String MIN_KM = "min_km";
    private static final String MINUT_PRICE = "minut_price";


    private static final String KEY_GCM = "gcm";

    private static final String KEY_PREORDERS = "preorders";

    private static final String IMG_USER = "img_user";
    private static final String IMG_CAR = "img_car";
    private static final String IMG_PRAVA = "img_prava";

    private static final String KEY_IS_FIRST_START = "isFirstStart";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setDriverId(String m) {
        editor.putString(KEY_DRIVER_ID, m);
        editor.commit();
    }








    public void setGcm(String m) {
        editor.putString(KEY_GCM, m);
        editor.commit();
    }
    public String getGsm() {
        return pref.getString(KEY_GCM, null);
    }


    public void setIsFirstStart(boolean m) {
        editor.putBoolean(KEY_IS_FIRST_START, m);
        editor.commit();
    }
    public boolean getIsFirstStart() {
        return pref.getBoolean(KEY_IS_FIRST_START, true);
    }


    public void setIsWaitingForSms(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForSms() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_MOBILE, mobileNumber);
        editor.commit();
    }

    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE, null);
    }

    public void createDriver(String name, String famaly,  String marka, String number,
                             String color) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_FAMALY, famaly);
        editor.putString(KEY_CAR_MARKA, marka);
        editor.putString(KEY_CAR_NUMBER, number);
        editor.putString(KEY_CAR_COLOR, color);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getDriverDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("name", pref.getString(KEY_NAME, null));
        profile.put("phone", pref.getString(KEY_MOBILE, null));
        profile.put("famaly", pref.getString(KEY_FAMALY, null));
        profile.put("marka", pref.getString(KEY_CAR_MARKA, null));
        profile.put("number", pref.getString(KEY_CAR_NUMBER, null));
        profile.put("color", pref.getString(KEY_CAR_COLOR, null));

        return profile;
    }

    public void setDriverCity(String city, String idCity, String urlCity) {
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_CITY_ID, idCity);
        editor.putString(KEY_CITY_URL, urlCity);
        editor.putBoolean(KEY_IS_CITY, true);

        editor.commit();
    }

    public void setTaxiUrl(String urlCity) {
        editor.putString(KEY_CITY_URL, urlCity);
        editor.commit();
    }

    public String getDriverName() {
        return pref.getString(KEY_NAME, null) + " " + pref.getString(KEY_FAMALY, null);
    }

    public String getCityUrl() {
        return pref.getString(KEY_CITY_URL, null);
    }

    public String getDriverId() {
        return pref.getString(KEY_DRIVER_ID, null);
    }

    public String getDriverCity() {
        return pref.getString(KEY_CITY, null);


    }


    public boolean isCityIn() {
        return pref.getBoolean(KEY_IS_CITY, false);
    }


    public void createFreeTarif(String price, String price_for_km, String min_km, String minut_price) {
        editor.putString(MIN_PRICE, price);
        editor.putString(PRICE_FOR_KM, price_for_km);
        editor.putString(MIN_KM, min_km);
        editor.putString(MINUT_PRICE, minut_price);
        editor.commit();
    }


    public HashMap<String, String> getFreeTarif() {
        HashMap<String, String> profile = new HashMap<>();

        profile.put("min_price", pref.getString(MIN_PRICE, null));
        profile.put("price_for_km", pref.getString(PRICE_FOR_KM, null));
        profile.put("min_km", pref.getString(MIN_KM, null));
        profile.put("minut_price", pref.getString(MINUT_PRICE, null));

        return profile;
    }


    public String getHash() {
        return pref.getString(KEY_HASH, null);
    }

    public void setHash(String u) {
        editor.putString(KEY_HASH, u);
        editor.commit();
    }


    public String getPreOrders() {
        return pref.getString(KEY_PREORDERS, null);
    }

    public void setPreOrders(String u) {
        editor.putString(KEY_PREORDERS, u);
        editor.commit();
    }

}