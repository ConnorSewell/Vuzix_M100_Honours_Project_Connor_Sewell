package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;


import android.widget.Toast;

import com.vuzix.hardware.GestureSensor;

/**
 * Created by Connor on 18/03/2017.
 * Handles Gestures and what to do when certain gestures occur
 *
 * Gesture Sensor code taken from official docs: http://files.vuzix.com/Content/Upload/Driver_File_GestureSensorSDK_20160317210116857.pdf
 * ^ Accessed: 18/03/2017 @ 02:00
 */
public class MyGestureSensor extends GestureSensor
{
    Context context;
    Main activity;
    boolean streamMode = true;
    boolean started = false;
    int swipeIndex = 0;
    long lastTime;

    public MyGestureSensor(Context context, Main activity)
    {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    protected void onBackSwipe(int speed)
    {
        long currTime = System.currentTimeMillis();
        if(swipeIndex == 1)
        {
            if (currTime - lastTime <= 1500)
            {
                if (!running)
                {
                    if (streamMode)
                    {
                        running = true;
                        Toast.makeText(activity, "Stream Starting...", Toast.LENGTH_LONG).show();
                        activity.startStream();
                    } else
                    {
                        running = true;
                        Toast.makeText(activity, "Recording Starting...", Toast.LENGTH_LONG).show();
                        activity.startRecording();
                    }
                }
                else
                {
                    running = false;
                    activity.resetTimer();
                    activity.stopStream();

                    if (!streamMode)
                    {
                        activity.stopRecording();
                    }
                }
            }
        }
        lastTime = currTime;
        swipeIndex = 2;
    }

    boolean running = false;
    protected void onForwardSwipe(int speed)
    {
        long currTime = System.currentTimeMillis();

        if (swipeIndex == 2)
        {
            if (currTime - lastTime <= 1500)
            {
                if (!running)
                {
                    streamMode = !streamMode;
                    if (streamMode)
                    {
                        activity.setModeText("Mode: Streaming");
                    } else {
                        activity.setModeText("Mode: Storing");
                    }
                }
                else
                {
                    Toast.makeText(activity, "Cannot swap modes whilst streaming/recording", Toast.LENGTH_LONG).show();
                }
            }
    }

        lastTime = currTime;
        swipeIndex = 1;
    }


    protected void onNear() {

    }

    protected void onFar() {

    }
}
