
package com.bj4.yhh.livewallpaper;

import java.util.ArrayList;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
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

    private class TechLinesWallpaper extends Engine {

        private Handler mHandler = new Handler();

        private Runnable mUpdateSettingsRunnable = new Runnable() {

            @Override
            public void run() {
                mContext.getSharedPreferences(TechLinesSettings.PREF_FILE, Context.MODE_PRIVATE)
                        .edit().putBoolean(TechLinesSettings.PREF_HAS_SETTING_CHANGED, false)
                        .apply();
            }
        };

        private TechLines mGrabbingWorm = null;

        private boolean mIsVisible = true;

        private ValueAnimator mInternalVa;

        private final Paint mReleasedWormPaint = new Paint(), mStruggledWormPaint = new Paint();

        private final ArrayList<TechLines> mLoadingLines = new ArrayList<TechLines>();

        private int mWidth, mHeight;

        private Context mContext;

        public TechLinesWallpaper(Context context) {
            mContext = context.getApplicationContext();
            mReleasedWormPaint.setDither(true);
            mReleasedWormPaint.setAntiAlias(true);
            mStruggledWormPaint.setDither(true);
            mStruggledWormPaint.setAntiAlias(true);
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
            mStruggledWormPaint.setColor(Color.RED);
            for (int i = 0; i < wormCount; i++) {
                mLoadingLines.add(new TechLines(density, mContext));
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mIsVisible = visible;
            if (visible) {
                if (mContext
                        .getSharedPreferences(TechLinesSettings.PREF_FILE, Context.MODE_PRIVATE)
                        .getBoolean(TechLinesSettings.PREF_HAS_SETTING_CHANGED, false) == true) {
                    loadInfo();
                    mHandler.removeCallbacks(mUpdateSettingsRunnable);
                    mHandler.postDelayed(mUpdateSettingsRunnable, 2000);
                }
                mInternalVa.setRepeatCount(ValueAnimator.INFINITE);
                mInternalVa.setRepeatMode(ValueAnimator.REVERSE);
                mInternalVa.start();
            } else {
                mInternalVa.setRepeatCount(0);
                mInternalVa.end();
            }
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
            mInternalVa = ValueAnimator.ofFloat(1, 4);
            mInternalVa.setDuration(4000);
            mInternalVa.addUpdateListener(new AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator va) {
                    draw();
                }
            });
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
                                line.struggle(mWidth, mHeight, canvas, mStruggledWormPaint, mReleasedWormPaint);
                            }
                        }
                    }
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
        }
    }

}
