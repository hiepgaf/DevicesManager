package com.hieptran.devicesmanager.common.adapter;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.fragment.CommonFragment;

/**
 * Created by hieptran on 09/01/2016.
 */
public class AdapterDivider extends AdapterParent {
    private View parentView;

    private TextView titleView;
    private View moreView;

    private String titleText;
    private String descriptionText;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        super.onBindViewHolder(viewHolder);

        parentView = viewHolder.itemView;

        titleView = (TextView) parentView.findViewById(R.id.title);
        moreView = parentView.findViewById(R.id.more_view);

        if (titleText != null) titleView.setText(titleText);
        setUpDescription();
        setFullSpan(true);
    }

    public void setText(String text) {
        titleText = text;
        if (titleView != null) titleView.setText(titleText);
    }

    public void setDescription(String description) {
        descriptionText = description;
        setUpDescription();
    }

    private void setUpDescription() {
        if (parentView != null && moreView != null && descriptionText != null) {
            moreView.setVisibility(View.VISIBLE);
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(parentView.getContext()).setMessage(descriptionText).show();
                }
            });
        }
    }

    @Override
    public View getView(ViewGroup viewGroup) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.divider_view, viewGroup, false);
    }

}
