
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

        private boolean mIsVisible = true;

        private ValueAnimator mInternalVa;

        private final Paint mRandomLinesPaint = new Paint();

        private final ArrayList<TechLines> mLoadingLines = new ArrayList<TechLines>();

        private int mWidth, mHeight;

        private Context mContext;

        public TechLinesWallpaper(Context context) {
            mContext = context.getApplicationContext();
            mRandomLinesPaint.setDither(true);
            mRandomLinesPaint.setAntiAlias(true);
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
            mRandomLinesPaint.setStrokeWidth((1 + wormWidth) * density);
            for (int i = 0; i < wormCount; i++) {
                mLoadingLines.add(new TechLines(density, mContext));
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mIsVisible = visible;
            if (visible) {
                loadInfo();
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
            mRandomLinesPaint.setColor(Color.WHITE);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
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
                            line.calculate(mWidth, mHeight, canvas, mRandomLinesPaint);
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
