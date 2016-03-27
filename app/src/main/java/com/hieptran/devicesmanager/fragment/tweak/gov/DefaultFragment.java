package com.hieptran.devicesmanager.fragment.tweak.gov;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListViewCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by hieptran on 11/01/2016.
 */
public class DefaultFragment extends Fragment implements Const, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener{
    protected boolean inhibit_spinner = true;
    LinearLayout main_vertical_view;
    MenuItem item_refresh;
    Handler hand;
    MaterialDialog mSflashDialog;
    int mCores;
    private String _current_gov;
    private TextView tv_current_gov;
    private Spinner avai_gov;
    private List<String> ls_avai_gov;
    private ArrayAdapter<String> adap_avai_gov;
    private int mState = 0;
    private ListViewCompat mListView;
    private ArrayList<String> mLableList, mValueList;
    private GovernorAdapter mGovAdapter;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showDialog(path + "/" + mLableList.get(position), mValueList.get(position));
    }

    public DefaultFragment(int core) {
        mCores = core;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gov_tw, container, false);
        tv_current_gov = (TextView) v.findViewById(R.id.current_gov);
        avai_gov = (Spinner) v.findViewById(R.id.available_gov_spinner);
        //  main_vertical_view = (LinearLayout) v.findViewById(R.id.main_vertical_view);
        ls_avai_gov = CPU.getAvailableGovernors(mCores);
        adap_avai_gov = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ls_avai_gov);
        avai_gov.setAdapter(adap_avai_gov);
        mLableList = new ArrayList<>();
        mValueList = new ArrayList<>();
        mListView = (ListViewCompat) v.findViewById(R.id.lv_main);
        mListView.setOnItemClickListener(this);
        mSflashDialog = new MaterialDialog(getContext());
        adap_avai_gov.setNotifyOnChange(true);
        avai_gov.setOnItemSelectedListener(this);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setStaticView();
                //  updateView();
                UpdateView2();
            }
        });

        hand = new Handler();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void setStaticView() {
        _current_gov = CPU.getCurGovernor(mCores, true);
        tv_current_gov.setText("\t\t" + _current_gov);
        avai_gov.setSelection(ls_avai_gov.indexOf(_current_gov));
        //updateView();
        Log.d("HiepTHb", "setStaticView: " + "_current_gov " + _current_gov + " ls_avai_gov.indexOf " + ls_avai_gov.indexOf(_current_gov));
    }

    private void UpdateView2() {
        mLableList.clear();
        mValueList.clear();
        final String path = getPath();
        if (path != null) {
            List<String> files = new RootFile(path).list();
            for (final String file : files) {
                final String value = Utils.readFileRoot(path + "/" + file);
                if (value != null && !value.isEmpty() && !value.contains("\n")) {

                    mLableList.add(file.toString());
                    mValueList.add(value.toString());

                }
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGovAdapter = new GovernorAdapter(mLableList, mValueList);
                mListView.setAdapter(mGovAdapter);
                Log.d("HiepTHb", "UpdateView2" + mGovAdapter.getCount());
                mGovAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //avai_gov.setSelection(ls_avai_gov.indexOf(_current_gov));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (inhibit_spinner) {
            inhibit_spinner = false;
            CPU.setGovernor(adap_avai_gov.getItem(position), getActivity());

        } else {
            mSflashDialog = new MaterialDialog(getContext())
                    .setTitle("Set Governor")
                    .setMessage("Change governor to " + ls_avai_gov.get(position) + "?")
                    .setPositiveButton("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mState = 1;
                            setStaticView();
                            getActivity().invalidateOptionsMenu();
                            //   updateView();
                            UpdateView2();
                            mSflashDialog.dismiss();

                        }
                    })
                    .setNegativeButton("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSflashDialog.dismiss();
                        }
                    });
            mSflashDialog.show();
            if (mCores == CPU.getBigCore())
                CPU.setGovernor(adap_avai_gov.getItem(position), getActivity());
            else
                CPU.setGovernor(CommandControl.CommandType.CPU_LITTLE, adap_avai_gov.getItem(position), getActivity());


        }
    }
    final String path = getPath();
    private void updateView() {
        main_vertical_view.removeAllViews();
        //final String path = getPath();
        if (path != null) {
            List<String> files = new RootFile(path).list();
            for (final String file : files) {
                final String value = Utils.readFileRoot(path + "/" + file);
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
                CPU_GOVERNOR_TUNABLES, CPU.getCurGovernor(mCores, true));
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
                if (mCores == CPU.getBigCore()) {
                    Log.d("HiepTHb", "BigCore - set param");
                    CommandControl.runCommand(editText.getText().toString(), file, CommandControl.CommandType.CPU, getActivity());
                } else {
                    Log.d("HiepTHb", "BigCore - set param");
                    CommandControl.runCommand(editText.getText().toString(), file, CommandControl.CommandType.GENERIC, getActivity());
                }
                // updateView();
                UpdateView2();
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
        if (item == item_refresh)// updateView();
            UpdateView2();

        return super.onOptionsItemSelected(item);

    }

    class GovernorAdapter extends BaseAdapter {
        private TextView mLable, mValue;
        final String path = getPath();
        private ArrayList<String> mLableList, mValueList;
        String lable, value;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gov_itemt = inflater.inflate(R.layout.gov_item, null);
            mLable = (TextView) gov_itemt.findViewById(R.id.lable_gov_items);
            mValue = (TextView) gov_itemt.findViewById(R.id.value_gov_items);
            lable = mLableList.get(position);
            value = mValueList.get(position);
            mLable.setText(lable);
            mValue.setText(value);
            return gov_itemt;
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mLableList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        public GovernorAdapter(ArrayList<String> lable, ArrayList<String> value) {
            //super(context, resource, objects);
            this.mLableList = lable;
            this.mValueList = value;
        }
        public String getLable(int i) {
            return mLableList.get(i);
        }
        public String getValue(int i) {
            return mValueList.get(i);
        }
        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }
}
