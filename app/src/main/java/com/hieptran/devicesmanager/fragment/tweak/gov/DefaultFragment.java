package com.hieptran.devicesmanager.fragment.tweak.gov;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hieptran.commonlibs.andoid.common.contrib.Util;
import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.common.root.CommandControl;
import com.hieptran.devicesmanager.common.root.RootFile;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.tweak.CPU;

import java.util.ArrayList;
import java.util.List;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogFragment;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by hieptran on 11/01/2016.
 */
public class DefaultFragment extends Fragment implements Const, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    protected boolean inhibit_spinner = true;
    LinearLayout main_vertical_view;
    MenuItem item_refresh;
    Handler hand;
    MaterialDialog mSflashDialog;
    static int mCores;
    private String _current_gov;
    private TextView tv_current_gov;
    private Spinner avai_gov;
    private List<String> ls_avai_gov;
    private ArrayAdapter<String> adap_avai_gov;
    private int mState = 0;
    static  private ListViewCompat mListView;
    static  private ArrayList<String> mLableList, mValueList;
    static private GovernorAdapter mGovAdapter;
    static Context mContext;
    static Activity mActivity;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showDialog(mLableList.get(position),path + "/" + mLableList.get(position), mValueList.get(position));
        Utils.setFullscreen(getActivity());
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
        mContext = getContext();
        mActivity = getActivity();
        mLableList = new ArrayList<>();
        mValueList = new ArrayList<>();
        mListView = (ListViewCompat) v.findViewById(R.id.lv_main);
        mGovAdapter = new GovernorAdapter(mLableList, mValueList);
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

    public static void UpdateView2() {
        mLableList.clear();
        mValueList.clear();
        final String path = getPath(CPU.isBigCluster() ? String.format(CPU_GOVERNOR_TUNABLES_CORE, 4) :
                CPU_GOVERNOR_TUNABLES, CPU.getCurGovernor(mCores, true));
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
     //   getActivity().runOnUiThread(new Runnable() {
      //      @Override
      //      public void run() {

                mListView.setAdapter(mGovAdapter);
                Log.d("HiepTHb", "UpdateView2" + mGovAdapter.getCount());
                mGovAdapter.notifyDataSetChanged();
      //      }
     //   });

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
    static String mLableItem;
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

                            showDialog(file.toString(),path + "/" + file, value);
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

    private static String getPath(String path, String governor) {
        if (Utils.existFile(path + "/" + governor)) return path + "/" + governor;
        else for (String file : new RootFile(path).list())
            if (governor.contains(file))
                return path + "/" + file;
        return null;
    }

    private void showDialog(String lable,final String file, String value) {

        EditTextDialog  fragment
                = EditTextDialog.newInstance(
                5,
                5,
                false,
                false,
                lable,
                file,value,mCores);
        fragment.show(getActivity().getFragmentManager(),"blur");
        /*LinearLayout layout = new LinearLayout(getActivity());
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
        }).show();*/
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
            if(!Utils.getBoolean("dark_theme",false,mContext))
                gov_itemt.setBackgroundResource(R.drawable.bg_shadow_white);
            else             gov_itemt.setBackgroundResource(R.drawable.bg_shadow_);
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


    static class EditTextDialog extends BlurDialogFragment {

        /**
         * Bundle key used to start the blur dialog with a given scale factor (float).
         */
        private static final String BUNDLE_KEY_DOWN_SCALE_FACTOR = "bundle_key_down_scale_factor";

        /**
         * Bundle key used to start the blur dialog with a given blur radius (int).
         */
        private static final String BUNDLE_KEY_BLUR_RADIUS = "bundle_key_blur_radius";

        /**
         * Bundle key used to start the blur dialog with a given dimming effect policy.
         */
        private static final String BUNDLE_KEY_DIMMING = "bundle_key_dimming_effect";

        /**
         * Bundle key used to start the blur dialog with a given debug policy.
         */
        private static final String BUNDLE_KEY_DEBUG = "bundle_key_debug_effect";

        private int mRadius;
        private float mDownScaleFactor;
        private boolean mDimming;
        private boolean mDebug;
        static String mFile = "", mValue = "",mLable;
        static int mCores;

        /**
         * Retrieve a new instance of the sample fragment.
         *
         * @param radius          blur radius.
         * @param downScaleFactor down scale factor.
         * @param dimming         dimming effect.
         * @param debug           debug policy.
         * @return well instantiated fragment.
         */
        public static EditTextDialog newInstance(int radius,
                                                         float downScaleFactor,
                                                         boolean dimming,
                                                         boolean debug,
                                                 String lable,
                                                         String file, String value, int core) {
            EditTextDialog fragment = new EditTextDialog();
            Bundle args = new Bundle();
            mLable =lable;
            mFile = file;
            mValue = value;
            mCores = core;
            args.putInt(
                    BUNDLE_KEY_BLUR_RADIUS,
                    radius
            );
            args.putFloat(
                    BUNDLE_KEY_DOWN_SCALE_FACTOR,
                    downScaleFactor
            );
            args.putBoolean(
                    BUNDLE_KEY_DIMMING,
                    dimming
            );
            args.putBoolean(
                    BUNDLE_KEY_DEBUG,
                    debug
            );

            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            Bundle args = getArguments();
            mRadius = args.getInt(BUNDLE_KEY_BLUR_RADIUS);
            mDownScaleFactor = args.getFloat(BUNDLE_KEY_DOWN_SCALE_FACTOR);
            mDimming = args.getBoolean(BUNDLE_KEY_DIMMING);
            mDebug = args.getBoolean(BUNDLE_KEY_DEBUG);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.edittext_dialog,null);
            final EditText editText = (EditText) layout.findViewById(R.id.context_msg);
            final TextView mTextView = (TextView) layout.findViewById(R.id.header_dialog);
            mTextView.setText(mLable);
            editText.setText(mValue);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(layout)
                    .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.exitFullscreen(mActivity);
                            dismiss();
                        }
                    }).setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mCores == CPU.getBigCore()) {
                        Log.d("HiepTHb", "BigCore - set param");
                        CommandControl.runCommand(editText.getText().toString(), mFile, CommandControl.CommandType.CPU, getActivity());
                    } else {
                        Log.d("HiepTHb", "BigCore - set param");
                        CommandControl.runCommand(editText.getText().toString(), mFile, CommandControl.CommandType.GENERIC, getActivity());
                    }
                    // updateView();
                    UpdateView2();
                }
            });
           // builder.create().getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
           // builder.create().getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            AlertDialog mDialog = builder.create();
            mDialog.setCanceledOnTouchOutside(false);
            return mDialog;
        }

        @Override
        protected boolean isDebugEnable() {
            return mDebug;
        }

        @Override
        protected boolean isDimmingEnable() {
            return mDimming;
        }

        @Override
        protected boolean isActionBarBlurred() {
            return true;
        }

        @Override
        protected float getDownScaleFactor() {
            return mDownScaleFactor;
        }

        @Override
        protected int getBlurRadius() {
            return mRadius;
        }
    }
}