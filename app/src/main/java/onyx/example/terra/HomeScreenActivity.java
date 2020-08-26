package onyx.example.terra;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.ScriptMethodCallback;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;
import onyx.example.terra.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
    ArrayList<String> emergContacts = new ArrayList<>();
    ArrayList<String> emergPhoneNumbers = new ArrayList<>();
    ArrayList<String> contacts = new ArrayList<>();
    ArrayList<String> phoneNumbers = new ArrayList<>();
    ArrayList<Boolean> checklistValues = new ArrayList<>();
    String readinessValue = "0%";
    int SIZE_OF_CHECKLIST;
    int checkedItems;
    double readinessScore;
    boolean hazardIndexUD = true;
    ArrayList<String> riskScores = new ArrayList<>();
    FloatingActionsMenu fab;

    //Location Updates variables
    static HomeScreenActivity instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentlat;
    double currentlong;

    public static HomeScreenActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        instance = this;

        earthquake = findViewById(R.id.earthquakes);
        wildfire = findViewById(R.id.wildfires);
        fab = findViewById(R.id.fab1);

        //Connect activity to Firebase
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        getFirebase();
        getReadiness();
        getEmergContacts();
        showContacts();
        updateLocation();
        getRisk();

        //Check if the user has allowed the app to send text messages
        if (!checkPermission(Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (!checkPermission(Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

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

        FloatingActionButton checklistFAB = findViewById(R.id.checklist_button);
        FloatingActionButton emergencyContactsFAB = findViewById(R.id.emergency_contacts_button);
        FloatingActionButton infoFAB = findViewById(R.id.info_button);
        FloatingActionButton addFAB = findViewById(R.id.add_disasters_button);

        alan_button.playCommand(commandJson.toString(),  new ScriptMethodCallback() {
            @Override
            public void onResponse(String methodName, String body, String error) {
                System.out.println("Home screen");
                System.out.println(methodName);
            }
        });
        AlanCallback myCallback = new AlanCallback() {
            @Override
            public void onCommandReceived(EventCommand eventCommand) {
                super.onCommandReceived(eventCommand);
                System.out.println("Command received");
                String cmd = eventCommand.getData().toString();
                System.out.println(cmd);
                if (cmd.contains("hazardIndex")){
                    if (hazardIndexUD) {
                        alan_button.playText("Your hazard indices haven't been calculated yet. Please go to your indices screen to find out what your hazard indices are.");
                    }
                    else {
                        for (int i = 0; i < riskScores.size(); i++) {
                            if (riskScores.get(i).equals("100")) {
                                alan_button.playText("Your hazard index for " + disasters.get(i) + " has not been calculated.");
                            }
                            else {
                                alan_button.playText("Your hazard index for " + disasters.get(i) + " is " + riskScores.get(i));
                            }
                        }
                    }
                }
                else if (cmd.contains("preparation index")) {
                    getFirebase();
                    System.out.println("at readiness " + readinessValue);
                    alan_button.playText("Your preparation index is " + readinessValue);
                }
                else if (cmd.contains("addContact")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");

                    if (emergContacts.contains(cmd.substring(i, j))) {
                        alan_button.playText(cmd.substring(i, j) + " is already in your emergency contacts list.");
                    }
                    else {
                        if (contacts.contains(cmd.substring(i, j))) {
                            int index = contacts.indexOf(cmd.substring(i, j));
                            String number = phoneNumbers.get(index);
                            Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_contacts")
                                    .child(cmd.substring(i, j));
                            mRefChild.setValue(number);
                            alan_button.playText("Added " + cmd.substring(i, j) + " to your contacts");
                        } else {
                            alan_button.playText("I don't know if " + cmd.substring(i, j) + " is in your contacts list.");
                        }
                    }
                }
                else if (cmd.contains("removeContact")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");

                    if (emergContacts.contains(cmd.substring(i, j))) {
                        mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_contacts")
                                .child(cmd.substring(i, j)).removeValue();
                        alan_button.playText("Removed " + cmd.substring(i, j) + "from your emergency contacts.");
                    }
                    else {
                        alan_button.playText("I don't think " + cmd.substring(i, j) + "is in your emergency contacts.");
                    }
                }
                else if (cmd.contains("call")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    String name = cmd.substring(i, j);

                    if (contacts.contains(name)) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        String number = "tel:" + phoneNumbers.get(contacts.indexOf(name));
                        callIntent.setData(Uri.parse(number));

                        if (ActivityCompat.checkSelfPermission(HomeScreenActivity.this,
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(callIntent);
                    }
                    else {
                        alan_button.playText("I am unable to call " + name);
                    }
                }
                else if (cmd.contains("safe")){
                    for (int i = 0; i < emergPhoneNumbers.size(); i++) {
                        sendSafeText(emergPhoneNumbers.get(i));
                    }
                    alan_button.playText("Your contacts have been informed that you are safe");
                }
                else if (cmd.contains("help")){
                    for (int i = 0; i < emergPhoneNumbers.size(); i++) {
                        sendHelpText(emergPhoneNumbers.get(i));
                    }
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
                        Intent intent = new Intent(HomeScreenActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")) {
                        Intent intent = new Intent(HomeScreenActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("during")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(HomeScreenActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(HomeScreenActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("after")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(HomeScreenActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(HomeScreenActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("nearMe")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(HomeScreenActivity.this, EarthquakeMapActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(HomeScreenActivity.this, EarthquakeMapActivity.class);
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
                    Intent intent = new Intent(HomeScreenActivity.this, NearbyFacilitiesActivity.class);
                    startActivity(intent);
                }
                else if (cmd.contains("left")) {
                    int count = 0;
                    for (int i = 0; i < checklistValues.size(); i++) {
                        if (!checklistValues.get(i)) {
                            count++;
                        }
                    }
                    alan_button.playText("You have " + count + " items left in your emergency checklist.");
                }
                else if (cmd.contains("about")) {
                    alan_button.playText("You can ask about anything related to natural disasters, risk scores, readiness scores, and other features of the app. Try asking, “What is my risk/readiness score?");
                }
                else if (cmd.contains("navigate")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("home"))
                        alan_button.playText("You are already at the home screen.");
                    else if (cmd.substring(i, j).equals("emergency contacts") || cmd.substring(i, j).equals("contacts")) {
                        Intent intent = new Intent(HomeScreenActivity.this, EmergencyContactsActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency checklist") || cmd.substring(i, j).equals("checklist")) {
                        Intent intent = new Intent(HomeScreenActivity.this, ChecklistActivity2.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("nearby facilities")){
                        Intent intent = new Intent(HomeScreenActivity.this, NearbyFacilitiesActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("disaster warnings") || cmd.substring(i, j).equals("disaster updates") || cmd.substring(i, j).equals("map")) {
                        Intent intent = new Intent(HomeScreenActivity.this, EarthquakeMapActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    alan_button.playText("Something went wrong. Please try again.");
                }
            }
        };

        getSelectedDisasters();

        alan_button.registerCallback(myCallback);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(HomeScreenActivity.this, DisastersActivity.class);
                startActivity(intent);

            }
        });
        emergencyContactsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(HomeScreenActivity.this, EmergencyContactsActivity.class);
                startActivity(intent);

            }
        });

        infoFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(HomeScreenActivity.this, NearbyFacilitiesActivity.class);
                startActivity(intent);

            }
        });

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

                fab.setVisibility(View.VISIBLE);
                fab.setEnabled(true);

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

    public void getFirebase() {

        Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("readiness_score");
        mRefChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                readinessValue = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getReadiness() {
        Firebase mRefChild1 = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist");
        mRefChild1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("That's");
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        Boolean value = (Boolean) entry.getValue();
                        checklistValues.add(value);
                        String item = entry.getKey();
                        if (item.equals("Water") || item.equals("Non-perishable Food") || item.equals("First-aid Kit")) {
                            SIZE_OF_CHECKLIST +=3;
                            if (value){
                                System.out.println("rough");
                                checkedItems += 3;
                            }
                        }
                        else if (item.equals("Portable Charger") || item.equals("Extra Cash") || item.equals("Flashlight") || item.equals("Extra Batteries")) {
                            SIZE_OF_CHECKLIST +=2;
                            if (value){
                                System.out.println("buddy");
                                checkedItems += 2;
                            }
                        }
                        else {
                            SIZE_OF_CHECKLIST++;
                            if (value){
                                System.out.println("- Z");
                                checkedItems ++;
                            }
                        }
                    }
                    Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("readiness_score");
                    readinessScore = (double) checkedItems / (double) SIZE_OF_CHECKLIST;
                    mRefChild.setValue((readinessScore*100) + "%");
                    mRefChild.setValue(String.format("%.2f", (readinessScore*100)) + "%");
                    System.out.println("CHECKED " + checkedItems);
                    checkedItems = 0;
                    SIZE_OF_CHECKLIST = 0;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getRisk() {
        System.out.println("RISK 1");
        Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("risk_score");
        mRefChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("RISK 2");
                int count = disasters.size();
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
//                    try {
//                        Double score = (Double) dataSnapshot.getValue();
//                        riskScores.add(score);
//                        riskScoreUD = false;
//                        System.out.println("FALSE");
//                    } catch (Exception e) {
//                        String s = (String) entry.getValue();
//                        if (s.equals("undefined")) {
//                            count--;
//                            riskScores.add(100.0);
//                            if (count == 0) {
//                                System.out.println("TRUE");
//                                riskScoreUD = true;
//                            }
//                        }
//                    }
                    String score = (String) entry.getValue();
                    if (score.equals("undefined")) {
                        count--;
                        riskScores.add("100");
                        if (count == 0) {
                            System.out.println("TRUE");
                            hazardIndexUD = true;
                        }
                    }
                    else {
                        riskScores.add(score);
                        hazardIndexUD = false;
                        System.out.println("FALSE");
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    //Check the permissions
    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    //Send the "safe" text message to the emergency contacts
    public void sendSafeText(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            return;
        }
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null,"I have been hit by a natural disaster, but I'm safe. I am at http://maps.google.com/?q=" + currentlat + "," + currentlong + ". From, Terra, my natural disaster app.", null, null);
            Toast.makeText(HomeScreenActivity.this, "Your contacts have been informed you are safe.", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(HomeScreenActivity.this, "You did not give Terra permission to send text messages.", Toast.LENGTH_LONG).show();
        }
    }

    //Sned the "help" text message to the emergency contacts
    public void sendHelpText(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            return;
        }
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null,"I have been hit by a natural disaster, and I need help! I am at http://maps.google.com/?q=" + currentlat + "," + currentlong + ". From, Terra, my natural disaster app.", null, null);
            Toast.makeText(HomeScreenActivity.this, "Your contacts have been informed you need help.", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(HomeScreenActivity.this, "You did not give Terra permission to send text messages.", Toast.LENGTH_LONG).show();
        }
    }

    public void getEmergContacts() {
        Firebase mRefChild1 = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_contacts");
        mRefChild1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        String contact = entry.getKey();
                        String number = (String) entry.getValue();

                        emergContacts.add(contact);
                        emergPhoneNumbers.add(number);
                        System.out.println("CONTACT " + contact);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showContacts() {
        //If the permission isn't granted and the SDK version isn't high enough
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            //Then request the user permission to access contacts
            ActivityCompat.requestPermissions(HomeScreenActivity.this,
                    new String[] { Manifest.permission.READ_CONTACTS }, 1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method
        }

        //If the user has already accepted the permission
        else {
            //Get the user's contacts
            getContactList();
        }
    }

    //Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {

            //If the permission has been granted, run showContacts() again and move on to the next step
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            }

            //If the permission hasn't been granted, handle it with an error message
            else {
                Toast.makeText(this, "Without your permission, Terra cannot access your contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Retrieve the user's contact list
    private void getContactList() {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacts.add(name); //Add each contact name to the ArrayList of contact names
                        phoneNumbers.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null){
            cur.close();
        }

    }

    private void updateLocation() {
        buildLocationRequest(); //request to get the current location

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
        System.out.println("updateLocation2");


    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationService3.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        System.out.println("pendingIntent2");
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //get the location at high accuracy
        System.out.println("buildLocationRequest2");
    }

    public void setCoordinates(final double lat, final double lon) {
        System.out.println("setting coords2");
        HomeScreenActivity.this.runOnUiThread(new Runnable() { //while this activity is running
            @Override
            public void run() {
                currentlat = lat;
                currentlong = lon;

                System.out.println("owo"+currentlat+"/"+currentlong);
            }
        });
    }
}