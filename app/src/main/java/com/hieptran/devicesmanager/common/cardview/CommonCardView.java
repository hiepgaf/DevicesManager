package com.hieptran.devicesmanager.common.cardview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.Utils;

/**
 * Created by hiepth on 28/01/2016.
 */
public abstract class CommonCardView extends CardView {
    private static final int DEFAULT_LAYOUT_ID = R.layout.default_cardview;
    protected final LinearLayout customLayout;
    private View customView;
    private LinearLayout mainView;
    private HeaderCardView headerCardView;
    private final LinearLayout headerLayout;

    public CommonCardView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, DEFAULT_LAYOUT_ID);
    }

    public CommonCardView(Context context) {
        this(context, DEFAULT_LAYOUT_ID);
    }
    public CommonCardView(Context context, int layout) {
        this(context, null, layout);
    }


    public CommonCardView(Context context,AttributeSet attributeSet, int layoutId) {
        super(context, attributeSet);

        // Add a margin
        setMargin();

        // Make a rounded card
        setRadius();

        // Set background color depending on the current theme
        setCardBackgroundColor(getResources().getColor(Utils.DARK ?
                R.color.card_background_dark : R.color.card_background_light));

        // This will enable the touch feedback of the card
        TypedArray ta = getContext().obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
        Drawable d = ta.getDrawable(0);
        ta.recycle();
        setForeground(d);

        // Add the base view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.common_cardview, null, false);
        addView(view);
        customLayout = (LinearLayout) view.findViewById(R.id.custom_layout);
        headerLayout = (LinearLayout) view.findViewById(R.id.header_layout);
        setupHeader();



    }
    public void setMargin() {
        int padding = getResources().getDimensionPixelSize(R.dimen.basecard_padding);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(padding, padding, padding, 0);
        setLayoutParams(layoutParams);
    }


    /**
     * Use a function to set radius, so child class can overwrite it
     */
    public void setRadius() {
        setRadius(getResources().getDimensionPixelSize(R.dimen.basecard_radius));
    }
    //Thiet lap mainView
    public void setView(View v) {
        customView = v;
        setCustomLayout();
    }
    public void setCustomLayout() {
        if (customLayout != null && customView != null) {
            customLayout.setVisibility(VISIBLE);
            customLayout.removeAllViews();
            try {
                ((ViewGroup) customView.getParent()).removeView(customView);
            } catch (NullPointerException ignored) {
            }
            customLayout.addView(customView);

        }
    }
    public void setupHeader() {
        if (headerCardView != null && headerLayout != null) {
            headerLayout.removeAllViews();
            headerLayout.addView(headerCardView.getView());
            headerLayout.setVisibility(VISIBLE);
        }
    }
    public void addHeader(HeaderCardView headerCardView) {
        this.headerCardView = headerCardView;
        setupHeader();
    }
}
