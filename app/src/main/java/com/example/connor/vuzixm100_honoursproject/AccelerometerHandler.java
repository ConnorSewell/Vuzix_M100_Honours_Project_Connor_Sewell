package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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
    private PrintWriter out;
    private long time;

    private String outputDirectory;
    private FileWriter outputFileWriter;
    private File accelerometerTextFile;

    private String TAG = "AccelerometerHandler: ";

    private boolean streamMode;

    public AccelerometerHandler(Context context, String outputDirectory, boolean streamMode)
    {
        this.context = context;
        this.outputDirectory = outputDirectory;
        this.streamMode = streamMode;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = new float[3];
        linearAcceleration = new float[3];

        if(!streamMode)
        {
            accelerometerTextFile = new File(outputDirectory + File.separator + "TestAccelerometer.txt");
            try
            {
                outputFileWriter = new FileWriter(accelerometerTextFile);
            } catch (IOException e) {
                Log.e(TAG, "File not found...");
            }
        }

    }

    public void registerSensorListener()
    {
        mSensorManager.registerListener(this, accelerometerSensor, 20000);
    }

    public void setOutputPoint(PrintWriter out)
    {
        this.out = out;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Log.i(TAG, "Accelerometer accuracy changed...");
    }

    private String outputString;
    private int count = 0;
    private float averagedX = 0.f;
    private float averagedY = 0.f;
    private float averagedZ = 0.f;
    private long averagedTime = 0;
    private final float alpha = 0.8f;

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

        if(count == 15)
        {
            averagedX = averagedX/15.f;
            averagedY = averagedY/15.f;
            averagedZ = averagedZ/15.f;
            averagedTime = averagedTime/15;
            outputString = averagedX + "," + averagedY + "," + averagedZ + "," + averagedTime;
            //outputString = linearAcceleration[0] + "," + linearAcceleration[1] + "," + linearAcceleration[2] + "," + event.timestamp;

            if(streamMode)
            out.println(outputString);

            count = 0;
            averagedX = 0;
            averagedY = 0;
            averagedZ = 0;
            averagedTime = 0;
        }

        if(!streamMode)
        writeToFile();
    }

    private void writeToFile()
    {
        try
        {
            outputFileWriter.write(outputString);
        }
        catch(IOException e)
        {
            Log.e(TAG, "Write failed...");
        }
    }
}
