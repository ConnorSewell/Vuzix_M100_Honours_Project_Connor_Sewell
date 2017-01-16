package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.util.Log;

import java.util.List;


/**
 * Created by Connor on 16/01/2017.
 */
public class SensorHandler
{
    private SensorManager mSensorManager;
    private Context context;


    private String sensorTAG = "Sensor: ";

    int i;

    public SensorHandler(Context context)
    {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        //Checking Sensors Exist (Other sensors not contained within SensorManager)
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
        {
            System.out.println("Accelerometer Detected");
        }
        else
        {
            System.out.println("Accelerometer Not Found");
        }

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null)
        {
            System.out.println("Gyroscope Detected");
        }
        else
        {
            System.out.println("Gyroscope Not Found");
        }


    }








}
