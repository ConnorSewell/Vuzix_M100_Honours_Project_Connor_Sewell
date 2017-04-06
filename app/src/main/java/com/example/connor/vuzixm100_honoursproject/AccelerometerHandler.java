package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
    private BufferedWriter bufferedWriter;

    private String TAG = "AccelerometerHandler: ";
    private boolean streamMode;
    private Main activity;

    private long startTime;

    public ArrayList<PrintWriter> outputPoints = new ArrayList<PrintWriter>();

    public AccelerometerHandler(Main activity, String outputDirectory, boolean streamMode)
    {
        this.context = activity;
        this.activity = activity;
        this.outputDirectory = outputDirectory;
        this.streamMode = streamMode;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = new float[3];
        linearAcceleration = new float[3];

        if(!streamMode)
        {
            accelerometerTextFile = new File(outputDirectory + File.separator + "AccelerometerData.txt");
            try
            {
                outputFileWriter = new FileWriter(accelerometerTextFile);
                bufferedWriter = new BufferedWriter(outputFileWriter);
            } catch (IOException e) {
                Log.e(TAG, "File not found...");
            }
        }

    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public void registerSensorListener()
    {
        mSensorManager.registerListener(this, accelerometerSensor, 20000);
        activity.setSensorReady();
    }

    public void setOutputPoint(PrintWriter out)
    {
        this.outputPoints.add(out);
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
    private SensorEvent sensorEvent;
    boolean busy = false;
    private String ratesAsString = null;

    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        if (!busy)
        {
            this.sensorEvent = event;
            busy = true;
            new Thread(new Runnable() {
                public void run()
                {
                    //gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
                    //gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
                    //gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];

                    linearAcceleration[0] = sensorEvent.values[0];
                    linearAcceleration[1] = sensorEvent.values[1];
                    linearAcceleration[2] = sensorEvent.values[2];
                    time = sensorEvent.timestamp;

                    ratesAsString = linearAcceleration[0] + "," + linearAcceleration[1] + "," + linearAcceleration[2];

                    long time = System.nanoTime() - startTime;
                    if (streamMode)
                    {
                        for (int i = 0; i < outputPoints.size(); i++)
                        {
                            outputPoints.get(i).println(ratesAsString + "," + time);
                        }
                    }

                    if (!streamMode) {
                        try {
                            bufferedWriter.write(ratesAsString + "," + String.valueOf(sensorEvent.timestamp));
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            Log.e(TAG, "Write failed...");
                        }
                    }
                    busy = false;
                }


            }).start();
        }
    }
}

