package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles accelerometer data gathering/streaming
 * https://developer.android.com/guide/topics/sensors/sensors_overview.html
 * ^ All sensor functionality/code taken from the above link. Accessed: 16/01/2017 @ 18:57.
 */
public class AccelerometerHandler implements SensorEventListener {
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
    public ArrayList<Socket> sockets = new ArrayList<Socket>();

    Handler acceleromterUpdateLooper;

    public AccelerometerHandler(Main activity, String outputDirectory, boolean streamMode) {
        this.context = activity;
        this.activity = activity;
        this.outputDirectory = outputDirectory;
        this.streamMode = streamMode;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = new float[3];
        linearAcceleration = new float[3];

        HandlerThread accelerometerUpdateThread = new HandlerThread("Accelerometer Update Handler");
        accelerometerUpdateThread.start();
        acceleromterUpdateLooper = new Handler(accelerometerUpdateThread.getLooper());
    }

    public void setFileOutput(String outputDirectory)
    {
        if (!streamMode) {
            accelerometerTextFile = new File(outputDirectory + File.separator + "AccelerometerData.txt");
            try {
                outputFileWriter = new FileWriter(accelerometerTextFile);
                bufferedWriter = new BufferedWriter(outputFileWriter);
            } catch (IOException e) {
                Log.e(TAG, "File not found...");
            }
        }
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setStreamMode(boolean streamMode) {
        this.streamMode = streamMode;
    }

    public void registerSensorListener()
    {
        mSensorManager.registerListener(this, accelerometerSensor, 20000, acceleromterUpdateLooper);
        activity.setSensorReady();
    }

    public void stopListener() {

        try
        {
            mSensorManager.unregisterListener(this, accelerometerSensor);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Likely listener was not registered... ERROR: " + e.toString());
        }
    }

    public void setOutputPoint(PrintWriter out, Socket socket) {
        this.outputPoints.add(out);
        sockets.add(socket);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "Accelerometer accuracy changed...");
    }


    boolean busy = false;
    private String ratesAsString = null;

    public void closeBuffer()
    {
        try
        {
            bufferedWriter.close();
        }
        catch(Exception e)
        {
            Log.e(TAG, "Trouble closing.. attempting flush");
            try
            {
                bufferedWriter.flush();
            }
            catch(Exception i)
            {
                Log.e(TAG, "Failed to flush. Warning: File likely missing end data");
            }
        }
    }


    boolean valuesReceived = false;
    @Override
    public final void onSensorChanged(SensorEvent event)
    {
                    linearAcceleration[0] = event.values[0];
                    linearAcceleration[1] = event.values[1];
                    linearAcceleration[2] = event.values[2];
                    time = event.timestamp;

                    ratesAsString = String.valueOf(linearAcceleration[0]) + "," + String.valueOf(linearAcceleration[1]) + "," + String.valueOf(linearAcceleration[2]);

                    long time = System.nanoTime() - startTime;
                    if (streamMode) {
                        for (int i = 0; i < outputPoints.size(); i++)
                        {
                            try
                            {
                                outputPoints.get(i).println(ratesAsString + "," + time);
                            }
                            catch(Exception e)
                            {
                                if(sockets.get(i).isClosed())
                                {
                                    outputPoints.remove(i);
                                    sockets.remove(i);
                                }
                            }
                        }
                    }

                    if (!streamMode)
                    {
                        try {
                            bufferedWriter.write(ratesAsString + "," + String.valueOf(event.timestamp));
                            bufferedWriter.newLine();
                            if(!valuesReceived)
                            {
                                activity.videoRequirementsToStart();
                                valuesReceived = true;
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Write failed...");
                        }
                    }
                    busy = false;
                }

}
