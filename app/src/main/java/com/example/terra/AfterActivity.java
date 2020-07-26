package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.ScriptMethodCallback;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

public class AfterActivity extends AppCompatActivity {

    ImageButton backButton;
    FloatingActionButton home;
    String disaster;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent disasterIntent = getIntent();
        Bundle bundle = disasterIntent.getExtras();

        if (bundle != null) {
            disaster = (String) bundle.get("Disaster");
        }

        if (disaster.equals("Earthquakes")) {
            setContentView(R.layout.activity_after);
        }

        else if (disaster.equals("Wildfires")) {
            setContentView(R.layout.activity_after_wildfires);
        }

        JSONObject commandJson = null;

        try {
            commandJson = new JSONObject("{\"command\":\"navigate\", \"screen\": \"settings\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        text = findViewById(R.id.after_text);
        text.setMovementMethod(new ScrollingMovementMethod());

        AlanButton alan_button;
        alan_button = findViewById(R.id.alan_button);

        AlanConfig config = AlanConfig.builder()
                .setProjectId("53a6f889052442def00f689c7641807c2e956eca572e1d8b807a3e2338fdd0dc/stage")
                .build();
        alan_button.initWithConfig(config);

        alan_button.playCommand(commandJson.toString(),  new ScriptMethodCallback() {
            @Override
            public void onResponse(String methodName, String body, String error) {
                System.out.println("Heyyyyy");
                System.out.println(methodName);
            }
        });
        AlanCallback myCallback = new AlanCallback() {
            @Override
            public void onCommandReceived(EventCommand eventCommand) {
                super.onCommandReceived(eventCommand);
                String cmd = eventCommand.getData().toString();
                System.out.println(cmd);
                if (cmd.contains("before")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent1 = new Intent(AfterActivity.this, BeforeActivity.class);
                        intent1.putExtra("Disaster", "Earthquakes");
                        startActivity(intent1);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")) {
                        Intent intent1 = new Intent(AfterActivity.this, BeforeActivity.class);
                        intent1.putExtra("Disaster", "Wildfires");
                        startActivity(intent1);
                    }
                }
                else if (cmd.contains("during")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent1 = new Intent(AfterActivity.this, DuringActivity.class);
                        intent1.putExtra("Disaster", "Earthquakes");
                        startActivity(intent1);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")){
                        Intent intent1 = new Intent(AfterActivity.this, DuringActivity.class);
                        intent1.putExtra("Disaster", "Wildfires");
                        startActivity(intent1);
                    }
                }
                else if (cmd.contains("after")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent1 = new Intent(AfterActivity.this, AfterActivity.class);
                        intent1.putExtra("Disaster", "Earthquakes");
                        startActivity(intent1);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent1 = new Intent(AfterActivity.this, AfterActivity.class);
                        intent1.putExtra("Disaster", "Wildfires");
                        startActivity(intent1);
                    }
                }
                else if (cmd.contains("nearMe")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent1 = new Intent(AfterActivity.this, DisasterMapActivity.class);
                        intent1.putExtra("Disaster", "Earthquakes");
                        startActivity(intent1);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent1 = new Intent(AfterActivity.this, DisasterMapActivity.class);
                        intent1.putExtra("Disaster", "Wildfires");
                        startActivity(intent1);
                    }
                }
                else if (cmd.contains("show")) {
                    Intent intent1 = new Intent(AfterActivity.this, EmergencyContactsActivity.class);
                    startActivity(intent1);
                }
                else if (cmd.contains("checklist")) {
                    Intent intent1 = new Intent(AfterActivity.this, ChecklistActivity2.class);
                    startActivity(intent1);
                }
                else if (cmd.contains("near")) {
                    Intent intent1 = new Intent(AfterActivity.this, NearbyFacilitiesActivity.class);
                    startActivity(intent1);
                }
                else if (cmd.contains("left")) {
                    //TODO
                    Intent intent1 = new Intent(AfterActivity.this, ChecklistActivity2.class);
                    startActivity(intent1);
                }
                else if (cmd.contains("navigate")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("home")){
                        Intent intent1 = new Intent(AfterActivity.this, HomeScreenActivity.class);
                        startActivity(intent1);
                    }
                    else if (cmd.substring(i, j).equals("emergency contacts") || cmd.substring(i, j).equals("contacts")) {
                        Intent intent1 = new Intent(AfterActivity.this, EmergencyContactsActivity.class);
                        startActivity(intent1);
                    }
                    else if (cmd.substring(i, j).equals("emergency checklist") || cmd.substring(i, j).equals("checklist")) {
                        Intent intent1 = new Intent(AfterActivity.this, ChecklistActivity2.class);
                        startActivity(intent1);
                    }
                    else if (cmd.substring(i, j).equals("nearby facilities")){
                        Intent intent1 = new Intent(AfterActivity.this, NearbyFacilitiesActivity.class);
                        startActivity(intent1);
                    }
                    else if (cmd.substring(i, j).equals("disaster warnings") || cmd.substring(i, j).equals("disaster updates") || cmd.substring(i, j).equals("map")) {
                        Intent intent1 = new Intent(AfterActivity.this, DisasterMapActivity.class);
                        startActivity(intent1);
                    }
                }
            }
        };

        alan_button.registerCallback(myCallback);

        backButton = findViewById(R.id.back_button);
        home = findViewById(R.id.home_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterActivity.this, BDAActivity.class);
                intent.putExtra("Disaster", disaster);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterActivity.this, HomeScreenActivity.class);
                intent.putExtra("Disaster", disaster);
                startActivity(intent);
            }
        });
    }
}
