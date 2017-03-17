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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.*;

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


    public GyroscopeHandler(Context context, String outputDirectory, boolean streamMode)
    {
        this.context = context;
        this.outputDirectory = outputDirectory;
        this.streamMode = streamMode;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroscopeOutputFile = new File(outputDirectory + File.separator + "GyroscopeData.txt");
        try
        {
            outputFileWriter = new FileWriter(gyroscopeOutputFile);
            bufferedWriter = new BufferedWriter(outputFileWriter);
        }
        catch(IOException e)
        {
            Log.e(TAG, "File not found...");
        }
    }

    public void setOutputPoint(PrintWriter out)
    {
        this.out = out;
    }

    public void registerSensorListener()
    {
        mSensorManager.registerListener(this, gyroscopeSensor, 20000);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;

            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            float omegaMagnitude = (float)Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

            if (omegaMagnitude > EPSILON)
            {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;

            averagedX = averagedX + axisX;
            averagedY = averagedY + axisY;
            averagedZ = averagedZ + axisZ;
            averagedTime = averagedTime + event.timestamp;
            count++;

            if(count == 15)
            {
                averagedX = averagedX/15.f;
                averagedY = averagedY/15.f;
                averagedZ = averagedZ/15.f;
                averagedTime = averagedTime/15;
                outputString = axisX + "," + axisY + "," + axisZ + "," + event.timestamp; //Remove
                if(streamMode)
                {
                    out.println(outputString);
                }

                count = 0;
                averagedX = 0;
                averagedY = 0;
                averagedZ = 0;
                averagedTime = 0;
            }

            if(!streamMode)
            {
                try
                {
                    bufferedWriter.write(axisX + "," + axisY + "," + axisZ + "," + event.timestamp);
                    bufferedWriter.newLine();
                }
                catch(IOException e)
                {
                    Log.e(TAG, e.toString());
                }
            }
         }
        timestamp = event.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);

        //writeToFile();

        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
    }

    private void writeToFile()
    {

    }
}
