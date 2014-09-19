
package com.bj4.yhh.livewallpaper;

import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * @author Yen-Hsun_Huang
 */
public class TechLinesWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new TechLinesWallpaper(this);
    }

    private class TechLinesWallpaper extends Engine implements LocationListener,
            GooglePlayServicesClient.ConnectionCallbacks,
            GooglePlayServicesClient.OnConnectionFailedListener {

        private final Handler mHandler = new Handler();

        private TechLines mGrabbingWorm = null;

        private boolean mIsVisible = true;

        private ValueAnimator mInternalVa;

        private final Paint mReleasedWormPaint = new Paint(), mStruggledWormPaint = new Paint(),
                mTextPaint = new Paint();

        private final ArrayList<TechLines> mLoadingLines = new ArrayList<TechLines>();

        private int mWidth, mHeight;

        private Context mContext;

        private WeatherData mWeatherData;

        private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processBatteryIntent(intent);
            }
        };

        private Bitmap mGrabbingIcon;

        private final BroadcastReceiver mWeatherDataUpdateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                mWeatherData = WeatherData.getWeatherData(context);
                if (mGrabbingIcon != null) {
                    mGrabbingIcon.recycle();
                }
                mGrabbingIcon = BitmapFactory.decodeResource(mContext.getResources(),
                        Utils.getWeatherIcon(mWeatherData.mImageCode));
            }
        };

        public TechLinesWallpaper(Context context) {
            mContext = context.getApplicationContext();
            mReleasedWormPaint.setDither(true);
            mReleasedWormPaint.setAntiAlias(true);
            mStruggledWormPaint.setDither(true);
            mStruggledWormPaint.setAntiAlias(true);
            mTextPaint.setColor(Color.BLACK);
            mTextPaint.setTextSize(mContext.getResources().getDimension(R.dimen.grabbing_textsize));
            mTextPaint.setTextAlign(Align.CENTER);
            mLocationClient = new LocationClient(mContext, this, this);
            loadInfo();
        }

        public void loadInfo() {
            mLoadingLines.clear();
            SharedPreferences pref = mContext.getSharedPreferences(TechLinesSettings.PREF_FILE,
                    Context.MODE_PRIVATE);
            final int wormCount = pref.getInt(TechLinesSettings.PREF_WORM_COUNT,
                    TechLinesSettings.DEFAULT_WORM_COUNT);
            final int wormWidth = pref.getInt(TechLinesSettings.PREF_WORM_WIDTH,
                    TechLinesSettings.DEFAULT_WORM_WIDTH);
            float density = getResources().getDisplayMetrics().scaledDensity;
            mReleasedWormPaint.setStrokeWidth((1 + wormWidth) * density);
            mReleasedWormPaint.setColor(Color.WHITE);
            mStruggledWormPaint.setStrokeWidth((1 + wormWidth) * density);
            mStruggledWormPaint.setColor(Color.YELLOW);
            for (int i = 0; i < wormCount; i++) {
                mLoadingLines.add(new TechLines(density, mContext));
            }
        }

        private final void processBatteryIntent(Intent batteryStatus) {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL;
            int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float)scale;
            // if (!isCharging) {
            mReleasedWormPaint.setColor(Color.rgb((int)(255 * (1 - batteryPct)),
                    (int)(255 * batteryPct), 0));
            // } else {
            // if (usbCharge) {
            // mReleasedWormPaint.setColor(Color.rgb(0, 255, 255));
            // } else if (acCharge) {
            // mReleasedWormPaint.setColor(Color.rgb(0, 0, 255));
            // }
            // }
        }

        private void registerStuff() {
            if (mContext.getSharedPreferences(TechLinesSettings.PREF_FILE, Context.MODE_PRIVATE)
                    .getInt(TechLinesSettings.PREF_WORM_COLOR, TechLinesSettings.COLOR_CLASSIC) == TechLinesSettings.COLOR_BATTERY) {
                IntentFilter batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = mContext.registerReceiver(mBatteryReceiver,
                        batteryIntentFilter);
                processBatteryIntent(batteryStatus);
            } else {
                mReleasedWormPaint.setColor(Color.WHITE);
            }
            mHandler.post(mLocationClientConnectionRunnable);
            IntentFilter weatherDataUpdateFilter = new IntentFilter(
                    WeatherParseService.ON_WEATHER_DATA_UPDATE);
            mContext.registerReceiver(mWeatherDataUpdateReceiver, weatherDataUpdateFilter);
        }

        private void unregisterStuff() {
            if (mContext.getSharedPreferences(TechLinesSettings.PREF_FILE, Context.MODE_PRIVATE)
                    .getInt(TechLinesSettings.PREF_WORM_COLOR, TechLinesSettings.COLOR_CLASSIC) == TechLinesSettings.COLOR_BATTERY) {
                mContext.unregisterReceiver(mBatteryReceiver);
            }
            if (mLocationClient != null) {
                mLocationClient.disconnect();
            }
            mContext.unregisterReceiver(mWeatherDataUpdateReceiver);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mIsVisible = visible;
            if (visible) {
                loadInfo();
                getInternalAnimator().setRepeatCount(ValueAnimator.INFINITE);
                getInternalAnimator().setRepeatMode(ValueAnimator.REVERSE);
                getInternalAnimator().start();
                registerStuff();
            } else {
                getInternalAnimator().setRepeatCount(0);
                getInternalAnimator().end();
                unregisterStuff();
            }
        }

        private ValueAnimator getInternalAnimator() {
            if (mInternalVa == null) {
                mInternalVa = ValueAnimator.ofFloat(1, 4);
                mInternalVa.setDuration(4000);
                mInternalVa.addUpdateListener(new AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator va) {
                        draw();
                    }
                });
            }
            return mInternalVa;
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mIsVisible = false;
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, final int width,
                final int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mWidth = width;
            mHeight = height;
            getInternalAnimator();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            final int x = (int)event.getRawX();
            final int y = (int)event.getRawY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                for (TechLines line : mLoadingLines) {
                    if (line.isGrabbing(x, y)) {
                        mGrabbingWorm = line;
                        break;
                    }
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (mGrabbingWorm != null) {
                    mGrabbingWorm.release();
                    mGrabbingWorm = null;
                    final int wormWidth = mContext.getSharedPreferences(
                            TechLinesSettings.PREF_FILE, Context.MODE_PRIVATE)
                            .getInt(TechLinesSettings.PREF_WORM_WIDTH,
                                    TechLinesSettings.DEFAULT_WORM_WIDTH);
                    float density = getResources().getDisplayMetrics().scaledDensity;
                    mStruggledWormPaint.setStrokeWidth((1 + wormWidth) * density);
                }
            }
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.BLACK);
                    if (mIsVisible) {
                        for (TechLines line : mLoadingLines) {
                            if (mGrabbingWorm != line) {
                                line.calculate(mWidth, mHeight, canvas, mReleasedWormPaint);
                            } else {
                                line.showInfo(mWidth, mHeight, canvas, mStruggledWormPaint,
                                        mReleasedWormPaint, mTextPaint, mWeatherData, mGrabbingIcon);
                            }
                        }
                    }
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
        }

        // location update
        private static final long DEFAULT_UPDATE_LOCATION_INTERVAL = 10 * 60 * 1000;

        private static final long UPDATE_WEATHER_DATA_INTERVAL = 60 * 60 * 1000 * 3;

        private LocationClient mLocationClient;

        private Runnable mLocationClientConnectionRunnable = new Runnable() {

            @Override
            public void run() {
                if (mLocationClient != null && mLocationClient.isConnected() == false)
                    mLocationClient.connect();
                mHandler.postDelayed(mLocationClientConnectionRunnable,
                        DEFAULT_UPDATE_LOCATION_INTERVAL);
            }
        };

        private Runnable mUpdateWeatherDataRunnable = new Runnable() {
            @Override
            public void run() {
                if (mLocationClient.isConnected()) {
                    updateCurrentLocationData(mLocationClient.getLastLocation());
                }
                mHandler.removeCallbacks(mUpdateWeatherDataRunnable);
                mHandler.postDelayed(mUpdateWeatherDataRunnable, UPDATE_WEATHER_DATA_INTERVAL);
            }
        };

        private void updateCurrentLocationData(Location location) {
            if (location != null) {
                Intent startIntent = new Intent(mContext, WeatherParseService.class);
                startIntent.putExtra(WeatherParseService.INTENT_KEY_LATITUDE,
                        location.getLatitude());
                startIntent.putExtra(WeatherParseService.INTENT_KEY_LONGTITUDE,
                        location.getLongitude());
                mContext.startService(startIntent);
            }
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionHint) {
            updateCurrentLocationData(mLocationClient.getLastLocation());
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(DEFAULT_UPDATE_LOCATION_INTERVAL)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationClient.requestLocationUpdates(locationRequest, this);
            mHandler.removeCallbacks(mLocationClientConnectionRunnable);
            mHandler.removeCallbacks(mUpdateWeatherDataRunnable);
            mHandler.postDelayed(mUpdateWeatherDataRunnable, UPDATE_WEATHER_DATA_INTERVAL);
        }

        @Override
        public void onDisconnected() {
            if (mLocationClient != null && mLocationClient.isConnected())
                mLocationClient.removeLocationUpdates(this);
            mHandler.removeCallbacks(mLocationClientConnectionRunnable);
        }

        @Override
        public void onLocationChanged(Location location) {
            updateCurrentLocationData(location);
        }
    }
}
