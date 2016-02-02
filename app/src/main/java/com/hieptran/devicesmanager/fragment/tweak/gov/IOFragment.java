package com.hieptran.devicesmanager.fragment.tweak.gov;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.utils.tweak.IOScheduler;

/**
 * Created by hieptran on 11/01/2016.
 */
public class IOFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    AppCompatSpinner internal_scheduler;
    ArrayAdapter<String> internal_scheduler_ls;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.io_fragment, container, false);
        ((TextView) v.findViewById(R.id.header_view)).setText(R.string.internal_storage);
        internal_scheduler = (AppCompatSpinner) v.findViewById(R.id.available_io_spinner);
        initView();
        return v;
    }

    private void initView() {
        internal_scheduler_ls = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,IOScheduler.getSchedulers(IOScheduler.StorageType.INTERNAL));
        internal_scheduler.setAdapter(internal_scheduler_ls);
        internal_scheduler_ls.setNotifyOnChange(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        IOScheduler.setScheduler(IOScheduler.StorageType.INTERNAL, internal_scheduler_ls.getItem(position), getContext());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
