package com.foodfinder.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.foodfinder.acount.Position;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class UpdatePositionService extends Service {

    public UpdatePositionService()
    {
    }

    private LocationManager locationManager;
    private String mprovider;
    private Thread updateThread;
    private boolean isInterrupt=false;
    private Position currentPosition;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void setCurrentLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

           // ActivityCompat.requestPermissions(,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        else {

            locationManager.requestLocationUpdates(mprovider, 1500, 1, getLocationListener());

        }
    }

    public LocationListener getLocationListener()
    {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentPosition=new Position(location.getLatitude(),location.getLongitude());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
         Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mprovider = locationManager.getBestProvider(new Criteria(), true);
        setCurrentLocation();

        updateThread = new Thread() {
            @Override
            public void run() {


                while (!isInterrupt)
                {

                   // Log.e("Test sevice", "run: ");
                    updatePosition();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show();
        updateThread.start();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();

        //updateThread.interrupt();
        isInterrupt=true;


    }

    private void updatePosition()
    {

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId=currentFirebaseUser.getUid();

                Random r=new Random();
                Position p=new Position(5.6, r.nextFloat());
                if(currentPosition!=null)
                {
                    p=currentPosition;
                }
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("place");
                ref.setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {


                            }
                        });

    }

}
