/**************************************************************************************************
 Filename:       KnockTrendsCustomTimer.java
 author:         Ian Parfitt

 This class handles the timeout function for listening for button presses.
 **************************************************************************************************/
package com.example.ti.util;

import java.util.Timer;
import java.util.TimerTask;
import com.example.ti.ble.sensortag.ButtonPressListener;

public class KnockTrendsCustomTimer {
    private Timer mTimer;
    private int mTimeout;
    private ButtonPressListener mListener = null;

    public KnockTrendsCustomTimer(int timeout, ButtonPressListener listener) {
        mTimeout = timeout;
        mTimer = new Timer();
        mListener = listener;
    }

    public void reset() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mTimer = new Timer();
            TimeoutTask timeout = new TimeoutTask();
            mTimer.schedule(timeout, 0, 1000); // One second tick
        }
    }

    public void start() {
        TimeoutTask timeout = new TimeoutTask();
        if(mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(timeout, 0, 1000); // One second tick
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private class TimeoutTask extends TimerTask {
        int i = 0;

        @Override
        public void run() {
            i++;
            if (i >= mTimeout) {
                mTimer.cancel();
                mTimer = null;
                if (mListener != null)
                    mListener.onTimeout();
            }
            // Else do nothing, this should listen in the background
        }
    }
}
