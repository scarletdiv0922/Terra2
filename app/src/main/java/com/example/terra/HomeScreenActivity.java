package com.example.terra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.ScriptMethodCallback;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class HomeScreenActivity extends AppCompatActivity {

    Button earthquake;
    Button wildfire;
    private Firebase mRef;
    ArrayList<String> disasters = new ArrayList<>();
    ArrayList<Boolean> values = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        earthquake = findViewById(R.id.earthquakes);
        wildfire = findViewById(R.id.wildfires);

        //Connect activity to Firebase
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        JSONObject commandJson = null;

        try {
            commandJson = new JSONObject("{\"command\":\"navigate\", \"screen\": \"settings\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AlanButton alanButton;
        alanButton = findViewById(R.id.alan_button_home_screen);
        AlanConfig config = AlanConfig.builder()
                .setProjectId("9852fc5b086a6dc66a51e5f59c5b909a2e956eca572e1d8b807a3e2338fdd0dc/stage")
                .build();
        alanButton.initWithConfig(config);

        FloatingActionButton checklistFAB = findViewById(R.id.checklist_button);
        FloatingActionButton emergencyContactsFAB = findViewById(R.id.emergency_contacts_button);
        FloatingActionButton infoFAB = findViewById(R.id.info_button);

        alanButton.playCommand(commandJson.toString(),  new ScriptMethodCallback() {
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
                if (cmd.contains("riskScore")){
                    alanButton.playText("Your risk score is 7%");
                }
                else if (cmd.contains("readinessScore")){
                    //TODO
                    alanButton.playText("Your readiness score is 90%");
                }
                else if (cmd.contains("addContact")){
                    int i = cmd.indexOf("value: ");
                    int j = cmd.indexOf("}");
                    alanButton.playText("Added " + cmd.substring(i, j) + "to your contacts");
                }
                else if (cmd.contains("removeContact")){
                    int i = cmd.indexOf("value: ");
                    int j = cmd.indexOf("}");
                    alanButton.playText("Removed " + cmd.substring(i, j) + "to your contacts");
                }
                else if (cmd.contains("safe")){
                    alanButton.playText("Your contacts have been informed that you are safe");
                }
                else if (cmd.contains("help")){
                    alanButton.playText("Your contacts have been informed that you need help");
                }
                else if (cmd.contains("mark")){
//                    int i = cmd.indexOf("value: ");
//                    int j = cmd.indexOf("}");
                    alanButton.playText("(item) has been checked on your emergency checklist");
                }
                else if (cmd.contains("before")){
                    int i = cmd.indexOf("value: ");
                    int j = cmd.indexOf("}");
                    if (cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(HomeScreenActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(HomeScreenActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("during")){
                    int i = cmd.indexOf("value: ");
                    int j = cmd.indexOf("}");
                    if (cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(HomeScreenActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(HomeScreenActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("after")){
                    int i = cmd.indexOf("value: ");
                    int j = cmd.indexOf("}");
                    if (cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(HomeScreenActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(HomeScreenActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("nearMe")) {
                    int i = cmd.indexOf("value: ");
                    int j = cmd.indexOf("}");
                    if (cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(HomeScreenActivity.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(HomeScreenActivity.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("show")) {
                    Intent intent = new Intent(HomeScreenActivity.this, EmergencyContactsActivity.class);
                    startActivity(intent);
                }
                else if (cmd.contains("checklist")) {
                    Intent intent = new Intent(HomeScreenActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("near")) {
                    //TODO
                    Toast.makeText(HomeScreenActivity.this, "near", Toast.LENGTH_SHORT).show();
                }
                else if (cmd.contains("left")) {
                    //TODO
                    Intent intent = new Intent(HomeScreenActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("about")) {
                    alanButton.playText("You can ask about anything related to natural disasters, risk scores, readiness scores, and other features of the app. Try asking, “What is my risk/readiness score?");
                }
                else if (cmd.contains("navigate")) {
                    int i = cmd.indexOf("value: ");
                    int j = cmd.indexOf("}");
                    if (cmd.substring(i, j).equals("home"))
                        alanButton.playText("You are already at the home screen.");
                    else if (cmd.substring(i, j).equals("emergency contacts") || cmd.substring(i, j).equals("contacts")) {
                        Intent intent = new Intent(HomeScreenActivity.this, EmergencyContactsActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency checklist") || cmd.substring(i, j).equals("checklist")) {
                        Intent intent = new Intent(HomeScreenActivity.this, ChecklistActivity2.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("nearby facilities")){
                        //TODO
                        Toast.makeText(HomeScreenActivity.this, "near", Toast.LENGTH_SHORT).show();
                    }
                    else if (cmd.substring(i, j).equals("disaster warnings") || cmd.substring(i, j).equals("disaster updates") || cmd.substring(i, j).equals("map")) {
                        Intent intent = new Intent(HomeScreenActivity.this, DisasterMapActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    alanButton.playText("Oops something went wrong. Please try again.");
                }
            }
        };

        getSelectedDisasters();

        alanButton.registerCallback(myCallback);
        emergencyContactsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(HomeScreenActivity.this, EmergencyContactsActivity.class);
                startActivity(intent);

            }
        });

//        infoFAB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                Intent intent = new Intent(HomeScreenActivity.this, BDAActivity.class);
//                startActivity(intent);
//
//            }
//        });

        checklistFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeScreenActivity.this, ChecklistActivity2.class);
                startActivity(intent);

            }
        });

        earthquake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, DisasterMenuActivity.class);
                intent.putExtra("Disaster", "Earthquakes");
                startActivity(intent);
            }
        });

        wildfire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, DisasterMenuActivity.class);
                intent.putExtra("Disaster", "Wildfires");
                startActivity(intent);
            }
        });

    }

    public void getSelectedDisasters() {
        Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("selected_disasters"); //Get the child element
        mRefChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {

                        String disaster = entry.getKey();
                        Boolean value = (Boolean) entry.getValue();

                        disasters.add(disaster);
                        values.add(value);

                    }
                }
                showButtons();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void showButtons() {
        for (int i = 0; i < disasters.size(); i++) {
            if (values.get(i)) {
                if (disasters.get(i).equals("Earthquakes")) {
                    earthquake.setVisibility(View.VISIBLE);
                    earthquake.setEnabled(true);
                }

                if (disasters.get(i).equals("Wildfires")) {
                    wildfire.setVisibility(View.VISIBLE);
                    wildfire.setEnabled(true);
                }
            }
        }
    }
}