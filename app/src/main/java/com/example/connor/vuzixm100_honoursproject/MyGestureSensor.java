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
    public MyGestureSensor(Context context)
    {
        super(context);
        this.context = context;
    }

    protected void onBackSwipe(int speed)
    {
        Toast.makeText(context, "Recognised: BackSwipe", Toast.LENGTH_LONG).show();
    }

    protected void onForwardSwipe(int speed)
    {
        Toast.makeText(context, "Recognised: ForwardSwipe", Toast.LENGTH_LONG).show();
    }

    protected void onNear() {

    }

    protected void onFar() {

    }
}
