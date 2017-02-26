/**************************************************************************************************
 * author: Ian Parfitt
 * file: ButtonPressListener.java
 *
 * This class handles determining how long button presses have happened for, and builds an array of
 * button press data, to be parsed as a button pattern for unlocking a door.  Packages results as
 * an array
 */
package com.example.ti.ble.sensortag;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import com.example.ti.util.KnockTrendsCustomTimer;

public class ButtonPressListener {
    private KnockTrendsCustomTimer mTimer;
    private static ButtonPressListener instance = null;
    private List<Long> mList;
    private long currentMilliseconds;
    private long lastMilliseconds;

    // Singleton protected constructor.  NO ONE GETS IN.
    private ButtonPressListener() {
        mTimer = new KnockTrendsCustomTimer(2, this); // 2 second timeout
        mList = new ArrayList<Long>();
        currentMilliseconds = 0;
        lastMilliseconds = 0;
    }

    // Publicly available "constructor"
    public static ButtonPressListener getInstance() {
        if(instance == null) {
            instance = new ButtonPressListener();
        }
        return instance;
    }

    private long buttonOff() {
        Date date = new Date();
        long milliseconds = date.getTime();
        mTimer.start();
        return milliseconds;
    }

    private long buttonOn() {
        Date date = new Date();
        long milliseconds = date.getTime();
        mTimer.stop();
        return milliseconds;
    }

    private Long[] resetValues() {
        Long valuesToSend[] = mList.toArray(new Long[mList.size()]);
        if(! mList.isEmpty() ) {
            mList.clear();
        }
        return valuesToSend;
    }

    public void buttonEvent(boolean event) {
        // parse message, determine if its on or off
        long millisecondDiff = 0;
        if (event) {
            // Button is pressed
            currentMilliseconds = buttonOn();
        }
        else {
            // Button is off
            currentMilliseconds = buttonOff();
        }
        // diff current timestamp from the last timestamp
        millisecondDiff = currentMilliseconds - lastMilliseconds;

        // push integer millisecond value to array
        mList.add(millisecondDiff);

        // set the new "last" value
        lastMilliseconds = currentMilliseconds;
    }

    public Long[] onTimeout() {
        mTimer.reset();
        return resetValues();
    }
}