
package com.bj4.yhh.livewallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author Yen-Hsun_Huang
 */
public class WeatherData {
    public int mImageCode;

    public float mTemperature;

    public String mText;

    public String mCity;

    public String mCountry;

    public WeatherData(int imageCode, float temperature, String text, String city, String country) {
        mImageCode = imageCode;
        mCity = city;
        mCountry = country;
        mTemperature = temperature;
        mText = text;
    }

    @Override
    public String toString() {
        return "mImageCode: " + mImageCode + ", mCity: " + mCity + ",mCountry: " + mCountry
                + ", mTemperature: " + mTemperature + ", mText: " + mText;
    }

    private static final String PREF_KEY = "WeatherData";

    private static final String KEY_IMAGE_CODE = "image_code";

    private static final String KEY_CITY = "city";

    private static final String KEY_COUNTRY = "country";

    private static final String KEY_TEMPERATURE = "temperature";

    private static final String KEY_TEXT = "text";

    public static final void saveWeatherData(Context context, WeatherData data) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_KEY,
                Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putInt(KEY_IMAGE_CODE, data.mImageCode);
        editor.putFloat(KEY_TEMPERATURE, data.mTemperature);
        editor.putString(KEY_COUNTRY, data.mCountry);
        editor.putString(KEY_TEXT, data.mText);
        editor.putString(KEY_CITY, data.mCity);
        editor.commit();
    }

    public static final WeatherData getWeatherData(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_KEY,
                Context.MODE_PRIVATE);
        WeatherData rtn = new WeatherData(pref.getInt(KEY_IMAGE_CODE, 0), pref.getFloat(
                KEY_TEMPERATURE, 80), pref.getString(KEY_TEXT, "Fair"), pref.getString(KEY_CITY,
                "Taipei"), pref.getString(KEY_COUNTRY, "TAIWAN"));
        return rtn;
    }
}
