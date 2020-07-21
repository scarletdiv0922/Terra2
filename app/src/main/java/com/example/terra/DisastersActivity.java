package com.example.terra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.ScriptMethodCallback;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DisastersActivity extends AppCompatActivity {

    ArrayList<String> disasters = new ArrayList<>();
    ArrayAdapter<String> displayDisasters;
    ListView disasterOptions;
    Button continueButton;
    private Firebase mRef;
    private static final String TAG = "DisastersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disasters);

        //Connect activity to Firebase
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

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
                        Intent intent = new Intent(DisastersActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")) {
                        Intent intent = new Intent(DisastersActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("during")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(DisastersActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(DisastersActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("after")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(DisastersActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(DisastersActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("nearMe")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(DisastersActivity.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(DisastersActivity.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("show"))
                    alan_button.playText("You are already on the emergency contacts screen.");
                else if (cmd.contains("checklist")) {
                    Intent intent = new Intent(DisastersActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("near")) {
                    //TODO
                    Toast.makeText(DisastersActivity.this, "near", Toast.LENGTH_SHORT).show();
                }
                else if (cmd.contains("left")) {
                    //TODO
                    Intent intent = new Intent(DisastersActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("about")) {
                    alan_button.playText("You can ask about anything related to natural disasters, risk scores, readiness scores, and other features of the app. Try asking, “What is my risk/readiness score?");
                }
                else if (cmd.contains("navigate")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("home")){
                        Intent intent = new Intent(DisastersActivity.this, HomeScreenActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency contacts") || cmd.substring(i, j).equals("contacts"))
                        alan_button.playText("You are already at the emergency contacts screen.");
                    else if (cmd.substring(i, j).equals("emergency checklist") || cmd.substring(i, j).equals("checklist")) {
                        Intent intent = new Intent(DisastersActivity.this, ChecklistActivity2.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("nearby facilities")){
                        //TODO
                        Toast.makeText(DisastersActivity.this, "near", Toast.LENGTH_SHORT).show();
                    }
                    else if (cmd.substring(i, j).equals("disaster warnings") || cmd.substring(i, j).equals("disaster updates") || cmd.substring(i, j).equals("map")) {
                        Intent intent = new Intent(DisastersActivity.this, DisasterMapActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    alan_button.playText("Something went wrong. Please try again.");
                }
            }
        };

        alan_button.registerCallback(myCallback);


        disasterOptions = findViewById(R.id.disaster_options);
        continueButton = findViewById(R.id.continue_button);

        disasters.add("Earthquakes");
        disasters.add("Wildfires");

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateFirebase()) {
                    Intent intent = new Intent(DisastersActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(DisastersActivity.this, "Please select at least one disaster to learn about.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fillList();

        disasterOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) disasterOptions.getItemAtPosition(position); //Store checklist item that was clicked
                Log.v(TAG, "Clicked on " + item + "at position " + position);
            }
        });
    }

    public void fillList() {
        displayDisasters = new ArrayAdapter<String>(
                DisastersActivity.this,
                R.layout.list_item,
                disasters
        );

        disasterOptions.setAdapter(displayDisasters);

        for (int i = 0; i < disasters.size(); i++) {
            disasterOptions.setItemChecked(i, false);
        }
    }

    public boolean updateFirebase() {

        boolean trueFalse = false;
        SparseBooleanArray sp = disasterOptions.getCheckedItemPositions();

        for (int i = 0; i < sp.size(); i++) {
            if (sp.valueAt(i)) {
                Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("selected_disasters")
                        .child(disasters.get(i));
                mRefChild.setValue(true);
                System.out.println(disasters.get(i) + "is true");
                trueFalse = true;
            }
            else {
                Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("selected_disasters")
                        .child(disasters.get(i));
                System.out.println(disasters.get(i) + "is false");
                mRefChild.setValue(false);
            }
        }
        return trueFalse;
    }
}
