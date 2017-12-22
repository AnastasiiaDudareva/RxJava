package com.ad.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import io.reactivex.Single;

/**
 * Created by anastasiia on 22.12.17.
 */

public class LocationHelper {
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;

    public LocationHelper(Activity context) {
        this.context = context;
        this.mFusedLocationClient = LocationServices.
                getFusedLocationProviderClient(context);
    }


    public Single<Location> getLastKnownLocation() {
        return Single.create(e -> {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(location -> e.onSuccess(location))
                        .addOnFailureListener(t -> e.onError(t));
            } else {
                e.onError(new Throwable("Permission not granted"));
            }
        });
    }

}
