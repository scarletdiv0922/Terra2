package com.example.terra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONObject commandJson = null;

        try {
            commandJson = new JSONObject("{\"command\":\"navigate\", \"screen\": \"settings\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                System.out.println("Heeereeee");
                String cmd = eventCommand.getData().toString();
                System.out.println(cmd);
                if (cmd.contains("riskScore")){
                    alan_button.playText("Your risk score is 7%");
                }
                else if (cmd.contains("readinessScore")){
                    //TODO
                    alan_button.playText("Your readiness score is 90%");
                }
                else if (cmd.contains("addContact")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    System.out.println("CMD: " + cmd + "I: " + i + "J: " + j);
                    //TODO add try/catch to make sure that we're actually able to add a contact that exists
                    System.out.println(cmd.substring(i, j));
                    alan_button.playText("Added " + cmd.substring(i, j) + "to your contacts");
                }
                else if (cmd.contains("removeContact")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    //TODO add try/catch to make sure that we're actually able to remove a contact that exists
//                    mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_contacts")
//                            .child(cmd.substring(i, j)).removeValue();
                    alan_button.playText("Removed " + cmd.substring(i, j) + "from your contacts");
                }
                else if (cmd.contains("safe")){
                    alan_button.playText("Your contacts have been informed that you are safe");
                }
                else if (cmd.contains("help")){
                    alan_button.playText("Your contacts have been informed that you need help");
                }
                else if (cmd.contains("mark")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    alan_button.playText(cmd.substring(i, j) + " has been checked on your emergency checklist");
                }
                else if (cmd.contains("before")){
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
                        Intent intent = new Intent(DisasterMenuActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(DisasterMenuActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("nearMe")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(DisasterMenuActivity.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(DisasterMenuActivity.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("show"))
                    alan_button.playText("You are already on the emergency contacts screen.");
                else if (cmd.contains("checklist")) {
                    Intent intent = new Intent(DisasterMenuActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("near")) {
                    //TODO
                    Toast.makeText(DisasterMenuActivity.this, "near", Toast.LENGTH_SHORT).show();
                }
                else if (cmd.contains("left")) {
                    //TODO
                    Intent intent = new Intent(DisasterMenuActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("about")) {
                    alan_button.playText("You can ask about anything related to natural disasters, risk scores, readiness scores, and other features of the app. Try asking, “What is my risk/readiness score?");
                }
                else if (cmd.contains("navigate")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("home")){
                        Intent intent = new Intent(DisasterMenuActivity.this, HomeScreenActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency contacts") || cmd.substring(i, j).equals("contacts"))
                        alan_button.playText("You are already at the emergency contacts screen.");
                    else if (cmd.substring(i, j).equals("emergency checklist") || cmd.substring(i, j).equals("checklist")) {
                        Intent intent = new Intent(DisasterMenuActivity.this, ChecklistActivity2.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("nearby facilities")){
                        //TODO
                        Toast.makeText(DisasterMenuActivity.this, "near", Toast.LENGTH_SHORT).show();
                    }
                    else if (cmd.substring(i, j).equals("disaster warnings") || cmd.substring(i, j).equals("disaster updates") || cmd.substring(i, j).equals("map")) {
                        Intent intent = new Intent(DisasterMenuActivity.this, DisasterMapActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    alan_button.playText("Something went wrong. Please try again.");
                }
            }
        };

        alan_button.registerCallback(myCallback);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            disaster = (String) bundle.get("Disaster");
        }

        if (disaster.equals("Earthquakes")) {
            setContentView(R.layout.activity_earthquake_menu);
        }

        else if (disaster.equals("Wildfires")) {
            setContentView(R.layout.activity_wildfire_menu);
        }

        information = findViewById(R.id.info_button);
        scores = findViewById(R.id.scores_button);
        updates = findViewById(R.id.updates_button);
        home = findViewById(R.id.home_button);
        back = findViewById(R.id.back_button);

        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, BDAActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, HomeScreenActivity.class);
                startActivity(intent1);
            }
        });

        scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, RiskReadinessActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, DisasterMapActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, HomeScreenActivity.class);
                startActivity(intent1);
            }
        });
    }
}
