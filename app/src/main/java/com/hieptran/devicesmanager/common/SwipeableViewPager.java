package com.hieptran.devicesmanager.common;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by hieptran on 09/01/2016.
 */
public class SwipeableViewPager extends ViewPager {

    private boolean swipe = true;

    public SwipeableViewPager(Context context) {
        this(context, null);
    }

    public SwipeableViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeableViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

    public void allowSwipe(boolean swipe) {
        this.swipe = swipe;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return swipe && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return swipe && super.onTouchEvent(ev);
    }
}