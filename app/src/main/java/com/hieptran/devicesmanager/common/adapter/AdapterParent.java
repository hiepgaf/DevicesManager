package com.hieptran.devicesmanager.common.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.fragment.CommonFragment;

/**
 * Created by hieptran on 09/01/2016.
 */
public abstract class AdapterParent implements CustomAdapter.AdapView{
    private boolean fullspan;
    private View view;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new RecyclerView.ViewHolder(getView(viewGroup)) {
        };
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public CommonFragment getFragment() {
        return null;
    }
    public abstract View getView(ViewGroup viewGroup);

    public void setFullSpan(boolean fullspan) {
        this.fullspan = fullspan;
        setUpLayout();
    }
    /**
     * Hamf thiet lap span cho layout
     */
    private void setUpLayout() {
        if (fullspan && view != null) {
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            int padding = view.getContext().getResources().getDimensionPixelSize(R.dimen.basecard_padding);
            layoutParams.setMargins(padding, padding, padding, 0);
            view.setLayoutParams(layoutParams);
        }
    }

}
