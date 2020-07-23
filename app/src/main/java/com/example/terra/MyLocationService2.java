package com.example.terra;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

public class MyLocationService2 extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATE = "com.example.terra.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {
//        System.out.println("that's");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATE.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                    try {
                        MainActivity.getInstance().setCoordinates(location.getLatitude(), location.getLongitude());
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

}
