package com.hieptran.devicesmanager.fragment.device;

import android.app.ActivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hieptran.devicesmanager.R;
import com.hieptran.devicesmanager.cardview.CardViewItem;
import com.hieptran.devicesmanager.fragment.RecyclerViewFragment;
import com.hieptran.devicesmanager.fragment.ViewPagerFragment;
import com.hieptran.devicesmanager.utils.Const;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hieptran on 09/01/2016.
 */
public class DeviceFragment extends Fragment {
    private TextView mModel;
    private TextView mManufacturer;
    private TextView mBoard;
    private TextView mHardWare;
    private TextView mScreenSz;
    private TextView mScreenDst;
    private TextView mTotalRAM;
    private TextView mAvaiRAM;
    private TextView mAvaiStorage;
    private TextView mTotalStorage;
    public DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
    public static final String ERROR = "Error while get infomations";

    public DeviceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_device_info, container, false);
        mModel = (TextView) rootView.findViewById(R.id.device_model);
        mManufacturer = (TextView) rootView.findViewById(R.id.device_manu);
        mBoard = (TextView) rootView.findViewById(R.id.device_board);
        mHardWare = (TextView) rootView.findViewById(R.id.device_hardware);
        mScreenSz = (TextView) rootView.findViewById(R.id.device_scr_sz);
        mScreenDst = (TextView) rootView.findViewById(R.id.device_scr_dst);
        mTotalRAM = (TextView) rootView.findViewById(R.id.device_total_ram);
        mAvaiRAM = (TextView) rootView.findViewById(R.id.device_avai_ram);
        mTotalStorage = (TextView) rootView.findViewById(R.id.device_total_storage);
        mAvaiStorage = (TextView) rootView.findViewById(R.id.device_avai_storage);
        new setAvaiRAM().execute();
        _setStaticVar();
        return rootView;
    }

    public void _setStaticVar() {
        mModel.setText(android.os.Build.MODEL);
        mManufacturer.setText(Build.MANUFACTURER);
        mBoard.setText(Build.BOARD);
        mHardWare.setText(Build.HARDWARE);
        mScreenSz.setText(readScreenInfo());
        mScreenDst.setText(twoDecimalForm.format(getActivity().getResources().getDisplayMetrics().densityDpi) + " dpi");
        mTotalStorage.setText(getTotalInternalMemorySize());
        mAvaiStorage.setText(getAvailableInternalMemorySize());
        setRAMInfo();
    }

    private String readScreenInfo() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return String.valueOf(twoDecimalForm.format(screenInches) + " inches");
    }

    private void setRAMInfo() {
        ActivityManager actManager = (ActivityManager) getActivity().getSystemService(getContext().ACTIVITY_SERVICE);
        final ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        mTotalRAM.setText(getTotalRAM());

    }

    public String getTotalRAM() {

        RandomAccessFile reader = null;
        String load = null;

        double totRam = 0;
        double avaiRam = 0;
        String lastValue = "";
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);

                // System.out.println("Ram : " + value);
            }
            reader.close();

            totRam = Double.parseDouble(value);
            // totRam = totRam / 1024;

            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }

        return lastValue;
    }

    class setAvaiRAM extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            ActivityManager actManager = (ActivityManager) getActivity().getSystemService(getContext().ACTIVITY_SERVICE);
            final ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            publishProgress(String.valueOf(twoDecimalForm.format(memInfo.availMem / 1048576)));
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mAvaiRAM.setText(values[0] + " MB");
        }
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        double blockSize = stat.getBlockSize();
        double availableBlocks = stat.getAvailableBlocks();
        return String.valueOf(twoDecimalForm.format(availableBlocks * blockSize / 1024 / 1024 / 1024)) + " GB";
    }

    public String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        double blockSize = stat.getBlockSize();
        double totalBlocks = stat.getBlockCount();
        return String.valueOf(twoDecimalForm.format(totalBlocks * blockSize / 1024 / 1024 / 1024)) + " GB";
    }

    public String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            double blockSize = stat.getBlockSize();
            double availableBlocks = stat.getAvailableBlocks();
            return String.valueOf(twoDecimalForm.format(availableBlocks * blockSize / 1024 / 1024 / 1024)) + " GB";
        } else {
            return ERROR;
        }
    }

    public String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            double blockSize = stat.getBlockSize();
            double totalBlocks = stat.getBlockCount();
            return String.valueOf(twoDecimalForm.format(totalBlocks * blockSize / 1024 / 1024 / 1024)) + " GB";
        } else {
            return ERROR;
        }
    }

    public String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    size /= 1024;
                }
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
}