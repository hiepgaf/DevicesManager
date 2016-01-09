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

package com.hieptran.devicesmanager.cardview;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.CardViewCommon;
import com.hieptran.devicesmanager.common.adapter.AdapterParent;

/**
 * Created by willi on 25.04.15.
 */
public class InformationCardView extends CardViewCommon {

    private TextView infoView;
    private String infoText;



    public InformationCardView(Context context) {
        super(context, R.layout.information_cardview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) setTranslationZ(getResources()
                .getDimensionPixelSize(R.dimen.information_card_elevation));
    }

    @Override
    public void setContentLayout(View view) {
        infoView = (TextView) view.findViewById(R.id.info_text);

        if (infoText != null) infoView.setText(infoText);
    }


    public void setText(String text) {
        infoText = text;
        if (infoView != null) infoView.setText(infoText);
    }

    public static class DInformationCard extends AdapterParent {

        private InformationCardView informationCardView;
        private String infoText;

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            super.onBindViewHolder(viewHolder);

            informationCardView = (InformationCardView) viewHolder.itemView;

            if (infoText != null) informationCardView.setText(infoText);
            setFullSpan(true);
        }

        @Override
        public View getView(ViewGroup viewGroup) {
            return new InformationCardView(viewGroup.getContext());
        }

        public void setText(String text) {
            infoText = text;
            if (informationCardView != null) informationCardView.setText(infoText);
        }

    }

}
