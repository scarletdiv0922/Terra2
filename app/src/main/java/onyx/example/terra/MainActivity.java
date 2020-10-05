package onyx.example.terra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import onyx.example.terra.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //TerraAlan ta = new TerraAlan();
    int count = 0;
    public String json;

    //Location Updates variables
    static MainActivity instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentlat;
    double currentlong;
    ArrayList<String> prevCodes = new ArrayList<>();
    ArrayList<String> currCodes = new ArrayList<>();
    ArrayList<Double> mags = new ArrayList<>();
    ArrayList<String> places = new ArrayList<>();
    NotificationChannel channel;
    int code = 0;
    String dateString;
    int minutes;
    int seconds;

    private static final String TAG = "MainActivity";

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

    }

}
