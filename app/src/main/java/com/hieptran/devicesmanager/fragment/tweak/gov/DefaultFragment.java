package com.hieptran.devicesmanager.fragment.tweak.gov;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.root.CommandControl;
import com.hieptran.devicesmanager.common.root.RootFile;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.tweak.CPU;

import java.util.List;

/**
 * Created by hieptran on 11/01/2016.
 */
public class DefaultFragment extends Fragment implements Const, Spinner.OnItemSelectedListener {
    LinearLayout main_vertical_view;
    MenuItem item_refresh;
    Handler hand;
    private String _current_gov;
    private TextView tv_current_gov;
    private Spinner avai_gov;
    private List<String> ls_avai_gov;
    private ArrayAdapter<String> adap_avai_gov;
    private int mState = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gov_tw,container,false);
        tv_current_gov = (TextView) v.findViewById(R.id.current_gov);
        avai_gov = (Spinner) v.findViewById(R.id.available_gov_spinner);
        main_vertical_view = (LinearLayout) v.findViewById(R.id.main_vertical_view);
        ls_avai_gov = CPU.getAvailableGovernors(0);
        adap_avai_gov = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ls_avai_gov);
        avai_gov.setAdapter(adap_avai_gov);
        adap_avai_gov.setNotifyOnChange(true);
        avai_gov.setOnItemSelectedListener(this);
        setStaticView();
        updateView();
        hand = new Handler();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void setStaticView() {
        _current_gov = Utils.readFile(String.format(CPU_SCALING_GOVERNOR, 0));
        tv_current_gov.setText("\t\t" + _current_gov);
        avai_gov.setSelection(ls_avai_gov.indexOf(_current_gov));
        //updateView();
        Log.d("HiepTHb", "setStaticView: " + "_current_gov " + _current_gov + " ls_avai_gov.indexOf " + ls_avai_gov.indexOf(_current_gov));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //avai_gov.setSelection(ls_avai_gov.indexOf(_current_gov));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CPU.setGovernor(adap_avai_gov.getItem(position), getActivity());
        // Toast.makeText(getContext(),String.format(getString(R.string.toast_set),_current_gov),Toast.LENGTH_LONG).show();
        mState = 1;
        setStaticView();
        getActivity().invalidateOptionsMenu();
    }

    private void updateView() {
        main_vertical_view.removeAllViews();
        final String path = getPath();
        if (path != null) {
            List<String> files = new RootFile(path).list();
            for (final String file : files) {
                final String value = Utils.readFile(path + "/" + file);
                if (value != null && !value.isEmpty() && !value.contains("\n")) {
                    LinearLayout gov_itemt = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.gov_item, main_vertical_view, false);
                    TextView lables = (TextView) gov_itemt.findViewById(R.id.lable_gov_items);
                    TextView valuess = (TextView) gov_itemt.findViewById(R.id.value_gov_items);
                    lables.setText(file.toString());
                    valuess.setText(value.toString());
                    gov_itemt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog(path + "/" + file, value);
                            if (DEBUG) {
                                Utils.showLog(path + "/" + file + "---" + value);
                            }
                        }
                    });

                    main_vertical_view.addView(gov_itemt);
                    main_vertical_view.addView(getActivity().getLayoutInflater().inflate(R.layout.div_view, main_vertical_view, false));
                }
            }
        }
    }

    public String getPath() {
        return getPath(CPU.isBigCluster() ? String.format(CPU_GOVERNOR_TUNABLES_CORE, 4) :
                CPU_GOVERNOR_TUNABLES, CPU.getCurGovernor(0, true));
    }

    private String getPath(String path, String governor) {
        if (Utils.existFile(path + "/" + governor)) return path + "/" + governor;
        else for (String file : new RootFile(path).list())
            if (governor.contains(file))
                return path + "/" + file;
        return null;
    }

    private void showDialog(final String file, String value) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(30, 30, 30, 30);

        final EditText editText = new EditText(getActivity());
        editText.setGravity(Gravity.CENTER);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        // if (!Utils.DARKTHEME)
        //     editText.setTextColor(getResources().getColor(R.color.black));
        editText.setText(value);

        layout.addView(editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout)
                .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CommandControl.runCommand(editText.getText().toString(), file, CommandControl.CommandType.GENERIC, getActivity());
                updateView();
            }
        }).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_item, menu);
        item_refresh = menu.findItem(R.id.refresh);
        if (mState == 1)
            item_refresh.setVisible(true);
        else
            item_refresh.setVisible(false);

        // getActivity().invalidateOptionsMenu();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == item_refresh) updateView();

        return super.onOptionsItemSelected(item);

    }
}
