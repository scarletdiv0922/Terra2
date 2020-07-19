package com.example.terra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
                if (cmd.contains("risk score")){
                    alanButton.playText("Your risk score is 7%");
                }
                else if (cmd.contains("readiness score")){
                    alanButton.playText("Your readiness score is 90%");
                }
                else if (cmd.contains("setContacts ")){
                    int i = cmd.indexOf("value: ");
                    alanButton.playText("Added " + cmd.substring(i, 4) + "to your contacts");
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