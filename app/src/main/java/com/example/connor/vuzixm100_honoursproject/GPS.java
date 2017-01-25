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


import java.security.Security;

/**
 * Created by Connor on 23/01/2017.
 */
public class GPS
{
    private LocationManager lms;
    private LocationManager locationManager;

    public GPS(Context context)
    {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                System.out.println("Longitude: " + location.getLongitude());
                System.out.println("Latitude: " + location.getLatitude());
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
