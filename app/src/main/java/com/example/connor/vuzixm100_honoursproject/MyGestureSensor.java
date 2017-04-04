package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;


import android.widget.Toast;

import com.vuzix.hardware.GestureSensor;

/**
 * Created by Connor on 18/03/2017.
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
    public MyGestureSensor(Context context, Main activity)
    {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    protected void onBackSwipe(int speed)
    {
        if(!running)
        {
             streamMode = !streamMode;
             if (streamMode)
             {
                activity.setModeText("Mode: Streaming");
             }
             else
             {
                activity.setModeText("Mode: Storing");
             }
        }
        else
        {
            Toast.makeText(activity, "Cannot swap modes whilst streaming/recording" , Toast.LENGTH_LONG);
        }
    }

    boolean running = false;
    protected void onForwardSwipe(int speed)
    {
        if(!running)
        {
            activity.setDirectory();
            if (streamMode)
            {
                running = true;
                activity.startStream();
            } else
            {
                running = true;
                activity.startRecording();
            }
        }
        else
        {
            running = false;
            activity.resetTimer();
            activity.stopStream();
        }
    }


    protected void onNear() {

    }

    protected void onFar() {

    }
}
