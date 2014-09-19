
package com.bj4.yhh.livewallpaper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Yen-Hsun_Huang
 */
public class WeatherParseService extends Service {
    public static final String ON_WEATHER_DATA_UPDATE = "com.bj4.yhh.livewallpaper.WeatherParseService.onweatherdataupdate";

    private static final String TAG = "WeatherParseService";

    private static final boolean DEBUG = true;

    public static final String INTENT_KEY_LONGTITUDE = "longtitude";

    public static final String INTENT_KEY_LATITUDE = "latitude";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private double mLongtitude, mLatitude;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mLongtitude = extras.getDouble(INTENT_KEY_LONGTITUDE);
                mLatitude = extras.getDouble(INTENT_KEY_LATITUDE);
                getWeatherData();
                return Service.START_STICKY;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void getWeatherData() {
        new WeatherDataParser(this, mLongtitude, mLatitude).execute();
    }

    private static class WeatherDataParser extends AsyncTask<Void, Void, Void> {
        private double mLongtitude, mLatitude;

        private Context mContext;

        public WeatherDataParser(Context context, double longtitude, double latitude) {
            mLongtitude = longtitude;
            mLatitude = latitude;
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mLongtitude != 0 && mLatitude != 0) {
                final String currentLocationUrl = Utils.generateCurrentLocationYqlUrl(mLongtitude,
                        mLatitude);
                final String currentLocationRawData = Utils.parseOnInternet(currentLocationUrl);
                if (DEBUG)
                    Log.d(TAG, currentLocationRawData);
                final long woeid = Utils.getWoeidFromYqlResult(currentLocationRawData);
                if (woeid != 0) {
                    final String weatherUrl = Utils.generateWeatherFromYqlResult(woeid);
                    final String weatherRawData = Utils.parseOnInternet(weatherUrl);
                    if (DEBUG)
                        Log.d(TAG, weatherRawData);
                    WeatherData weatherData = Utils.getWeatherData(weatherRawData);
                    if (weatherData != null) {
                        if (DEBUG)
                            Log.i(TAG, weatherData.toString());
                        WeatherData.saveWeatherData(mContext, weatherData);
                        mContext.sendBroadcast(new Intent(ON_WEATHER_DATA_UPDATE));
                    }
                }
                if (DEBUG)
                    Log.i(TAG, "woeid: " + woeid);
            }
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
