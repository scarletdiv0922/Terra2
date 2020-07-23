package com.example.terra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.ScriptMethodCallback;
import com.alan.alansdk.alanbase.ConnectionState;
import com.alan.alansdk.alanbase.DialogState;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;
import com.alan.alansdk.events.EventRecognised;
import com.alan.alansdk.events.EventText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private AlanButton alanButton;
    int count = 0;
    public String json;

    //Location Updates variables
    static MainActivity instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentlat;
    double currentlong;

    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        Button signup = findViewById(R.id.sign_up_button);
        signup.setOnClickListener((v) -> {
            //Toast.makeText(this, "Moving to signup activity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), SignupActivity.class);
            startActivity(intent);
        });

        Button login = findViewById(R.id.login_button);
        login.setOnClickListener((v) -> {
            //Toast.makeText(this, "Moving to login activity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    threadCode();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void threadCode() {
        //TODO: FIX THE LINK TO THE RIGHT QUERY
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        getPermission();
        System.out.println(currentlat);
        System.out.println(currentlong);
        String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&limit=10&orderby=time&latitude="
                + currentlat
                + "&longitude="
                + currentlong
                + "&maxradiuskm=100&minmagnitude=2.5";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        // Display the first 500 characters of the response string.
                        json = response.toString();
                        System.out.println("I'M HERE 22222" + json);
                        try {
                            readJSON();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                eqInfo.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
        System.out.println("uwu " + count);
        count++;
    }

    public void readJSON() throws JSONException {
        if (json != null) {
            JSONObject reader = new JSONObject(json);
            JSONArray earthquakes = reader.getJSONArray("features");
            for (int i = 0; i < earthquakes.length(); i++) {
                JSONObject earthquake = (JSONObject) earthquakes.get(i);
                JSONObject properties = (JSONObject) earthquake.get("properties");
                Double magnitude = properties.getDouble("mag");
                String place = properties.getString("place");

                System.out.println("MAG: " + magnitude);
                JSONObject geometry = (JSONObject) earthquake.get("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                Double latitude = (Double) coordinates.get(1);
                Double longitude = (Double) coordinates.get(0);
//                System.out.println(latitude + "/" + longitude);
            }
        }
    }

    public void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Then request the user permission to access contacts
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION }, 1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method
        }
        else {
            System.out.println("PERMISSION RECEIVED");
            updateLocation();
        }
    }

    //Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {

            //If the permission has been granted, run showContacts() again and move on to the next step
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPermission();
            }

            //If the permission hasn't been granted, handle it with an error message
            else {
                Toast.makeText(this, "Without your permission, Terra cannot access your contacts.", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, MyLocationService2.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        System.out.println("pendingIntent");
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //get the location at high accuracy
        System.out.println("buildLocationRequest");
    }

    public void setCoordinates(final double lat, final double lon) {
        System.out.println("setting coords");
        MainActivity.this.runOnUiThread(new Runnable() { //while this activity is running
            @Override
            public void run() {
                currentlat = lat;
                currentlong = lon;
            }
        });
    }
}
