package com.example.terra;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class EmergencyContactsActivity extends AppCompatActivity {

    private AlanButton alanButton;
    TextView noContacts;
    TextView instructions;
    ArrayAdapter<String> displayContacts;
    ListView emergContacts;
    FloatingActionButton removeContacts;
    FloatingActionButton addContacts;
    FloatingActionButton safe;
    FloatingActionButton help;
    ImageButton backButton;
    private Firebase mRef;
    ArrayList<String> contacts = new ArrayList<>();
    ArrayList<String> phoneNumbers = new ArrayList<>();
    private static final String TAG = "EmergencyContacts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        //Connect to Firebase
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        //Get the user's emergency contacts from Firebase
        updateContacts();

        noContacts = findViewById(R.id.no_contacts);
        emergContacts = findViewById(R.id.listv);
        instructions = findViewById(R.id.instructions);
        safe = findViewById(R.id.safe);
        help = findViewById(R.id.help);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyContactsActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });

//        getSupportActionBar().hide();

        FloatingActionButton checklistFAB = findViewById(R.id.checklist_button);
        FloatingActionButton emergencyContactsFAB = findViewById(R.id.emergency_contacts_button);
        FloatingActionButton infoFAB = findViewById(R.id.info_button);

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
                        Intent intent = new Intent(EmergencyContactsActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")) {
                        Intent intent = new Intent(EmergencyContactsActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("during")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(EmergencyContactsActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(EmergencyContactsActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("after")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(EmergencyContactsActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(EmergencyContactsActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("nearMe")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(EmergencyContactsActivity.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(EmergencyContactsActivity.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("show"))
                    alan_button.playText("You are already on the emergency contacts screen.");
                else if (cmd.contains("checklist")) {
                    Intent intent = new Intent(EmergencyContactsActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("near")) {
                    //TODO
                    Toast.makeText(EmergencyContactsActivity.this, "near", Toast.LENGTH_SHORT).show();
                }
                else if (cmd.contains("left")) {
                    //TODO
                    Intent intent = new Intent(EmergencyContactsActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("about")) {
                    alan_button.playText("You can ask about anything related to natural disasters, risk scores, readiness scores, and other features of the app. Try asking, “What is my risk/readiness score?");
                }
                else if (cmd.contains("navigate")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("home")){
                        Intent intent = new Intent(EmergencyContactsActivity.this, HomeScreenActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency contacts") || cmd.substring(i, j).equals("contacts"))
                        alan_button.playText("You are already at the emergency contacts screen.");
                    else if (cmd.substring(i, j).equals("emergency checklist") || cmd.substring(i, j).equals("checklist")) {
                        Intent intent = new Intent(EmergencyContactsActivity.this, ChecklistActivity2.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("nearby facilities")){
                        //TODO
                        Toast.makeText(EmergencyContactsActivity.this, "near", Toast.LENGTH_SHORT).show();
                    }
                    else if (cmd.substring(i, j).equals("disaster warnings") || cmd.substring(i, j).equals("disaster updates") || cmd.substring(i, j).equals("map")) {
                        Intent intent = new Intent(EmergencyContactsActivity.this, DisasterMapActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    alan_button.playText("Something went wrong. Please try again.");
                }
            }
        };

        alan_button.registerCallback(myCallback);

        addContacts = findViewById(R.id.add_contact);
        addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyContactsActivity.this, ContactsActivity2.class);
                startActivity(intent);
            }
        });

        removeContacts = findViewById(R.id.remove_contact);
        removeContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyContactsActivity.this, RemoveContactActivity.class);
                startActivity(intent);
            }
        });

        //Check if the user has allowed the app to send text messages
        if (!checkPermission(Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        //When the user long-clicks on the "I'm safe!" button, send a text to each emergency contact
        safe.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (int i = 0; i < phoneNumbers.size(); i++) {
                    sendSafeText(phoneNumbers.get(i));
                }
                return false;
            }
        });

        //When the user long-clicks on the "Help!" button, send a text to each emergency contact
        help.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (int i = 0; i < phoneNumbers.size(); i++) {
                    sendHelpText(phoneNumbers.get(i));
                }
                return false;
            }
        });
    }

    //Send the "safe" text message to the emergency contacts
    public void sendSafeText(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            return;
        }
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null,"I have been hit by a natural disaster, but I'm safe! From, Terra.", null, null);
            Toast.makeText(EmergencyContactsActivity.this, "Your contacts have been informed you are safe.", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(EmergencyContactsActivity.this, "You did not give Terra permission to send text messages.", Toast.LENGTH_LONG).show();
        }
    }

    //Sned the "help" text message to the emergency contacts
    public void sendHelpText(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            return;
        }
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null,"I have been hit by a natural disaster, and I need help! From, Terra.", null, null);
            Toast.makeText(EmergencyContactsActivity.this, "Your contacts have been informed you need help.", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(EmergencyContactsActivity.this, "You did not give Terra permission to send text messages.", Toast.LENGTH_LONG).show();
        }
    }

    //Check the permissions
    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    //Retrieve the user's emergency contacts from Firebase and populate the emergency contacts ListView
    public void updateContacts() {
        //Access the children in Firebase's emergency_contacts
        Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_contacts");

        mRefChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : map.entrySet()){

                        //Get the contact name and phone number
                        String contactPhone = (String) entry.getValue();
                        String contactName = entry.getKey();

                        Log.v(TAG, "NAME: " + contactName + "\n PHONE: " + contactPhone);

                        contacts.add(contactName + "\n" + contactPhone); //Format the contact name and number and add it to the contacts list
                        phoneNumbers.add(contactPhone); //Save the phone number to the phoneNumbers ArrayList
                    }

                    //If the user has saved emergency contacts, then remove the message on the screen and populate the ListView
                    if (contacts.size() != 0) {
                        noContacts.setVisibility(View.INVISIBLE);

                        displayContacts = new ArrayAdapter<String>(
                                EmergencyContactsActivity.this,
                                R.layout.list_item,
                                contacts
                        );

                        emergContacts.setAdapter(displayContacts);
                    }

                    //If the user has no saved emergency contacts, display the message that tells them to add some
                    else {
                        noContacts.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "Error connecting to Firebase");
            }
        });
    }
}
