package com.hieptran.devicesmanager.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.cardview.HeaderCardView;

/**
 * Created by hieptran on 09/01/2016.
 */
public abstract class CardViewCommon extends CardView {
    /**
     * Default layout
     */
    private static final int DEFAULT_LAYOUT = R.layout.default_cardview;
    private HeaderCardView headerCardView;
    private final LinearLayout headerLayout;
    /**
     * Views
     */
    protected final View layoutView;
    protected final LinearLayout customLayout;
    private View customView;
    private CharSequence mTitle;
    private TextView innerView;


    public CardViewCommon(Context context) {
        this(context, DEFAULT_LAYOUT);
    }

    public CardViewCommon(Context context, int layout) {
        this(context, null, layout);
    }

    public CardViewCommon(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, DEFAULT_LAYOUT);
    }

    public CardViewCommon(Context context, AttributeSet attributeSet, int layout) {
        super(context, attributeSet);

        // Add a margin
        setMargin();

        // Make a rounded card
        setRadius();

        // Set background color depending on the current theme
        setCardBackgroundColor(/**getResources().getColor(Utils.DARKTHEME ?
                R.color.card_background_dark :*/ R.color.card_background_light);

        // This will enable the touch feedback of the card
        TypedArray ta = getContext().obtainStyledAttributes(new int[]{R.attr.selectableItemBackground});
        Drawable d = ta.getDrawable(0);
        ta.recycle();
        setForeground(d);

        // Add the base view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.common_cardview, null, false);
        addView(view);
        headerLayout = (LinearLayout) view.findViewById(R.id.header_layout);

        setUpHeader();


        LinearLayout innerLayout = (LinearLayout) view.findViewById(R.id.inner_layout);
        customLayout = (LinearLayout) view.findViewById(R.id.custom_layout);

        // Inflate the innerlayout
        layoutView = LayoutInflater.from(getContext()).inflate(layout, null, false);

        // If sub class overwrites the default layout then don't try to get the TextView
        if (layout == DEFAULT_LAYOUT) {
            innerView = (TextView) layoutView.findViewById(R.id.default_view);
            if (mTitle != null) innerView.setText(mTitle);
        } else setContentLayout(layoutView);

        // Add innerlayout to base view
        innerLayout.addView(layoutView);

    }

    public void setView(View view) {
        customView = view;
        setLayoutCustom();
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
    /**
     * Sets the string value of TextView in innerlayout
     *
     * @param mTitle new Text of the card
     */
    public void setText(CharSequence mTitle) {
        this.mTitle = mTitle;
        if (innerView != null) innerView.setText(mTitle);
    }
    /**
     *
     * @param v là parrent view chứa content của layout
     */
    public abstract void setContentLayout(View v);

    /**
     * Suwr dụng để thiết lập content view trong mỗi card view
     * @param v
     */
    public void setContentView(View v) {
        customView = v;

    }
    /**
     * Add a header to the card
     *
     * @param headerCardView new Header of the card
     */
    public void addHeader(HeaderCardView headerCardView) {
        this.headerCardView = headerCardView;
        setUpHeader();
    }

    private void setUpHeader() {
        if (headerCardView != null && headerLayout != null) {
            headerLayout.removeAllViews();
            headerLayout.addView(headerCardView.getView());
            headerLayout.setVisibility(VISIBLE);
        }
    }

    public void removeHeader() {
        headerCardView = null;
        if (headerLayout != null) {
            headerLayout.removeAllViews();
            headerLayout.setVisibility(GONE);
        }
    }

    private void setLayoutCustom() {
        if (customLayout != null && customView != null) {
            innerView.setVisibility(GONE);
            customLayout.setVisibility(VISIBLE);
            customLayout.removeAllViews();
            try {
                ((ViewGroup) customView.getParent()).removeView(customView);
            } catch (NullPointerException ignored) {
            }
            customLayout.addView(customView);
    }
}
}
