
package com.bj4.yhh.livewallpaper;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * @author Yen-Hsun_Huang
 */
public class TechLinesSettings extends Activity {
    private SeekBar mWormLength, mWormCount, mWormWidth, mWormSpeed;

    public static final String PREF_FILE = "settings_pref";

    public static final String PREF_WORM_LENGTH = "pref_worm_length";

    public static final int DEFAULT_WORM_LENGTH = 150;

    public static final String PREF_WORM_COUNT = "pref_worm_count";

    public static final int DEFAULT_WORM_COUNT = 50;

    public static final String PREF_WORM_WIDTH = "pref_worm_width";

    public static final int DEFAULT_WORM_WIDTH = 1;

    public static final String PREF_WORM_SPEED = "pref_worm_speed";

    public static final int DEFAULT_WORM_SPEED = 3;

    private SharedPreferences mPref;

    private SharedPreferences getShpref() {
        if (mPref == null) {
            mPref = this.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        }
        return mPref;
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        Tracker tracker = GoogleAnalytics.getInstance(this).getTracker("UA-54904223-1");
        tracker.send(MapBuilder.createEvent("settings", "worm length",
                String.valueOf(getShpref().getInt(PREF_WORM_LENGTH, DEFAULT_WORM_LENGTH)), null)
                .build());
        tracker.send(MapBuilder.createEvent("settings", "worm count",
                String.valueOf(getShpref().getInt(PREF_WORM_COUNT, DEFAULT_WORM_COUNT)), null)
                .build());
        tracker.send(MapBuilder.createEvent("settings", "worm width",
                String.valueOf(getShpref().getInt(PREF_WORM_WIDTH, DEFAULT_WORM_WIDTH)), null)
                .build());
        tracker.send(MapBuilder.createEvent("settings", "worm speed",
                String.valueOf(getShpref().getInt(PREF_WORM_SPEED, DEFAULT_WORM_SPEED)), null)
                .build());
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings_activity);
        mWormLength = (SeekBar)findViewById(R.id.worm_length);
        mWormLength.setProgress(getShpref().getInt(PREF_WORM_LENGTH, DEFAULT_WORM_LENGTH));
        mWormLength.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getShpref().edit().putInt(PREF_WORM_LENGTH, progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mWormCount = (SeekBar)findViewById(R.id.worm_count);
        mWormCount.setProgress(getShpref().getInt(PREF_WORM_COUNT, DEFAULT_WORM_COUNT));
        mWormCount.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getShpref().edit().putInt(PREF_WORM_COUNT, progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mWormWidth = (SeekBar)findViewById(R.id.worm_width);
        mWormWidth.setProgress(getShpref().getInt(PREF_WORM_WIDTH, DEFAULT_WORM_WIDTH));
        mWormWidth.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getShpref().edit().putInt(PREF_WORM_WIDTH, progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mWormSpeed = (SeekBar)findViewById(R.id.worm_speed);
        mWormSpeed.setProgress(getShpref().getInt(PREF_WORM_SPEED, DEFAULT_WORM_SPEED));
        mWormSpeed.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getShpref().edit().putInt(PREF_WORM_SPEED, progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
