
package com.bj4.yhh.livewallpaper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author Yen-Hsun_Huang
 */
public class Utils {
    public static final WeatherData getWeatherData(final String rawData) {
        WeatherData rtn = null;
        try {
            JSONObject data = new JSONObject(rawData).getJSONObject("query")
                    .getJSONObject("results").getJSONObject("channel");
            JSONObject location = data.getJSONObject("location");
            JSONObject condition = data.getJSONObject("item").getJSONObject("condition");
            String city = location.getString("city");
            String country = location.getString("country");
            String text = condition.getString("text");
            float temp = condition.getInt("temp");
            int code = condition.getInt("code");
            rtn = new WeatherData(code, temp, text, city, country);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    public static final String generateWeatherFromYqlResult(final long woeid) {
        return "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%3D"
                + woeid
                + "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    }

    public static final long getWoeidFromYqlResult(final String rawData) {
        try {
            JSONObject data = new JSONObject(rawData).getJSONObject("query")
                    .getJSONObject("results").getJSONObject("Result");
            return data.getLong("woeid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static final String generateCurrentLocationYqlUrl(final double longtitude,
            final double latitude) {
        return "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.placefinder%20where%20text%3D%22"
                + longtitude
                + "%2C"
                + latitude
                + "%22%20and%20gflags%3D%22R%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    }

    @SuppressWarnings("deprecation")
    public static String parseOnInternet(String url) {
        URL u;
        InputStream is = null;
        DataInputStream dis;
        String s;
        StringBuilder sb = new StringBuilder();
        try {
            u = new URL(url);
            is = u.openStream();
            dis = new DataInputStream(new BufferedInputStream(is));
            while ((s = dis.readLine()) != null) {
                sb.append(s);
            }
        } catch (Exception e) {
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ioe) {
            }
        }
        return sb.toString();
    }

    public static String readFromFile(String filePath) {
        String ret = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            ret = sb.toString();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static boolean writeToFile(final String filePath, final String data) {
        Writer writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),
                    "utf-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getWeatherIcon(int weatherCode) {
        int rtn = 3200;
        switch (weatherCode) {
            case 0:
                rtn = R.drawable.w00;
                break;
            case 1:
                rtn = R.drawable.w01;
                break;
            case 2:
                rtn = R.drawable.w02;
                break;
            case 3:
                rtn = R.drawable.w03;
                break;
            case 4:
                rtn = R.drawable.w04;
                break;
            case 5:
                rtn = R.drawable.w05;
                break;
            case 6:
                rtn = R.drawable.w06;
                break;
            case 7:
                rtn = R.drawable.w07;
                break;
            case 8:
                rtn = R.drawable.w08;
                break;
            case 9:
                rtn = R.drawable.w09;
                break;
            case 10:
                rtn = R.drawable.w10;
                break;
            case 11:
                rtn = R.drawable.w11;
                break;
            case 12:
                rtn = R.drawable.w12;
                break;
            case 13:
                rtn = R.drawable.w13;
                break;
            case 14:
                rtn = R.drawable.w14;
                break;
            case 15:
                rtn = R.drawable.w15;
                break;
            case 16:
                rtn = R.drawable.w16;
                break;
            case 17:
                rtn = R.drawable.w17;
                break;
            case 18:
                rtn = R.drawable.w18;
                break;
            case 19:
                rtn = R.drawable.w19;
                break;
            case 20:
                rtn = R.drawable.w20;
                break;
            case 21:
                rtn = R.drawable.w21;
                break;
            case 22:
                rtn = R.drawable.w22;
                break;
            case 23:
                rtn = R.drawable.w23;
                break;
            case 24:
                rtn = R.drawable.w24;
                break;
            case 25:
                rtn = R.drawable.w25;
                break;
            case 26:
                rtn = R.drawable.w26;
                break;
            case 27:
                rtn = R.drawable.w27;
                break;
            case 28:
                rtn = R.drawable.w28;
                break;
            case 29:
                rtn = R.drawable.w29;
                break;
            case 30:
                rtn = R.drawable.w30;
                break;
            case 31:
                rtn = R.drawable.w31;
                break;
            case 32:
                rtn = R.drawable.w32;
                break;
            case 33:
                rtn = R.drawable.w33;
                break;
            case 34:
                rtn = R.drawable.w34;
                break;
            case 35:
                rtn = R.drawable.w35;
                break;
            case 36:
                rtn = R.drawable.w35;
                break;
            case 37:
                rtn = R.drawable.w37;
                break;
            case 38:
                rtn = R.drawable.w38;
                break;
            case 39:
                rtn = R.drawable.w39;
                break;
            case 40:
                rtn = R.drawable.w40;
                break;
            case 41:
                rtn = R.drawable.w41;
                break;
            case 42:
                rtn = R.drawable.w42;
                break;
            case 43:
                rtn = R.drawable.w43;
                break;
            case 44:
                rtn = R.drawable.w44;
                break;
            case 45:
                rtn = R.drawable.w45;
                break;
            case 46:
                rtn = R.drawable.w46;
                break;
            case 47:
                rtn = R.drawable.w47;
                break;
            default:
                rtn = R.drawable.w00;
                break;
        }
        return rtn;
    }
}
