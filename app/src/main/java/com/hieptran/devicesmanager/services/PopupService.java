package com.hieptran.devicesmanager.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.CircleChart;
import com.hieptran.devicesmanager.utils.Utils;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by hiepth on 30/03/2016.
 */
public class PopupService extends Service {


    WindowManager wm;
    int mCurrentX, mCurrentY;
    WaveLoadingView mLoadingView;
    TextView mInfo;
    android.os.Handler hand;
    GestureDetector gestureDetector;
    AppCompatButton mCloseButton;
    View mViewMain;
    LinearLayout layoutMain;
    Intent i;
    int level;
    private CircleChart mCircleChartLevel;
    private boolean mInternalClosed = false;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("HiepTHB","mBroadcastReceiver");
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

        }
    };
    private int mW;
    @Override
    public void onStart(Intent intent, int startId)
    {
       // registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        hand = new android.os.Handler();
        i = intent;
        gestureDetector = new GestureDetector(getApplicationContext(), new GestureListener());
        mInternalClosed = false;

        //   LayoutInflater layoutInflater = (LayoutInflater)getApplication().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        //http://stackoverflow.com/questions/8073803/android-multi-touch-and-type-system-overlay
        mViewMain = layoutInflater.inflate(R.layout.popup_layout, null);
        mInfo = (TextView) mViewMain.findViewById(R.id.totalpower);
        layoutMain = (LinearLayout) mViewMain.findViewById(R.id.layout_main);
        mCloseButton = (AppCompatButton) mViewMain.findViewById(R.id.close_popup);
        mCircleChartLevel = (CircleChart) mViewMain.findViewById(R.id.level_popup);
        mCircleChartLevel.setMax(100);


       /* mInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mViewMain, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });*/

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mW = wm.getDefaultDisplay().getWidth()*3/4;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
               /* WindowManager.LayoutParams.WRAP_CONTENT*/mW,
               WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.RGBA_8888);
        // final PopupWindow mPopup = new PopupWindow(mViewMain, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        View.OnTouchListener otl = new View.OnTouchListener() {
            private float mDx;
            private float mDy;
            private float mLastX, mLastY;

            //http://stackoverflow.com/questions/14938853/could-not-get-touch-event-for-type-system-overlay
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layoutMain.onInterceptTouchEvent(event);

                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    Log.d("HiepTHb", "DOWN");
                    mDx = mLastX - event.getRawX();
                    mDy = mLastY - event.getRawY();
                } else if (action == MotionEvent.ACTION_MOVE) {
                    mCurrentX = (int) (event.getRawX() + mDx);
                    mCurrentY = (int) (event.getRawY() + mDy);
                    mLastX = mCurrentX;
                    mLastY = mCurrentY;

                 /*   mViewMain.setX(mCurrentX);
                    mViewMain.setY(mCurrentY);
                    Log.d("HiepTHb", "MOVE");*/
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                           mW,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            mCurrentX, mCurrentY,
                            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
                    //params.gravity = Gravity.BOTTOM|Gravity.LEFT|Gravity.CENTER|Gravity.TOP|Gravity.RIGHT;
                    wm.updateViewLayout(v, params);
                   // Log.d("HiepTHb", "mViewMain.sau()" + mLastX);
                   // Log.d("HiepTHb", "mViewMain.sau()" + mLastY);
                    //   mViewMain.update(mCurrentX, mCurrentY, -1, -1);
                } else if (action == MotionEvent.ACTION_UP) {
                    mCurrentX = (int) mLastX;
                    mCurrentY = (int) mLastY;
                 /*   WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            (int)mLastX,(int)mLastY,
                            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT ,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.TOP | Gravity. LEFT;
                    wm.updateViewLayout(mViewMain, params);
                    Log.d("HiepTHb", "mViewMain.mCurrentX()" + mCurrentX);
                    Log.d("HiepTHb", "mViewMain.mCurrentY()" +mCurrentY);*/
//mViewMain.invalidate();
                    Log.d("HiepTHb", "mViewMain.getLeft()" + v.getLeft());
                    Log.d("HiepTHb", "mViewMain.getRight()" + v.getRight());
                    Log.d("HiepTHb", "mViewMain.getTop()" + v.getTop());
                    Log.d("HiepTHb", "mViewMain.getBottom()" + v.getBottom());
                    //return true;
                }
                //   return gestureDetector.onTouchEvent(event);
                return true;
            }
        };
        //http://stackoverflow.com/questions/23120807/android-move-view-on-touch-event
        //  http://stackoverflow.com/questions/9398057/android-move-a-view-on-touch-move-action-move

        layoutMain.setOnTouchListener(otl);

        mViewMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("HiepTHb", "OnLong");
       /* wm.removeView(mViewMain);
        if (hand != null) hand.removeCallbacks(updateView);*/
                return false;
            }
        });
       /* mViewMain.post(new Runnable() {
            @Override
            public void run() {
                mViewMain.setX(mCurrentX);
                mViewMain.setY(mCurrentY);
                //mPopup.showAtLocation(mViewMain, Gravity.NO_GRAVITY, mCurrentX, mCurrentY);
            }
        });*/
        // WindowManagerを取得する


        wm.addView(mViewMain, params);
        hand.post(updateView);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInternalClosed = false;

//                if (hand != null) hand.removeCallbacks(updateView);
//                wm.removeView(mViewMain);
                Intent i = new Intent("com.hieptran.devicesmanager.CLOSED_POPUP_ACTION");
                sendBroadcast(i);
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (hand != null) hand.removeCallbacks(updateView);
        if (wm != null && mViewMain != null)

            wm.removeView(mViewMain);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (hand != null) hand.post(updateView);
        return null;
    }

    int oo = 0;

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private Runnable updateView = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mInfo.post(new Runnable() {
                        @Override
                        public void run() {
                            mCircleChartLevel.setProgress(Utils.getInt("level_popup", 0, getApplicationContext()));

                            mInfo.setText(Utils.getString("info_popup", "----", getApplicationContext()));
                        }
                    });
                }
            }).start();
            hand.postDelayed(updateView, 1000);
        }

    };

    //http://stackoverflow.com/questions/21013963/handling-intercept-touch-event-without-extending-viewgroup
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
            wm.removeView(mViewMain);
            if (hand != null) hand.removeCallbacks(updateView);
            return true;
        }
    }

}
