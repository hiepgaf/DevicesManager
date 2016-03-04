/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hieptran.devicesmanager.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.hieptran.devicesmanager.R;

import me.itangqi.waveloadingview.WaveLoadingView;


/**
 * Created by HiepTran on 09.03.15.
 */
//Todo: View hiển thị khi thiết lập các môi trường ban đầu, lấy quyền root từ thiết bị

public class SplashView extends View {


    private static final int START_ANGLE_POINT = 0;
    private final Paint paint;
    private final RectF rect;
    private int radius = 0;
    private int rotate = 0;
    private boolean finished = false;
    private float angle;
    private boolean isFinish = false;
    public SplashView(Context context) {
        this(context, null);
    }

    public SplashView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        final int strokeWidth = 40;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        //Circle color
        paint.setColor(Color.RED);

        //size 200x200 example
        rect = new RectF(strokeWidth, strokeWidth, 200 + strokeWidth, 200 + strokeWidth);

        //Initial Angle (optional, it can be zero)
        angle = 0;
        setBackgroundColor(getResources().getColor(R.color.black));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                        splashTime+=100;
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                            }
                        });
                        if (isFinish) break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
/*//HiepTHb
        waveView = new WaveLoadingView();
        waveView.setBorderWidth(0);
        waveView.setAmplitudeRatio(50);
        waveView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        waveView.setProgressValue(50);
        waveView.setWaveColor(Color.WHITE);*/


    }

    public void finish() {
        isFinish = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 1; i <= getHeight() / 2; i += 15) {
                        radius = i;
                        ((Activity) getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                invalidate();
                            }
                        });
                        Thread.sleep(35);
                    }
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setVisibility(GONE);
                            finished = true;
                            startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public float getAngle() {
        return angle;
    }
    int splashTime=0;
    public void setAngle(float angle) {
        this.angle = angle;
    }
    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        TextPaint textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setColor(Color.LTGRAY);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.splashview_textsize));
        //canvas.drawText(getResources().getString(R.string.waiting_for_init), getWidth() / 2, getHeight() / 2, textPaint);

                if(splashTime < 500){
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    canvas.drawText(getResources().getString(R.string.waiting_for_init) +" .", getWidth() / 2, getHeight() / 2, textPaint);
                }
                else if(splashTime >= 500 && splashTime < 700 ){
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                    canvas.drawText(getResources().getString(R.string.waiting_for_init)+ " ..", getWidth() / 2, getHeight() / 2, textPaint);
                }else if (splashTime >= 700){
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                    canvas.drawText(getResources().getString(R.string.waiting_for_init)+ " ...", getWidth() / 2, getHeight() / 2, textPaint);
                }
                //splashTime = splashTime + 100;
            }

        // canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);




}
