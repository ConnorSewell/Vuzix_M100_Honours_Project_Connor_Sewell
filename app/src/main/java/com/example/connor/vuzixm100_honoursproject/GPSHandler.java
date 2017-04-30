package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.security.Security;

/**
 * Created by Connor on 23/01/2017.
 * https://developer.android.com/guide/topics/location/strategies.html
 * ^ Used throughout class. Time of reference: 25/01/2017 @ 20:05
 */
public class GPSHandler
{
    private LocationManager lms;
    private LocationManager locationManager;
    private final Activity activity;

    public GPSHandler(Context context, Activity activity_in)
    {
        activity = activity_in;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Toast.makeText(activity, "GPS Enabled", Toast.LENGTH_LONG).show();
        }

        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Toast.makeText(activity, "Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
            }

            @Override
            public void onProviderEnabled(String provider)
            {
                Toast.makeText(activity, "Provider enabled (GPS)", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String provider)
            {
                Toast.makeText(activity, "Provider disabled (GPS)", Toast.LENGTH_LONG).show();
            }
        };

        try
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, locationListener);
        } catch (SecurityException e)
        {
            Toast.makeText(activity, "Security exception: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
