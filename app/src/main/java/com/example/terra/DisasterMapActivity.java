package com.example.terra;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DisasterMapActivity extends FragmentActivity implements OnMapReadyCallback {

    String disaster;
    String json;
    ImageButton back;
    TextView eqInfo;
    MapView mapView;
    GoogleMap map;
    ArrayList<Double> magnitudes = new ArrayList<>();
    ArrayList<Double> lats = new ArrayList<>();
    ArrayList<Double> lons = new ArrayList<>();
    int NUM_DISASTERS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            disaster = (String) bundle.get("Disaster");
        }

        back = findViewById(R.id.back_button);
        eqInfo = findViewById(R.id.info);
        eqInfo.setMovementMethod(new ScrollingMovementMethod());
        mapView = findViewById(R.id.mapView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMapActivity.this, DisasterMenuActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        GetUsgsData();
    }

    public void GetUsgsData() {
        String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-01-02";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        // Display the first 500 characters of the response string.
                        json = response.toString();
                        System.out.println("I'M HERE" + json.substring(0,100));
                        try {
                            readJSON();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        eqInfo.setText(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                eqInfo.setText("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }

    public void readJSON() throws JSONException {
        if (json != null) {
            Toast.makeText(this, "OWO", Toast.LENGTH_SHORT).show();
            JSONObject reader = new JSONObject(json);
            JSONArray earthquakes = reader.getJSONArray("features");
            NUM_DISASTERS = earthquakes.length();
            for (int i = 0; i < earthquakes.length(); i++) {
                JSONObject earthquake = (JSONObject) earthquakes.get(i);
                JSONObject properties = (JSONObject) earthquake.get("properties");
                Double magnitude = properties.getDouble("mag");
                System.out.println("MAG: " + magnitude);
                magnitudes.add(magnitude);
                JSONObject geometry = (JSONObject) earthquake.get("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                Double latitude = (Double) coordinates.get(1);
                Double longitude = (Double) coordinates.get(0);
                System.out.println(latitude + "/" + longitude);

                lats.add(latitude);
                lons.add(longitude);
            }
        }

        for (int i = 0; i < NUM_DISASTERS; i++) {
            LatLng location = new LatLng(lats.get(i), lons.get(i));
            map.addMarker(new MarkerOptions().position(location).title(String.valueOf(magnitudes.get(i))));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
