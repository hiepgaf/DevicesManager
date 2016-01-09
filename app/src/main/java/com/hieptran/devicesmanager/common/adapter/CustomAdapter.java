package com.hieptran.devicesmanager.common.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.fragment.CommonFragment;

import java.util.List;

/**
 * Created by hieptran on 09/01/2016.
 */
public class CustomAdapter {
    public interface AdapView {
        void onBindViewHolder(RecyclerView.ViewHolder viewHolder);
        RecyclerView.ViewHolder onCreateViewHolder (ViewGroup viewGroup);
        String getTitle();
        CommonFragment getFragment();
    }
    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }
        public final List<AdapView> AdapViews;
        private OnItemClickListener onItemClickListener;
        private int selectedItem;
        private boolean itemOnly;

        public Adapter(List<AdapView> lsAdapViews) {
            AdapViews = lsAdapViews;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
           AdapViews.get(position).onBindViewHolder(holder);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return AdapViews.size();
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = AdapViews.get(viewType).onCreateViewHolder(parent);
            setOnClickListener(AdapViews.get(viewType), viewHolder.itemView);
            return viewHolder;
        }
        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
        private void setOnClickListener(final AdapView dView, View view) {
            boolean onClick = false;
            if (itemOnly)
                onClick = dView instanceof Item;
            else
                if (onItemClickListener != null) onClick = true;
            if (onClick) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null)
                            onItemClickListener.onItemClick(v, AdapViews.indexOf(dView));
                    }
                });
            }
        }
    }

    public static class Item implements AdapView {

        private final String title;
        private final CommonFragment fragment;
        private View view;
        private TextView text;
        private boolean checked;
        private int defaultTextColor;
        private int checkedTextColor;
        private int defaultBackgroundColor;
        private int checkedBackgroundColor;

        public Item(String title, CommonFragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public CommonFragment getFragment() {
            return fragment;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
            checkedTextColor = view.getResources().getColor(R.color.color_primary);
            defaultTextColor = view.getResources().getColor(/**Utils.DARKTHEME ? R.color.white : */R.color.black);
            defaultBackgroundColor = view.getResources().getColor(android.R.color.transparent);
            checkedBackgroundColor = view.getResources().getColor(/**Utils.DARKTHEME ? */
                    R.color.navigationdrawer_selected_background_dark /**: R.color.navigationdrawer_selected_background_light*/);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            view = viewHolder.itemView;
            text = (TextView) view.findViewById(R.id.text);
            text.setText(title);
            view.setBackgroundColor(checked ? checkedBackgroundColor : defaultBackgroundColor);
            text.setTextColor(checked ? checkedTextColor : defaultTextColor);
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
            if (view != null && text != null) {
                view.setBackgroundColor(checked ? checkedBackgroundColor : defaultBackgroundColor);
                text.setTextColor(checked ? checkedTextColor : defaultTextColor);
            }
        }

    }
}
