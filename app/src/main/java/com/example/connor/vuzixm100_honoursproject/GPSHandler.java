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
import android.widget.Toast;


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
    public final Activity activity2;

    public GPSHandler(Context context, Activity activity)
    {
        activity2 = activity;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        System.out.println("HEHEHEHEH");
        //Toast.makeText(activity2, "Tester", Toast.LENGTH_LONG).show();


        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Toast.makeText(activity2, "GPS Enabled", Toast.LENGTH_LONG).show();
        }

        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                System.out.println("Longitude: " + location.getLongitude());
                System.out.println("Latitude: " + location.getLatitude());

                //Toast.makeText(activity2, "Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                System.out.println("Changed");
            }

            @Override
            public void onProviderEnabled(String provider) {
                System.out.println("Enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                System.out.println("Disabled");
            }
        };

        try
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        } catch (SecurityException e) {
            System.out.println("Cannot Access");
        }
    }
}
