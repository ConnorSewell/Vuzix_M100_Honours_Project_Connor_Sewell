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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.*;
import java.net.Socket;
import java.util.ArrayList;

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
    private PrintWriter out;
    private String outputDirectory;
    private boolean streamMode;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    private float EPSILON = 0.00000001f;
    private String outputString;
    private int count = 0;
    private float averagedX = 0.f;
    private float averagedY = 0.f;
    private float averagedZ = 0.f;
    private long averagedTime = 0;
    private FileWriter outputFileWriter;
    private BufferedWriter bufferedWriter;
    private File gyroscopeOutputFile;
    private String TAG = "GyroscopeHandler";
    private Main activity;

    private long startTime;
    public ArrayList<PrintWriter> outputPoints = new ArrayList<PrintWriter>();
    public ArrayList<Socket> sockets = new ArrayList<Socket>();

    Handler gyroscopeUpdateLooper;

    public GyroscopeHandler(Main activity, String outputDirectory, boolean streamMode)
    {
        this.context = activity;
        this.activity = activity;
        this.outputDirectory = outputDirectory;
        this.streamMode = streamMode;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        HandlerThread gyroscopeUpdateThread = new HandlerThread("Accelerometer Update Handler");
        gyroscopeUpdateThread.start();
        gyroscopeUpdateLooper = new Handler(gyroscopeUpdateThread.getLooper());

    }

    public void setFileOutput(String outputDirectory)
    {
        if (!streamMode) {
            gyroscopeOutputFile = new File(outputDirectory + File.separator + "GyroscopeData.txt");
            try {
                outputFileWriter = new FileWriter(gyroscopeOutputFile);
                bufferedWriter = new BufferedWriter(outputFileWriter);
            } catch (IOException e) {
                Log.e(TAG, "File not found...");
            }
        }
    }

    public void addOutputPoint(PrintWriter out, Socket socket)
    {
        outputPoints.add(out);
        sockets.add(socket);
    }

    public void setStreamMode(boolean streamMode)
    {
        this.streamMode = streamMode;
    }

    public void registerSensorListener()
    {
        mSensorManager.registerListener(this, gyroscopeSensor, 20000, gyroscopeUpdateLooper);
        activity.setSensorReady();
    }

    public void stopListener()
    {
        try
        {
            mSensorManager.unregisterListener(this, gyroscopeSensor);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Likely listener was not registered... ERROR: " + e.toString());
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // Do something here if sensor accuracy changes.
    }

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

    boolean busy = false;
    boolean valuesReceived = false;
    SensorEvent sensorEvent;
    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        //Set here?
        //if (!busy)
        //{
        //    this.sensorEvent = event;
        //    busy = true;
        //    new Thread(new Runnable()
        //    {
        //        @Override
        //        public void run()
        //        {
                    if (timestamp != 0) {
                        final float dT = (event.timestamp - timestamp) * NS2S;

                        float axisX = event.values[0];
                        float axisY = event.values[1];
                        float axisZ = event.values[2];

                        float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                        if (omegaMagnitude > EPSILON)
                        {
                            axisX /= omegaMagnitude;
                            axisY /= omegaMagnitude;
                            axisZ /= omegaMagnitude;
                        }

                        //float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                        //float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                        //float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
                        //deltaRotationVector[0] = sinThetaOverTwo * axisX;
                        //deltaRotationVector[1] = sinThetaOverTwo * axisY;
                        //deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                        //deltaRotationVector[3] = cosThetaOverTwo;

                        outputString = axisX + "," + axisY + "," + axisZ; //Remove

                        if (streamMode)
                            {
                                long currTime = System.nanoTime();
                                for (int i = 0; i < outputPoints.size(); i++)
                                {
                                    try {
                                        outputPoints.get(i).println(outputString + "," + (currTime - startTime));
                                    }
                                    catch(Exception e)
                                    {
                                        if(sockets.get(i).isClosed())
                                        {
                                            outputPoints.remove(i);
                                            sockets.remove(i);
                                        }
                                    }
                                }}

                        if (!streamMode)
                        {
                            try
                            {
                                bufferedWriter.write(outputString + "," + String.valueOf(event.timestamp));
                                bufferedWriter.newLine();
                                if(!valuesReceived)
                                {
                                    activity.videoRequirementsToStart();
                                    valuesReceived = true;
                                }
                            } catch (IOException e)
                            {

                               Log.e(TAG, e.toString());
                            }
                       }
                    }

                    timestamp = event.timestamp;
                    float[] deltaRotationMatrix = new float[9];
                    SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
                    busy = false;
               // }
          //  }).start();
        //}
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    private float[] matrixMultiplication(float[] a, float[] b)
    {
        float[] result = new float[9];

        result[0] = a[0] * b[0] + a[1] * b[3] + a[2] * b[6];
        result[1] = a[0] * b[1] + a[1] * b[4] + a[2] * b[7];
        result[2] = a[0] * b[2] + a[1] * b[5] + a[2] * b[8];

        result[3] = a[3] * b[0] + a[4] * b[3] + a[5] * b[6];
        result[4] = a[3] * b[1] + a[4] * b[4] + a[5] * b[7];
        result[5] = a[3] * b[2] + a[4] * b[5] + a[5] * b[8];

        result[6] = a[6] * b[0] + a[7] * b[3] + a[8] * b[6];
        result[7] = a[6] * b[1] + a[7] * b[4] + a[8] * b[7];
        result[8] = a[6] * b[2] + a[7] * b[5] + a[8] * b[8];

        return result;
    }


}
