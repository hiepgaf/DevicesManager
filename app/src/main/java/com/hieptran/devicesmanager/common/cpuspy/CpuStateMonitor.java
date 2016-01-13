//-----------------------------------------------------------------------------
//
// (C) Brandon Valosek, 2011 <bvalosek@gmail.com>
//
//-----------------------------------------------------------------------------

package com.hieptran.devicesmanager.common.cpuspy;

// imports

import android.os.SystemClock;

import com.hieptran.devicesmanager.utils.Const;
import com.hieptran.devicesmanager.utils.Utils;
import com.hieptran.devicesmanager.utils.tweak.CPU;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CpuStateMonitor is a class responsible for querying the system and getting
 * the time-in-state information, as well as allowing the user to set/reset
 * offsets to "restart" the state timers
 */
public class CpuStateMonitor implements Const{


    private List<CpuState> _states = new ArrayList<>();
    private Map<Integer, Long> _offsets = new HashMap<>();
//Added by hieptran
private List<CpuState> _states_big = new ArrayList<>();
    private Map<Integer, Long> _offsets_big = new HashMap<>();
    /**
     * @return List of CpuState with the offsets applied
     */
    public List<CpuState> getStates() {
        List<CpuState> states = new ArrayList<>();

        /* check for an existing offset, and if it's not too big, subtract it
         * from the duration, otherwise just add it to the return List */
        for (CpuState state : _states) {
            long duration = state.duration;
            if (_offsets.containsKey(state.freq)) {
                long offset = _offsets.get(state.freq);
                if (offset <= duration) {
                    duration -= offset;
                } else {
                    /* offset > duration implies our offsets are now invalid,
                     * so clear and recall this function */
                    _offsets.clear();
                    return getStates();
                }
            }

            states.add(new CpuState(state.freq, duration));
        }

        return states;
    }
    public List<CpuState> getStates_big() {
        List<CpuState> states = new ArrayList<CpuState>();

        /* check for an existing offset, and if it's not too big, subtract it
         * from the duration, otherwise just add it to the return List */
        for (CpuState state : _states_big) {
            long duration = state.duration;
            if (_offsets_big.containsKey(state.freq)) {
                long offset = _offsets_big.get(state.freq);
                if (offset <= duration) {
                    duration -= offset;
                } else {
                    /* offset > duration implies our offsets are now invalid,
                     * so clear and recall this function */
                    _offsets_big.clear();
                    return getStates();
                }
            }

            states.add(new CpuState(state.freq, duration));
        }

        return states;
    }
    /**
     * @return Sum of all state durations including deep sleep, accounting
     * for offsets
     */
    public long getTotalStateTime() {
        long sum = 0;
        long offset = 0;

        for (CpuState state : _states) {
            sum += state.duration;
        }

        for (Map.Entry<Integer, Long> entry : _offsets.entrySet()) {
            offset += entry.getValue();
        }

        return sum - offset;
    }
    public long getTotalStateTime_big() {
        long sum = 0;
        long offset = 0;

        for (CpuState state : _states_big) {
            sum += state.duration;
        }

        for (Map.Entry<Integer, Long> entry : _offsets_big.entrySet()) {
            offset += entry.getValue();
        }

        return sum - offset;
    }

    /**
     * @return Map of freq->duration of all the offsets
     */
    public Map<Integer, Long> getOffsets() {
        return _offsets;
    }
    public Map<Integer, Long> getOffsets_big() {
        return _offsets_big;
    }
    /**
     * Sets the offset map (freq->duration offset)
     */
    public void setOffsets(Map<Integer, Long> offsets) {
        _offsets = offsets;
    }
    public void setOffsets_big(Map<Integer, Long> offsets) {
        _offsets_big = offsets;
    }
    /**
     * Updates the current time in states and then sets the offset map to the
     * current duration, effectively "zeroing out" the timers
     */
    public void setOffsets() throws CpuStateMonitorException {
        _offsets.clear();
        updateStates();

        for (CpuState state : _states) {
            _offsets.put(state.freq, state.duration);
        }
    }
    public void setOffsets_big() throws CpuStateMonitorException {
        _offsets_big.clear();
        updateStates();

        for (CpuState state : _states_big) {
            _offsets_big.put(state.freq, state.duration);
        }
    }
    /**
     * removes state offsets
     */
    public void removeOffsets() {
        _offsets.clear();
    }

    /**
     * @return a list of all the CPU frequency states, which contains
     * both a frequency and a duration (time spent in that state
     */
    public List<CpuState> updateStates ()
            throws CpuStateMonitorException {
        /* attempt to create a buffered reader to the time in state
         * file and read in the states to the class */
        InputStream is;
        try {
            if(Utils.existFile(String.format(CPU_TIME_STATE, 0))) is = new FileInputStream(String.format(CPU_TIME_STATE,0));
            else is = new FileInputStream(String.format(CPU_TIME_STATE_2, 0));
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            _states.clear();
            readInStates(br);
            is.close();
        } catch (IOException e) {
            throw new CpuStateMonitorException(
                    "Problem opening time-in-states file");
        }

        /* deep sleep time determined by difference between elapsed
         * (total) boot time and the system uptime (awake) */
        long sleepTime = (SystemClock.elapsedRealtime()
                - SystemClock.uptimeMillis()) / 10;
        _states.add(new CpuState(0, sleepTime));

        Collections.sort(_states, Collections.reverseOrder());

        return _states;
    }

    /**
     *     Added by hieptran
     *     Su dung de lay time state cua big pluse
     */

    public List<CpuState> updateStates_Big()
            throws CpuStateMonitorException {
        /* attempt to create a buffered reader to the time in state
         * file and read in the states to the class */
        InputStream is;
        try {
            if(Utils.existFile(String.format(CPU_TIME_STATE, 4))) is = new FileInputStream(String.format(CPU_TIME_STATE, 4));
            else is = new FileInputStream(String.format(CPU_TIME_STATE_2, 4));
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            _states_big.clear();
            readInStates_big(br);
            is.close();
        } catch (IOException e) {
            throw new CpuStateMonitorException(
                    "Problem opening time-in-states file");
        }

        /* deep sleep time determined by difference between elapsed
         * (total) boot time and the system uptime (awake) */
        long sleepTime = (SystemClock.elapsedRealtime()
                - SystemClock.uptimeMillis()) / 10;
        _states_big.add(new CpuState(0, sleepTime));

        Collections.sort(_states_big, Collections.reverseOrder());

        return _states_big;
    }

    /**
     * read from a provided BufferedReader the state lines into the
     * States member field
     */
    private void readInStates(BufferedReader br)
            throws CpuStateMonitorException {
        try {
            String line;
            while ((line = br.readLine()) != null) {
                // split open line and convert to Integers
                String[] nums = line.split(" ");
                _states.add(new CpuState(
                        Integer.parseInt(nums[0]),
                        Long.parseLong(nums[1])));
            }
        } catch (IOException e) {
            throw new CpuStateMonitorException(
                    "Problem processing time-in-states file");
        }
    }
    private void readInStates_big(BufferedReader br)
            throws CpuStateMonitorException {
        try {
            String line;
            while ((line = br.readLine()) != null) {
                // split open line and convert to Integers
                String[] nums = line.split(" ");
                _states_big.add(new CpuState(
                        Integer.parseInt(nums[0]),
                        Long.parseLong(nums[1])));
            }
        } catch (IOException e) {
            throw new CpuStateMonitorException(
                    "Problem processing time-in-states file");
        }
    }

    /**
     * exception class
     */
    public class CpuStateMonitorException extends Exception {
        public CpuStateMonitorException(String s) {
            super(s);
        }
    }

    /**
     * simple struct for states/time
     */
    public class CpuState implements Comparable<CpuState> {
        public int freq = 0;
        public long duration = 0;

        /**
         * init with freq and duration
         */
        public CpuState(int a, long b) {
            freq = a;
            duration = b;
        }

        /**
         * for sorting, compare the freqs
         */
        public int compareTo(CpuState state) {
            Integer a = new Integer(freq);
            Integer b = new Integer(state.freq);
            return a.compareTo(b);
        }
    }
}
