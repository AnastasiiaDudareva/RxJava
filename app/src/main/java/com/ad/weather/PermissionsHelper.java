package com.ad.weather;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anastasiia on 22.12.17.
 */

public class PermissionsHelper {
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    public static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 2;
    public static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 3;


    /**
     * Request dangerous permissions for android 6.
     *
     * @param thisActivity       activity for result
     * @param permission         requested permission
     * @param permissionsRequest key for response
     */
    private static void requestPermissionIfNeed(AppCompatActivity thisActivity, String permission, int permissionsRequest) {
        if (ContextCompat.checkSelfPermission(thisActivity, permission) == PackageManager.PERMISSION_GRANTED) {
            thisActivity.onRequestPermissionsResult(permissionsRequest,
                    new String[]{permission},
                    new int[]{PackageManager.PERMISSION_GRANTED});
        } else {
            ActivityCompat.requestPermissions(thisActivity,
                    new String[]{permission},
                    permissionsRequest);
        }
    }

    static void requestPermissionsIfNeed(AppCompatActivity thisActivity, String[] permission, int[] permissionsRequest) {
        if (permission.length != permissionsRequest.length) {
            Log.e(PermissionsHelper.class.getName(),
                    "permission length must be equally length of permissionsRequest");
        }
        if (permission.length == 1) {
            requestPermissionIfNeed(thisActivity, permission[0], permissionsRequest[0]);
            return;
        }
        List<String> permissionForRequest = new ArrayList<>();
        for (int i = 0; i < permission.length; i++) {
            if (ContextCompat.checkSelfPermission(thisActivity, permission[i]) == PackageManager.PERMISSION_GRANTED) {
                thisActivity.onRequestPermissionsResult(permissionsRequest[i],
                        new String[]{permission[i]},
                        new int[]{PackageManager.PERMISSION_GRANTED});
            } else {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, permission[i])) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    permissionForRequest.add(permission[i]);
                }
            }
        }
        if (!permissionForRequest.isEmpty())
            ActivityCompat.requestPermissions(thisActivity,
                    permissionForRequest.toArray(new String[permissionForRequest.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }
}
