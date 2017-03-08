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

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.*;


public class ButtonPressListener {
    private KnockTrendsCustomTimer mTimer;
    private static ButtonPressListener instance = null;
    private List<Long> mList;
    private long currentMilliseconds;
    private long lastMilliseconds;
    private static String Endpoint = "http://www.techlikenew.com/test/admin/post.php";

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

        // Still not sure why the first value is useless, but this works. ¯\_(ツ)_/¯
        if (! mList.isEmpty() ) {
            mList.remove(0);
        }

        Long valuesToSend[] = mList.toArray(new Long[mList.size()]);
        if(! mList.isEmpty() ) {
            mList.clear();
        }

        makePost(valuesToSend);

        return valuesToSend;
    }

    public void buttonEvent(boolean event) {
        // parse message, determine if it's on or off

        // Ignore any "off" messages until we have an "on"
        if (!event && mList.isEmpty()) {
            return;
        }

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

    public void makePost(Long[] values){

        Gson gson = new GsonBuilder().create();
        String gSonData = gson.toJson(values);

        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody postData = new FormBody.Builder().add("data", gSonData).build();
            Request request = new Request.Builder().url(Endpoint).post(postData).build();
            Response response = client.newCall(request).execute();
            System.out.println(response);
        }catch(Exception e) {
            System.exit(0);
        }

    }
}
