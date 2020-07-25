package com.example.terra;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ChecklistActivity2 extends AppCompatActivity {

    ImageButton backButton;
    ListView checklist;
    ArrayAdapter<String> displayChecklist;
    private Firebase mRef;
    ArrayList<String> checklistItems = new ArrayList<>();
    ArrayList<Boolean> updatedValues = new ArrayList<>();
    ArrayList<Boolean> firebaseValues = new ArrayList<>();
    public int SIZE_OF_CHECKLIST;
    private static final String TAG = "ChecklistActivity2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        JSONObject commandJson = null;

        try {
            commandJson = new JSONObject("{\"command\":\"navigate\", \"screen\": \"settings\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AlanButton alan_button;
        alan_button = findViewById(R.id.alan_button);

        AlanConfig config = AlanConfig.builder()
                .setProjectId("9852fc5b086a6dc66a51e5f59c5b909a2e956eca572e1d8b807a3e2338fdd0dc/stage")
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
                //newly added
                else if (cmd.contains("set score")){
                    alan_button.playText("Okay, got it.");
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
                        Intent intent = new Intent(ChecklistActivity2.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")) {
                        Intent intent = new Intent(ChecklistActivity2.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("during")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(ChecklistActivity2.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(ChecklistActivity2.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("after")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(ChecklistActivity2.this, ChecklistActivity2.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(ChecklistActivity2.this, ChecklistActivity2.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("nearMe")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(ChecklistActivity2.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(ChecklistActivity2.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("show"))
                    alan_button.playText("You are already on the emergency contacts screen.");
                else if (cmd.contains("checklist")) {
                    Intent intent = new Intent(ChecklistActivity2.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("near")) {
                    Intent intent = new Intent(ChecklistActivity2.this, NearbyFacilitiesActivity.class);
                    startActivity(intent);
                }
                else if (cmd.contains("left")) {
                    //TODO
                    Intent intent = new Intent(ChecklistActivity2.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("about")) {
                    alan_button.playText("You can ask about anything related to natural disasters, risk scores, readiness scores, and other features of the app. Try asking, “What is my risk/readiness score?");
                }
                else if (cmd.contains("navigate")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("home")){
                        Intent intent = new Intent(ChecklistActivity2.this, HomeScreenActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency contacts") || cmd.substring(i, j).equals("contacts"))
                        alan_button.playText("You are already at the emergency contacts screen.");
                    else if (cmd.substring(i, j).equals("emergency checklist") || cmd.substring(i, j).equals("checklist")) {
                        Intent intent = new Intent(ChecklistActivity2.this, ChecklistActivity2.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("nearby facilities")){
                        Intent intent = new Intent(ChecklistActivity2.this, NearbyFacilitiesActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("disaster warnings") || cmd.substring(i, j).equals("disaster updates") || cmd.substring(i, j).equals("map")) {
                        Intent intent = new Intent(ChecklistActivity2.this, DisasterMapActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    alan_button.playText("Something went wrong. Please try again.");
                }
            }
        };

        alan_button.registerCallback(myCallback);

        //Connect activity to Firebase
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        backButton = findViewById(R.id.back_button);
        checklist = findViewById(R.id.listv);

        //Set onClickListener to go back to Home Screen
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChecklistActivity2.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });

        getChecklist();

        //Set onItemClickListener to check when list item is clicked
        checklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) checklist.getItemAtPosition(position); //Store checklist item that was clicked
                Log.v(TAG, "Clicked on " + item + "at position " + position);

                //If the value of the item was true, then update it to false
                if (updatedValues.get(checklistItems.indexOf(item))) {
                    updatedValues.set(checklistItems.indexOf(item), false);
                    view.setSelected(false);
                }

                //If the value of the item was false, then update it to true
                else {
                    updatedValues.set(checklistItems.indexOf(item), true);
                    view.setSelected(true);
                }
            }
        });
    }

    //When the user leaves the activity, empty the lists so that they can be loaded from
    //Firebase when the user comes back.

    @Override
    protected void onStop() {
        super.onStop();
        setChecklist();
        checklistItems.clear();
        updatedValues.clear();
        firebaseValues.clear();
    }

    //Load the items in the checklist from Firebase when the user enters the activity
    public void getChecklist() {
        Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist"); //Get the child element
        mRefChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : map.entrySet()){

                        //Get the emergency checklist item and its value
                        Boolean value = (Boolean) entry.getValue();
                        String item = entry.getKey();

                        //Add the item and value to their respective ArrayLists
                        checklistItems.add(item);
                        firebaseValues.add(value);
                    }

                    SIZE_OF_CHECKLIST = firebaseValues.size();

                    //Make a copy of the original Firebase values to be updated as the user selects items in the activity
                    updatedValues.addAll(firebaseValues);

                    //Inflate the checklist ListView with the items retrieved from Firebase
                    displayChecklist = new ArrayAdapter<String>(
                            ChecklistActivity2.this,
                            R.layout.list_item,
                            checklistItems
                    );

                    checklist.setAdapter(displayChecklist);
                }

                //If the item is already checked as true, then mark it as checked when the user enters the activity
                for (int i = 0; i < SIZE_OF_CHECKLIST; i++) {
                    if (firebaseValues.get(i)) {
                        checklist.setItemChecked(i,true);
                    }
                }

             }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "Error connecting to Firebase");
            }
        });
    }

    //When the user leaves the activity, take the updated values and update Firebase with them
    public void setChecklist() {
        System.out.println("sparky sparky");
        if (updatedValues.size() > 0) {
            for (int i = 0; i < SIZE_OF_CHECKLIST; i++) {
                Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist")
                        .child(checklistItems.get(i)); //Get the Firebase children under emergency_checklist
                if (updatedValues.get(i) != firebaseValues.get(i)) { //If the new value isn't equal to the original value in Firebase, then update the Firebase value to match
                    if (firebaseValues.get(i)) {
                        Log.v(TAG, checklistItems.get(i) + " is now set to false");
                        mRefChild.setValue(false);
                    }
                    else {
                        Log.v(TAG, checklistItems.get(i) + " is now set to true");
                        mRefChild.setValue(true);
                    }
                }
            }
        }
    }
}
