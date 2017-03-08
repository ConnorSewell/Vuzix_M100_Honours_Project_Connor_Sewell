package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.PrintWriter;

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
    private PrintWriter out;
    private long upTimeBeforeStart;
    private float x,y,z;
    private long time;
    public AccelerometerHandler(Context context, PrintWriter out)
    {
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = new float[3];
        linearAcceleration = new float[3];
        //Max sample rate
        this.out = out;
        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Log.i(infoLogTag, "Accelerometer accuracy changed...");
    }

    String outputString;
    int count = 0;
    float averagedX = 0.f;
    float averagedY = 0.f;
    float averagedZ = 0.f;
    long averagedTime = 0;
    final float alpha = 0.8f;

    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linearAcceleration[0] = event.values[0] - gravity[0];
        linearAcceleration[1] = event.values[1] - gravity[1];
        linearAcceleration[2] = event.values[2] - gravity[2];
        time = event.timestamp;

        averagedX = averagedX + linearAcceleration[0];
        averagedY = averagedY + linearAcceleration[1];
        averagedZ = averagedZ + linearAcceleration[2];
        averagedTime = averagedTime + event.timestamp;
        count++;

        if(count == 25)
        {
            averagedX = averagedX/25.f;
            averagedY = averagedY/25.f;
            averagedZ = averagedZ/25.f;
            averagedTime = averagedTime/25;
            outputString = averagedX + "," + averagedY + "," + averagedZ + "," + averagedTime;
            //outputString = linearAcceleration[0] + "," + linearAcceleration[1] + "," + linearAcceleration[2] + "," + event.timestamp;
            out.println(outputString);
            count = 0;
            averagedX = 0;
            averagedY = 0;
            averagedZ = 0;
            averagedTime = 0;
        }
    }

    public String getCurrentValues()
    {
        outputString = linearAcceleration[0] + "," + linearAcceleration[1] + "," + linearAcceleration[2] + "," + time;
        return outputString;
    }
}
