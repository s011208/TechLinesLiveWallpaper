
package com.bj4.yhh.livewallpaper;

import java.util.ArrayList;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class MyWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new MyWallpaperEngine();
    }

    private class MyWallpaperEngine extends Engine {

        private boolean visible = true;

        private ValueAnimator mInternalVa;

        private final Paint mRandomLinesPaint = new Paint();

        private final ArrayList<LoadingLine> mLoadingLines = new ArrayList<LoadingLine>();

        private int mWidth, mHeight;

        private static final int NUMBER_OF_LOADING_WORMS = 100;

        public MyWallpaperEngine() {
            mRandomLinesPaint.setDither(true);
            mRandomLinesPaint.setAntiAlias(true);
            float density = getResources().getDisplayMetrics().scaledDensity;
            mRandomLinesPaint.setStrokeWidth(LoadingLine.LOADING_WORM_WIDTH * density);
            for (int i = 0; i < NUMBER_OF_LOADING_WORMS; i++) {
                mLoadingLines.add(new LoadingLine(density));
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
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
            this.visible = false;
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
            LinearGradient lg = new LinearGradient(0, 0, 100, 100, Color.BLACK, Color.argb(30, 255,
                    255, 255), android.graphics.Shader.TileMode.MIRROR);
            mRandomLinesPaint.setShader(lg);
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
                    if (visible) {
                        for (LoadingLine line : mLoadingLines) {
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
