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

/*

public class TerraAlan implements Parcelable {

    AlanButton alan_button;

    public TerraAlan () {
        //alan_button = findViewById(R.id.alan_button);

        AlanConfig config = AlanConfig.builder()
                .setProjectId("53a6f889052442def00f689c7641807c2e956eca572e1d8b807a3e2338fdd0dc/stage")
                .build();
        alan_button.initWithConfig(config);
    }

    public AlanButton getAlan_button() {
        return alan_button;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TerraAlan createFromParcel(Parcel in) {
            return null;
        }
    };
}
*/

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

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

//        minutes = Calendar.getInstance().get(Calendar.MINUTE);
//        seconds = Calendar.getInstance().get(Calendar.SECOND);
//        SimpleDateFormat formatter
//                = new SimpleDateFormat ("yyyy-MM-dd'T'hh:");
//        Date date = new Date();
//        dateString = formatter.format(date);
//        System.out.println("DATE "+dateString+":"+minutes+":"+seconds);

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

        createNotificationChannel();

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
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        minutes = Calendar.getInstance().get(Calendar.MINUTE);
        seconds = Calendar.getInstance().get(Calendar.SECOND);
        SimpleDateFormat formatter
                = new SimpleDateFormat ("yyyy-MM-dd'T'hh:");
        Date date = new Date();
        dateString = formatter.format(date);
        System.out.println("DATE "+dateString+":"+minutes+":"+seconds);
        getPermission();
        System.out.println(currentlat);
        System.out.println(currentlong);
        String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&limit=20&orderby=time&latitude="
                + currentlat
                + "&longitude="
                + currentlong
                + "&maxradiuskm=100&minmagnitude=2.5&starttime="
                + dateString+(minutes-1)+":"+seconds;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {

                        json = response.toString();
                        System.out.println("I'M HERE 22222" + json);
                        try {
                            readJSON();
                        } catch (JSONException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        });
        // Add the request to the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
        System.out.println("uwu " + count);
        count++;

        prevCodes.addAll(currCodes);
        currCodes.clear();
        mags.clear();
        places.clear();
    }

    public void readJSON() throws JSONException, InterruptedException {
        if (json != null) {
            JSONObject reader = new JSONObject(json);
            JSONArray earthquakes = reader.getJSONArray("features");
            for (int i = 0; i < earthquakes.length(); i++) {
                JSONObject earthquake = (JSONObject) earthquakes.get(i);
                JSONObject properties = (JSONObject) earthquake.get("properties");
                Double magnitude = properties.getDouble("mag");
                String place = properties.getString("place");
                String code = properties.getString("code");
                currCodes.add(code);
                mags.add(magnitude);
                places.add(place);

//                System.out.println("MAG: " + magnitude);
                JSONObject geometry = (JSONObject) earthquake.get("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                Double latitude = (Double) coordinates.get(1);
                Double longitude = (Double) coordinates.get(0);
//                System.out.println(latitude + "/" + longitude);
            }

            for (int i = 0; i < currCodes.size(); i++) {
                if (!prevCodes.contains(currCodes.get(i))) {
                    System.out.println("NOTIFY " + currCodes.get(i));

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "channel")
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setContentTitle("Earthquake")
                            .setContentText("An earthquake of magnitude " + mags.get(i) + " at " + places.get(i) + " has occurred.")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("An earthquake of magnitude " + mags.get(i) + " at " + places.get(i) + " has occurred."))
                            .setAutoCancel(true);

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(code, builder.build());
                    code++;
                }
                else {
                    System.out.println("DON'T NOTIFY");
                }
            }
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            String description = "Earthquake";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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
                Toast.makeText(this, "Without your permission, Terra cannot tell you what disasters are in your area.", Toast.LENGTH_SHORT).show();
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
