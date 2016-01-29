package com.hieptran.devicesmanager.utils.tweak;

import android.content.Context;

import com.hieptran.devicesmanager.common.root.CommandControl;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hiepth on 29/01/2016.
 */
public class IOScheduler implements Const {
    public enum StorageType {
        INTERNAL, EXTERNAL
    }
    public static boolean hasExternalStorage() {
        return Utils.existFile(IO_EXTERNAL_READ_AHEAD)
                || Utils.existFile(IO_EXTERNAL_SCHEDULER);
    }
    public static void setReadahead(StorageType type, int readahead, Context context) {
        CommandControl.runCommand(String.valueOf(readahead), type == StorageType.INTERNAL ? IO_INTERNAL_READ_AHEAD :
                IO_EXTERNAL_READ_AHEAD, CommandControl.CommandType.GENERIC, context);
    }

    public static int getReadahead(StorageType type) {
        String file = type == StorageType.INTERNAL ? IO_INTERNAL_READ_AHEAD
                : IO_EXTERNAL_READ_AHEAD;
        if (Utils.existFile(file)) {
            String values = Utils.readFile(file);
            if (values != null) return Utils.stringToInt(values);
        }
        return 0;
    }

    public static void setScheduler(StorageType type, String scheduler, Context context) {
        CommandControl.runCommand(scheduler, type == StorageType.INTERNAL ? IO_INTERNAL_SCHEDULER :
                IO_EXTERNAL_SCHEDULER, CommandControl.CommandType.GENERIC, context);
    }

    public static List<String> getSchedulers(StorageType type) {
        String file = type == StorageType.INTERNAL ? IO_INTERNAL_SCHEDULER
                : IO_EXTERNAL_SCHEDULER;
        if (Utils.existFile(file)) {
            String values = Utils.readFile(file);
            if (values != null) {
                String[] valueArray = values.split(" ");
                String[] out = new String[valueArray.length];

                for (int i = 0; i < valueArray.length; i++)
                    out[i] = valueArray[i].replace("[", "").replace("]", "");

                return new ArrayList<>(Arrays.asList(out));
            }
        }
        return null;
    }

    public static String getScheduler(StorageType type) {
        String file = type == StorageType.INTERNAL ? IO_INTERNAL_SCHEDULER
                : IO_EXTERNAL_SCHEDULER;
        if (Utils.existFile(file)) {
            String values = Utils.readFile(file);
            if (values != null) {
                String[] valueArray = values.split(" ");

                for (String value : valueArray)
                    if (value.contains("["))
                        return value.replace("[", "").replace("]", "");
            }
        }
        return "";
    }
}
