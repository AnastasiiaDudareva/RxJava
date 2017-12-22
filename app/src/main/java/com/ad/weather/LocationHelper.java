package com.ad.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by anastasiia on 22.12.17.
 */

public class LocationHelper {
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private PublishSubject<Location> observable = PublishSubject.create();

    public LocationHelper(Activity context) {
        this.context = context;
        this.mFusedLocationClient = LocationServices.
                getFusedLocationProviderClient(context);
    }


    public PublishSubject<Location> getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            observable.onNext(location);
                            observable.onComplete();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            observable.onError(e);
                        }
                    });
        } else {
            observable.onError(new Throwable("Permission not granted"));
        }
        return observable;
    }
}
