package onyx.example.terra;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import onyx.example.terra.R;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EarthquakeMapActivity extends FragmentActivity implements OnMapReadyCallback {

    String disaster;
    String json;
    ImageButton back;
    FloatingActionButton home;
    MapView mapView;
    GoogleMap map;
    int NUM_DISASTERS;
    int count = 0;

    //Location Updates variables
    static EarthquakeMapActivity instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentlat;
    double currentlong;
    String dateString;

    private static final String TAG = "EarthquakeMap";

    private Firebase mRef;
    long isLocPermissionGranted;

    public static EarthquakeMapActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");
        getIsLocPermissionGranted();
        if (isLocPermissionGranted == 2){
            Toast.makeText(this, "Without your location, Terra can not provide a map of earthquakes near you. Please allow Terra to access your location to use this feature.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EarthquakeMapActivity.this, HomeScreenActivity.class);
            startActivity(intent);
        }

        SimpleDateFormat formatter
                = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        dateString = formatter.format(date);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            disaster = (String) bundle.get("Disaster");
        }

        if (disaster.equals("Earthquakes")) { //If the user is on the earthquake route in the app
            setContentView(R.layout.activity_earthquake_map);
            if (isLocPermissionGranted == 0)
                getPermission();
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager() //create the map fragment
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync((OnMapReadyCallback) this);
            GetUsgsData(); //retrieve the USGS data
        }

        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(EarthquakeMapActivity.this, DisasterMenuActivity.class);
                intent1.putExtra("Disaster", "Earthquakes");
                startActivity(intent1);
            }
        });

        instance = this;
    }

    //Get the user permission for location
    public void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Then request the user permission to access contacts
            ActivityCompat.requestPermissions(EarthquakeMapActivity.this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method
        }
        else {
            Log.v(TAG, "PERMISSION RECEIVED");
            updateLocation();
        }
    }

    //Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {

            //If the permission has been granted, run showContacts() again and move on to the next step
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            }

            //If the permission hasn't been granted, handle it with an error message
            else {
                Toast.makeText(this, "Without your location, Terra can not provide a map of earthquakes near you. Please allow Terra to access your location to use this feature.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EarthquakeMapActivity.this, HomeScreenActivity.class);
                startActivity(intent);
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
        Log.v(TAG, "updateLocation");


    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        Log.v(TAG, "pendingIntent");
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //get the location at high accuracy
        Log.v(TAG, "buildLocationRequest");
    }


    public void setCoordinates(final double lat, final double lon) {
        Log.v(TAG, "Setting coordinates");
        EarthquakeMapActivity.this.runOnUiThread(new Runnable() { //while this activity is running
            @Override
            public void run() {
                currentlat = lat;
                currentlong = lon;
            }
        });
    }

    public void GetUsgsData() {
        String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&limit=2000&orderby=time&starttime=" + dateString;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        // Display the first 500 characters of the response string.
                        json = response.toString();
                        Log.v(TAG, json.substring(0,100));
                        try {
                            readJSON();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Add the request to the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }

    //Parse the JSON file that the app receives from USGS
    public void readJSON() throws JSONException {
        if (json != null) {
            JSONObject reader = new JSONObject(json);
            JSONArray earthquakes = reader.getJSONArray("features");
            NUM_DISASTERS = earthquakes.length();
            for (int i = 0; i < earthquakes.length(); i++) {
                JSONObject earthquake = (JSONObject) earthquakes.get(i);
                JSONObject properties = (JSONObject) earthquake.get("properties");
                Double magnitude = properties.getDouble("mag");
                String place = properties.getString("place");

                JSONObject geometry = (JSONObject) earthquake.get("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                Double latitude = (Double) coordinates.get(1);
                Double longitude = (Double) coordinates.get(0);

                LatLng location = new LatLng(latitude, longitude);
                map.addMarker(new MarkerOptions().position(location).title("Magnitude: " + magnitude+"; " + place) //Once the earthquake information has been received, set a map marker
                        .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(magnitude))));
            }
        }

        LatLng user = new LatLng(currentlat, currentlong);
        map.addMarker(new MarkerOptions().position(user).title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        map.moveCamera(CameraUpdateFactory.newLatLng(user));
        map.moveCamera(CameraUpdateFactory.zoomTo(10.0f));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Log.v(TAG, "Map Ready");
    }

    //Set the map marker's color based on the magnitude of the earthquake
    public float getMarkerColor(double magnitude) {
        if (magnitude < 1.0) {
            return BitmapDescriptorFactory.HUE_CYAN;
        } else if (magnitude < 2.5) {
            return BitmapDescriptorFactory.HUE_GREEN;
        } else if (magnitude < 4.5) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        } else {
            return BitmapDescriptorFactory.HUE_RED;
        }
    }

    public void getIsLocPermissionGranted() {
        Firebase mRefChild1 = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("isLocPermissionGranted");
        mRefChild1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    isLocPermissionGranted = (long) dataSnapshot.getValue();
                    Log.v(TAG, "Permission is granted?: " + isLocPermissionGranted);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
