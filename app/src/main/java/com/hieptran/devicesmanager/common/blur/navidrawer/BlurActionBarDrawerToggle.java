package com.hieptran.devicesmanager.common.blur.navidrawer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hieptran.devicesmanager.common.blur.Blur;

/**
 * Created by hiepth on 01/04/2016.
 */
public class BlurActionBarDrawerToggle extends ActionBarDrawerToggle{
    private Context mContext;

    /**
     * Layout nen ben duoi khi take screenhot de blur
     */
    private DrawerLayout mDrawerLayout;

    private ImageView mImageViewBlurred;

    public static int DEFAULT_RADIUS = 8;
    private int mBlurRadius = DEFAULT_RADIUS;

    private float mDownScaleFactor = DEFAULT_DOWNSCALE;
    private boolean prepareToRender = true;

    /**
     * flag for "fake" sliding detection
     */
    private boolean isOpening = false;

    public static float DEFAULT_DOWNSCALE = 6.0f;
    /**
     * @param activity
     * @param drawerLayout
     * @param openDrawerContentDescRes
     * @param closeDrawerContentDescRes
     */


    public BlurActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        this.mContext = activity.getBaseContext();
        this.mDrawerLayout = drawerLayout;
        initview();

    }

    public BlurActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        this.mDrawerLayout = drawerLayout;
        this.mContext = activity.getBaseContext();
        initview();
    }

    private void initview() {
        mImageViewBlurred = new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        mImageViewBlurred.setLayoutParams(params);
        mImageViewBlurred.setClickable(false);
        mImageViewBlurred.setVisibility(View.GONE);
        mImageViewBlurred.setScaleType(ImageView.ScaleType.FIT_XY);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.addView(mImageViewBlurred,1);
            }
        });
    }
    @Override
    public void onDrawerSlide(final View drawerView, final float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);

        //must check this for "fake" sliding..
        if (slideOffset == 0.f)
            isOpening = false;
        else
            isOpening = true;

        render();
        setAlpha(mImageViewBlurred, slideOffset, 100);
    }


    @Override
    public void onDrawerClosed(View view) {
        prepareToRender = true;
        mImageViewBlurred.setVisibility(View.GONE);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        super.onDrawerStateChanged(newState);

        // "fake" sliding detection
        if (newState == DrawerLayout.STATE_IDLE
                && !isOpening) {

            handleRecycle();
        }
    }

    /**
     * Snapshots the specified layout and scale it using scaleBitmap() function
     * then we blur the scaled bitmap with the preferred blur radius.
     * Finally, we post it to our fake {@link android.widget.ImageView}.
     */

    private void render() {

        if (prepareToRender) {
            prepareToRender = false;

            Bitmap bitmap = loadBitmapFromView(mDrawerLayout);
            bitmap = scaleBitmap(bitmap);
            bitmap = Blur.fastblur(mContext, bitmap, mBlurRadius, false);

            mImageViewBlurred.setVisibility(View.VISIBLE);
            mImageViewBlurred.setImageBitmap(bitmap);
        }

    }

    public void setRadius(int radius) {
        mBlurRadius = radius < 1 ? 1 : radius;
    }

    public void setDownScaleFactor(float downScaleFactor) {
        mDownScaleFactor = downScaleFactor < 1 ? 1 : downScaleFactor;
    }

    private void setAlpha(View view, float alpha, long durationMillis) {
        if (Build.VERSION.SDK_INT < 11) {
            final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(durationMillis);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        } else {
            view.setAlpha(alpha);
        }
    }


    private Bitmap loadBitmapFromView(View mView) {
        Bitmap b = Bitmap.createBitmap(
                mView.getWidth(),
                mView.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);

        // With the following, screen blinks
        //v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);

        mView.draw(c);

        return b;
    }


    private Bitmap scaleBitmap(Bitmap myBitmap) {

        int width = (int) (myBitmap.getWidth() / mDownScaleFactor);
        int height = (int) (myBitmap.getHeight() / mDownScaleFactor);

        return Bitmap.createScaledBitmap(myBitmap, width, height, false);
    }

    private void handleRecycle() {
        Drawable drawable = mImageViewBlurred.getDrawable();

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
            Bitmap bitmap = bitmapDrawable.getBitmap();

            if (bitmap != null)
                bitmap.recycle();

            mImageViewBlurred.setImageBitmap(null);
        }

        prepareToRender = true;
    }

}
