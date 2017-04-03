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
        started = !started;
        activity.setDirectory();
        if(streamMode)
        {
           // activity.startStream();
        }
        else
        {
           // activity.startRecording();
        }
        Toast.makeText(context, "Started", Toast.LENGTH_LONG).show();
    }

    protected void onForwardSwipe(int speed)
    {
        streamMode = !streamMode;
        activity.streamMode = !activity.streamMode;
        Toast.makeText(context, "Mode Changed", Toast.LENGTH_LONG).show();
    }

    protected void onNear() {

    }

    protected void onFar() {

    }
}
