package com.hieptran.devicesmanager.utils.tweak;

import android.content.Context;
import android.util.Log;

import com.hieptran.devicesmanager.common.root.CommandControl;
import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hiepth on 29/01/2016.
 */
public class CPUVoltage implements Const{
    private static String CPU_VOLTAGE_FILE;
    private static String[] mCpuFreqs;

    public static List<String> getVoltages() {
        String value = Utils.readFileRoot(CPU_VOLTAGE_FILE);
        if(value != null) {
            String[] lines;
            //Bo dau cach cua output
            value = value.replace(" ","");
            switch (CPU_VOLTAGE_FILE) {
                case CPU_VDD_VOLTAGE:
                case CPU_FAUX_VOLTAGE:
                    lines = value.split("\\r?\\n");
                    break;
                default:
                    lines = value.split("mV");
                    break;
            }
            String[] voltages = new String[lines.length];
            for (int i = 0; i < voltages.length; i++) {
                String[] voltageLine;
                switch (CPU_VOLTAGE_FILE) {
                    case CPU_VDD_VOLTAGE:
                    case CPU_FAUX_VOLTAGE:
                        voltageLine = lines[i].split(":");
                        break;
                    default:
                        voltageLine = lines[i].split("mhz:");
                        break;
                }
                if (voltageLine.length > 1) {
                    switch (CPU_VOLTAGE_FILE) {
                        case CPU_FAUX_VOLTAGE:
                            voltages[i] = String.valueOf(Utils.stringToInt(voltageLine[1]) / 1000).trim();
                            break;
                        default:
                            voltages[i] = voltageLine[1].trim();
                            break;
                    }
                }
            }
            return new ArrayList<>(Arrays.asList(voltages));
        }
        return null;
    }
    //Kiem tra vddVoltage
    public static boolean isVddVoltage() {
        return CPU_VOLTAGE_FILE.equals(CPU_VDD_VOLTAGE) || CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE);
    }

    public static List<String> getFreqs() {
        if (mCpuFreqs == null) {
            String value = Utils.readFileRoot(CPU_VOLTAGE_FILE);
            if (value != null) {
                String[] lines;
                value = value.replace(" ", "");
                switch (CPU_VOLTAGE_FILE) {
                    case CPU_VDD_VOLTAGE:
                    case CPU_FAUX_VOLTAGE:
                        lines = value.split("\\r?\\n");
                        break;
                    default:
                        lines = value.split("mV");
                        break;
                }
                mCpuFreqs = new String[lines.length];
                for (int i = 0; i < lines.length; i++) {
                    switch (CPU_VOLTAGE_FILE) {
                        case CPU_VDD_VOLTAGE:
                        case CPU_FAUX_VOLTAGE:
                            mCpuFreqs[i] = lines[i].split(":")[0].trim();
                            break;
                        default:
                            mCpuFreqs[i] = lines[i].split("mhz:")[0].trim();
                            break;
                    }
                }
            }
        }
        if (mCpuFreqs == null) return null;
        return new ArrayList<>(Arrays.asList(mCpuFreqs));
    }
    //Kiem tra kernel ho tro CPUVolgate ?
    public static boolean hasCpuVoltage() {
        for(String file: CPU_VOLTAGE_ARRAY)
            if(Utils.existFile(file)) {
                CPU_VOLTAGE_FILE = file;
                Log.d("HiepTHb","Voltage file : "+CPU_VOLTAGE_FILE);
                return true;
            }
        return false;
    }
    public static void activateOverrideVmin(boolean active, Context context) {
        CommandControl.runCommand(active ? "1" : "0", CPU_OVERRIDE_VMIN, CommandControl.CommandType.GENERIC, context);
    }

    public static boolean isOverrideVminActive() {
        return Utils.readFileRoot(CPU_OVERRIDE_VMIN).equals("1");
    }

    public static boolean hasOverrideVmin() {
        return Utils.existFile(CPU_OVERRIDE_VMIN);
    }

    public static void setGlobalOffset(String voltage, Context context) {
        int adjust = Utils.stringToInt(voltage);
        String command = String.valueOf(adjust);

        switch (CPU_VOLTAGE_FILE) {
            case CPU_VDD_VOLTAGE:
            case CPU_FAUX_VOLTAGE:
                if (CPU_VOLTAGE_FILE.equals(CPU_FAUX_VOLTAGE))
                    command = String.valueOf(adjust * 1000);
                if (adjust > 0) command = "+" + command;
                break;
            default:
                command = "";
                for (String volt : getVoltages())
                    if (volt != null)
                        command += command.isEmpty() ? (Utils.stringToInt(volt) + adjust) :
                                " " + (Utils.stringToInt(volt) + adjust);
                break;
        }

        CommandControl.runCommand(command, CPU_VOLTAGE_FILE, CommandControl.CommandType.GENERIC, context);
    }

    public static void setVoltage(String freq, String voltage, Context context) {
        String command = "";
        int position = 0;
        for (int i = 0; i < getFreqs().size(); i++) {
            if (freq.equals(getFreqs().get(i)))
                position = i;
        }
        switch (CPU_VOLTAGE_FILE) {
            case CPU_VDD_VOLTAGE:
            case CPU_FAUX_VOLTAGE:
                command = getFreqs().get(position) + " " + Utils.stringToInt(voltage) * 1000;
                CommandControl.runCommand(command, CPU_VOLTAGE_FILE, CommandControl.CommandType.GENERIC, String.valueOf(position), context);
                break;
            default:
                List<String> voltages = getVoltages();
                for (int i = 0; i < voltages.size(); i++)
                    if (i == position)
                        command += command.isEmpty() ? voltage : " " + voltage;
                    else
                        command += command.isEmpty() ? voltages.get(i) : " " + voltages.get(i);
                CommandControl.runCommand(command, CPU_VOLTAGE_FILE, CommandControl.CommandType.GENERIC, context);
                break;
        }
    }
}
