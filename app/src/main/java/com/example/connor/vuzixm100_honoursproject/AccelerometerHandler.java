package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Handles accelerometer data gathering/streaming
 * https://developer.android.com/guide/topics/sensors/sensors_overview.html
 * ^ All sensor functionality/code taken from the above link. Accessed: 16/01/2017 @ 18:57.
 */
public class AccelerometerHandler implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor accelerometerSensor;
    private Context context;
    private float[] gravity, linearAcceleration;
    String infoLogTag = "INFO: ";

    public AccelerometerHandler(Context context)
    {
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = new float[3];
        linearAcceleration = new float[3];
        //Max sample rate
        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Log.i(infoLogTag, "Accelerometer accuracy changed...");
    }

    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        final float alpha = 0.8f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linearAcceleration[0] = event.values[0] - gravity[0];
        linearAcceleration[1] = event.values[1] - gravity[1];
        linearAcceleration[2] = event.values[2] - gravity[2];

        System.out.println("x: " + linearAcceleration[0]);
        System.out.println("y: " + linearAcceleration[1]);
        System.out.println("z: " + linearAcceleration[2]);

        System.out.println("Time: " + event.timestamp);
    }
}
