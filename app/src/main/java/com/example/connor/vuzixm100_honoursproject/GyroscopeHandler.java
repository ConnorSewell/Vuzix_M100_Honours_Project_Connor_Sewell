package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Handles Gyroscope data gathering/streaming
 * https://developer.android.com/guide/topics/sensors/sensors_overview.html
 * ^ All sensor functionality/code taken from the above link. Accessed: 17/01/2017 @ 19:13.
 */
public class GyroscopeHandler implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor gyroscopeSensor;
    private Context context;

    public GyroscopeHandler(Context context)
    {
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event)
    {

    }
}
