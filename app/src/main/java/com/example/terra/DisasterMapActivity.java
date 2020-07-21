package com.example.terra;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class DisasterMapActivity extends AppCompatActivity {

    String disaster;
    ImageButton back;
    FloatingActionButton home;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    static DisasterMapActivity instance;
    double currentLat;
    double currentLon;

    public static DisasterMapActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_map);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            disaster = (String) bundle.get("Disaster");
        }

        back = findViewById(R.id.back_button);
        home = findViewById(R.id.home_button);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMapActivity.this, DisasterMenuActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMapActivity.this, HomeScreenActivity.class);
                startActivity(intent1);
            }
        });

        requestLocation();
        updateLocation();
    }

    public void requestLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Then request the user permission to access contacts
            ActivityCompat.requestPermissions(DisasterMapActivity.this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {

            //If the permission has been granted, run showContacts() again and move on to the next step
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                requestLocation();
            }

            //If the permission hasn't been granted, handle it with an error message
            else {
                Toast.makeText(this, "Without your permission, Terra cannot inform you of disasters in your area.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateLocation() {
        buildLocationRequest(); //request to get the current location

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
        System.out.println("updateLocation");


    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        System.out.println("pendingIntent");
        return  PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //get the location at high accuracy
        locationRequest.setInterval(100); //get the location at 100 ms intervals
        locationRequest.setSmallestDisplacement(1); //get the location if the user moves 1 meter
        System.out.println("buildLocationRequest");
    }

    public void setCoordinates(final double lat, final double lon) {
        currentLat = lat;
        currentLon = lon;
//        Toast.makeText(DisasterMapActivity.this, "Currently at " + currentLat + "/" + currentLon, Toast.LENGTH_SHORT).show();

        System.out.println(lat + "/" + lon);
    }
}
