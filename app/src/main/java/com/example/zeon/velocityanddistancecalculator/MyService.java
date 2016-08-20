package com.example.zeon.velocityanddistancecalculator;

import android.*;
import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.text.Format;

/**
 * Created by Zeon on 20/8/2559.
 */
public class MyService extends Service {


    private static final int interval = 6000;
    private static final int fastInterval = 3000;

    private GoogleApiClient client;
    private LocationRequest request;
    private boolean isLocationReadFirst;
    Location prevLocation, lastLocation;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        client = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addConnectionCallbacks(connectionCallbacks)
                .build();
        request = new LocationRequest();
        request.setInterval(interval);
        request.setFastestInterval(fastInterval);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        client.connect();
        isLocationReadFirst = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.disconnect();
    }

    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener =
            new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                }
            };
    private GoogleApiClient.ConnectionCallbacks connectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                android.Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    LocationServices.FusedLocationApi.requestLocationUpdates(client, request, locationListener);
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            };

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Format fm = new DecimalFormat("##.###");
            if (isLocationReadFirst) {
                prevLocation = location;
                lastLocation = location;
                isLocationReadFirst = false;
            } else {
                prevLocation = lastLocation;
                lastLocation = location;
                float distance = prevLocation.distanceTo(lastLocation);
                long time = lastLocation.getTime() - prevLocation.getTime();
                Toast.makeText(getApplicationContext(),
                        "Distance : " + fm.format(distance / 1000) + " m."
                                + "\nVelocity : "
                                + fm.format(computeVelocity(distance, time)) + " km/h.",
                        Toast.LENGTH_SHORT).show();
                //TODO: Add log that you want to track here.
            }
        }
    };

    private float computeVelocity(float distance, long time) {
        float a = distance / 1000;
        float b = ((float) (time / 1000) / 60);
        return (a / b) * 0.06f;
    }
}
