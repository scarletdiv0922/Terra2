package onyx.example.terra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.ScriptMethodCallback;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

public class DisasterMenuActivity extends AppCompatActivity {

    String disaster;
    ImageButton information;
    ImageButton scores;
    ImageButton updates;
    FloatingActionButton home;
    ImageButton back;
    private static final String TAG = "DisasterMenu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout based on the disaster the user clicked on
        Intent disasterIntent = getIntent();
        Bundle bundle = disasterIntent.getExtras();

        if (bundle != null) {
            disaster = (String) bundle.get("Disaster"); //Get the disaster passed to this activity from the previous activity
        }

        if (disaster.equals("Earthquakes")) {
            setContentView(R.layout.activity_earthquake_menu);
        }

        else if (disaster.equals("Wildfires")) {
            setContentView(R.layout.activity_wildfire_menu);
        }

        JSONObject commandJson = null;

        try {
            commandJson = new JSONObject("{\"command\":\"navigate\", \"screen\": \"settings\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        information = findViewById(R.id.info_button);
        scores = findViewById(R.id.scores_button);
        updates = findViewById(R.id.updates_button);
        home = findViewById(R.id.home_button);
        back = findViewById(R.id.back_button);

        //Set up the Alan AI voice assistant
        AlanButton alan_button;
        alan_button = findViewById(R.id.alan_button);

        AlanConfig config = AlanConfig.builder()
                .setProjectId("53a6f889052442def00f689c7641807c2e956eca572e1d8b807a3e2338fdd0dc/stage")
                .build();
        alan_button.initWithConfig(config);

        alan_button.playCommand(commandJson.toString(),  new ScriptMethodCallback() {
            @Override
            public void onResponse(String methodName, String body, String error) {
                Log.v(TAG, methodName);
            }
        });

        AlanCallback myCallback = new AlanCallback() {
            @Override
            public void onCommandReceived(EventCommand eventCommand) {
                super.onCommandReceived(eventCommand);
                String cmd = eventCommand.getData().toString();
                Log.v(TAG, cmd);
                if (cmd.contains("before")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(DisasterMenuActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")) {
                        Intent intent = new Intent(DisasterMenuActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("during")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(DisasterMenuActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(DisasterMenuActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("after")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(DisasterMenuActivity.this, ContactsActivity2.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(DisasterMenuActivity.this, ContactsActivity2.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("nearMe")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(DisasterMenuActivity.this, EarthquakeMapActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(DisasterMenuActivity.this, EarthquakeMapActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("show")) {
                    Intent intent = new Intent(DisasterMenuActivity.this, EmergencyContactsActivity.class);
                    startActivity(intent);
                }
                else if (cmd.contains("checklist")) {
                    Intent intent = new Intent(DisasterMenuActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("near")) {
                    Intent intent = new Intent(DisasterMenuActivity.this, NearbyFacilitiesActivity.class);
                    startActivity(intent);
                }
                else if (cmd.contains("navigate")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("home")){
                        Intent intent = new Intent(DisasterMenuActivity.this, HomeScreenActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency contacts") || cmd.substring(i, j).equals("contacts")) {
                        Intent intent = new Intent(DisasterMenuActivity.this, EmergencyContactsActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency checklist") || cmd.substring(i, j).equals("checklist")) {
                        Intent intent = new Intent(DisasterMenuActivity.this, ChecklistActivity2.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("nearby facilities")){
                        Intent intent = new Intent(DisasterMenuActivity.this, NearbyFacilitiesActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("disaster warnings") || cmd.substring(i, j).equals("disaster updates") || cmd.substring(i, j).equals("map")) {
                        Intent intent = new Intent(DisasterMenuActivity.this, EarthquakeMapActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };

        alan_button.registerCallback(myCallback);

        //Go to the information page
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, BDAActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        //Go to the home screen
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, HomeScreenActivity.class);
                startActivity(intent1);
            }
        });

        //Go to the hazard/prep scores screen
        scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, HazardPrepActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        //Go to the disaster updates screen
        updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disaster.equals("Earthquakes")) {
                    Intent intent1 = new Intent(DisasterMenuActivity.this, EarthquakeMapActivity.class);
                    intent1.putExtra("Disaster", disaster);
                    startActivity(intent1);
                }
                else {
                    Intent intent1 = new Intent(DisasterMenuActivity.this, WildfireMapActivity.class);
                    intent1.putExtra("Disaster", disaster);
                    startActivity(intent1);
                }
            }
        });

        //Go back to the home screen
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, HomeScreenActivity.class);
                startActivity(intent1);
            }
        });
    }
}
