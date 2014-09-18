
package com.bj4.yhh.livewallpaper;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Yen-Hsun_Huang
 */
public class WeatherParseService extends Service {
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
                // mLongtitude = extras.getDouble(INTENT_KEY_LONGTITUDE);
                // mLatitude = extras.getDouble(INTENT_KEY_LATITUDE);
                mLongtitude = 25.946314;
                mLatitude = 83.534546;
                getWeatherData();
                return Service.START_STICKY;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void getWeatherData() {
        new WeatherDataParser(mLongtitude, mLatitude).execute();
    }

    private static class WeatherDataParser extends AsyncTask<Void, Void, Void> {
        private double mLongtitude, mLatitude;

        public WeatherDataParser(double longtitude, double latitude) {
            mLongtitude = longtitude;
            mLatitude = latitude;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("QQQQ", "getWeatherData, mLongtitude: " + mLongtitude + ", mLatitude: "
                    + mLatitude);
            final String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.placefinder%20where%20text%3D%22"
                    + mLongtitude
                    + "%2C"
                    + mLatitude
                    + "%22%20and%20gflags%3D%22R%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
            final String rawData = Utils.parseOnInternet(url);
            Log.i("QQQQ", "raw: " + rawData);
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
