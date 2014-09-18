
package com.bj4.yhh.livewallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;

/**
 * @author Yen-Hsun_Huang
 */
public class TechLines {
    private static final int GRABBING_DISTANCE = 20;

    private static final int GRABBING_GROWTH_CONSTANT = 3;

    public int LOADING_WORM_CHANGE_DISTANCE;

    public int LOADING_WORM_LENGTH;

    public int mWay = 0;

    public int mLoadingLineX, mLoadingLineY;

    private boolean mIsGrabbing = false;

    private Context mContext;

    public TechLines(float density, Context context) {
        mContext = context;
        SharedPreferences pref = context.getSharedPreferences(TechLinesSettings.PREF_FILE,
                Context.MODE_PRIVATE);
        final int baseWormSpeed = pref.getInt(TechLinesSettings.PREF_WORM_SPEED,
                TechLinesSettings.DEFAULT_WORM_SPEED);
        LOADING_WORM_CHANGE_DISTANCE = (int)((1 + baseWormSpeed) * density);
        final int baseWormLength = pref.getInt(TechLinesSettings.PREF_WORM_LENGTH,
                TechLinesSettings.DEFAULT_WORM_LENGTH);
        LOADING_WORM_LENGTH = (int)((((Math.random() * 1000) % 150) + baseWormLength) * density);
        mLoadingLineX = -1 - LOADING_WORM_LENGTH;
        mLoadingLineY = -1 - LOADING_WORM_LENGTH;
    }

    public void release() {
        mIsGrabbing = false;
        mGrabbingRadius = 0;
    }

    public boolean isGrabbing(final int x, final int y) {
        switch (mWay) {
            case 0:
                if (x <= mLoadingLineX && x >= mLoadingLineX - LOADING_WORM_LENGTH
                        && y >= mLoadingLineY - GRABBING_DISTANCE
                        && y <= mLoadingLineY + GRABBING_DISTANCE) {
                    mIsGrabbing = true;
                }
                break;
            case 1:
                if (x >= mLoadingLineX && x <= mLoadingLineX + LOADING_WORM_LENGTH
                        && y >= mLoadingLineY - GRABBING_DISTANCE
                        && y <= mLoadingLineY + GRABBING_DISTANCE) {
                    mIsGrabbing = true;
                }
                break;
            case 2:
                if (y <= mLoadingLineY && y >= mLoadingLineY - LOADING_WORM_LENGTH
                        && x >= mLoadingLineX - GRABBING_DISTANCE
                        && x <= mLoadingLineX + GRABBING_DISTANCE) {
                    mIsGrabbing = true;
                }
                break;
            case 3:
                if (y >= mLoadingLineY && y <= mLoadingLineY + LOADING_WORM_LENGTH
                        && x >= mLoadingLineX - GRABBING_DISTANCE
                        && x <= mLoadingLineX + GRABBING_DISTANCE) {
                    mIsGrabbing = true;
                }
                break;
        }
        return mIsGrabbing;
    }

    public void calculate(final int width, final int height, final Canvas canvas, final Paint paint) {
        if (mLoadingLineX + LOADING_WORM_LENGTH <= -1 || mLoadingLineY + LOADING_WORM_LENGTH <= -1
                || mLoadingLineX - LOADING_WORM_LENGTH > width
                || mLoadingLineY - LOADING_WORM_LENGTH > height) {
            mWay = (int)((Math.random() * 20) % 4);
            switch (mWay) {
                case 0:
                    mLoadingLineX = 0;
                    mLoadingLineY = (int)((Math.random() * 10000) % height);
                    break;
                case 1:
                    mLoadingLineX = width;
                    mLoadingLineY = (int)((Math.random() * 10000) % height);
                    break;
                case 2:
                    mLoadingLineY = 0;
                    mLoadingLineX = (int)((Math.random() * 10000) % width);
                    break;
                case 3:
                    mLoadingLineY = height;
                    mLoadingLineX = (int)((Math.random() * 10000) % width);
                    break;
            }
        }
        switch (mWay) {
            case 0:
                mLoadingLineX += LOADING_WORM_CHANGE_DISTANCE;
                canvas.drawLine(mLoadingLineX, mLoadingLineY, mLoadingLineX - LOADING_WORM_LENGTH,
                        mLoadingLineY, paint);
                break;
            case 1:
                mLoadingLineX -= LOADING_WORM_CHANGE_DISTANCE;
                canvas.drawLine(mLoadingLineX, mLoadingLineY, mLoadingLineX + LOADING_WORM_LENGTH,
                        mLoadingLineY, paint);
                break;
            case 2:
                mLoadingLineY += LOADING_WORM_CHANGE_DISTANCE;
                canvas.drawLine(mLoadingLineX, mLoadingLineY, mLoadingLineX, mLoadingLineY
                        - LOADING_WORM_LENGTH, paint);
                break;
            case 3:
                mLoadingLineY -= LOADING_WORM_CHANGE_DISTANCE;
                canvas.drawLine(mLoadingLineX, mLoadingLineY, mLoadingLineX, mLoadingLineY
                        + LOADING_WORM_LENGTH, paint);
                break;
        }
    }

    private int mGrabbingRadius = 0;

    public void showInfo(final int width, final int height, final Canvas canvas,
            final Paint circlaPaint, final Paint paint) {
        int start = 0, finalPosition = 0;
        mGrabbingRadius += GRABBING_GROWTH_CONSTANT;
        // Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
        // R.drawable.w00);
        // canvas.drawBitmap(bmp, mLoadingLineX - LOADING_WORM_LENGTH / 2,
        // mLoadingLineY, null);
        // bmp.recycle();
        switch (mWay) {
            case 0:
                start = mLoadingLineX - mGrabbingRadius * 2;
                finalPosition = mLoadingLineX - LOADING_WORM_LENGTH + mGrabbingRadius * 2;
                canvas.drawCircle(mLoadingLineX - LOADING_WORM_LENGTH / 2, mLoadingLineY,
                        mGrabbingRadius, circlaPaint);
                if (finalPosition <= start) {
                    canvas.drawLine(start, mLoadingLineY, finalPosition, mLoadingLineY, paint);
                }
                break;
            case 1:
                start = mLoadingLineX + mGrabbingRadius * 2;
                finalPosition = mLoadingLineX + LOADING_WORM_LENGTH - mGrabbingRadius * 2;
                canvas.drawCircle(mLoadingLineX + LOADING_WORM_LENGTH / 2, mLoadingLineY,
                        mGrabbingRadius, circlaPaint);
                if (finalPosition >= start) {
                    canvas.drawLine(start, mLoadingLineY, finalPosition, mLoadingLineY, paint);
                }
                break;
            case 2:
                start = mLoadingLineY - mGrabbingRadius * 2;
                finalPosition = mLoadingLineY - LOADING_WORM_LENGTH + mGrabbingRadius * 2;
                canvas.drawCircle(mLoadingLineX, mLoadingLineY - LOADING_WORM_LENGTH / 2,
                        mGrabbingRadius, circlaPaint);
                if (finalPosition <= start) {
                    canvas.drawLine(mLoadingLineX, start, mLoadingLineX, finalPosition, paint);
                }
                break;
            case 3:
                start = mLoadingLineY + mGrabbingRadius * 2;
                finalPosition = mLoadingLineY + LOADING_WORM_LENGTH - mGrabbingRadius * 2;
                canvas.drawCircle(mLoadingLineX, mLoadingLineY + LOADING_WORM_LENGTH / 2,
                        mGrabbingRadius, circlaPaint);
                if (finalPosition >= start) {
                    canvas.drawLine(mLoadingLineX, start, mLoadingLineX, finalPosition, paint);
                }
                break;
        }
    }
}
