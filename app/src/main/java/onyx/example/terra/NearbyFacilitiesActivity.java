package onyx.example.terra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import onyx.example.terra.R;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class NearbyFacilitiesActivity extends AppCompatActivity {
    //Initialize variables
    Spinner spType;
    Button btFind;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;
    long isLocPermissionGranted;
    private Firebase mRef;
    private static final String TAG = "NearbyFacilities";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_facilities);
        //Assign variables
        spType = findViewById(R.id.sp_type);
        btFind = findViewById(R.id.bt_find);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        //Connect activity to Firebase
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");
        getIsLocPermissionGranted();

        //Initialize array of place types
        final String[] placeTypeList = {"atm", "bank", "hospital", "store", "shelter"};
        //Initialize array of place names
        String[] placeNameList = {"ATM", "Bank", "Hospital", "Store", "Shelter"};
        //Set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(NearbyFacilitiesActivity.this,
                android.R.layout.simple_spinner_dropdown_item, placeNameList));

        //Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //Check permission
        if (isLocPermissionGranted == 1) {
            //When permission granted, call method
            getCurrentLocation();
        }
        else {

            if (isLocPermissionGranted == 0) {
                //If permission denied, request permission
                ActivityCompat.requestPermissions(NearbyFacilitiesActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
            else {
                Toast.makeText(this, "Without your location, Terra can not provide a map of nearby facilities near you. Please allow Terra to access your location to use this feature.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(NearbyFacilitiesActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        }
        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Get selected position of spinner
                int i = spType.getSelectedItemPosition();
                //Initialize url
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + //URL
                        "?location=" + currentLat + "," + currentLong + //Location coordinates
                        "&radius=5000" + //Nearby radius
                        "&types=" + placeTypeList[i] + //Place type
                        "&sensor=true" + //Sensor
                        "&key=" + getResources().getString(R.string.google_map_key);// Google map api key
                //Execute place task method to download json data
                new PlaceTask().execute(url);
            }
        });
    }

    private void getCurrentLocation() {
        //Initialize task location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //When successful
                if (location != null){
                    //When location is not null, get current latitude and longitude
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    //Sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //When map is ready
                            map = googleMap;
                            //Zoom current location
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat, currentLong), 10
                            ));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //When permission is granted, call method
                getCurrentLocation();
            }
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

    private class PlaceTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                //Initialize data
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            //Execute parser task
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        //Initialize url
        URL url = new URL(string);
        //Initialize connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //Connect connection
        connection.connect();
        //Initialize input stream, buffer reader, and string builder
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null){
            builder.append(line);
        }
        //Get append data
        String data = builder.toString();
        reader.close();
        //Return data
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //Create json parser class
            JsonParser jsonParser = new JsonParser();
            //Initialize hash map list
            List<HashMap<String, String>> mapList = null;
            JSONObject object = null;
            try {
                //Initialize json object
                object = new JSONObject(strings[0]);
                //Parse json object
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Return map list
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            //Clear map
            map.clear();
            //Use for loop
            for (int i = 0; i < hashMaps.size(); i++){
                //Initialize hash map
                HashMap<String, String> hashMapList = hashMaps.get(i);
                //Get latitude and longitude
                double lat = Double.parseDouble(hashMapList.get("lat"));
                double lng = Double.parseDouble(hashMapList.get("lng"));
                //Get name
                String name = hashMapList.get("name");
                //Concatenate latitude and longitude
                LatLng latLng = new LatLng(lat, lng);
                //Initialize marker options
                MarkerOptions options = new MarkerOptions();
                //Set position
                options.position(latLng);
                //Set title
                options.title(name);
                //Add marker on map
                map.addMarker(options);
                System.out.println(257);
            }
        }
    }
}