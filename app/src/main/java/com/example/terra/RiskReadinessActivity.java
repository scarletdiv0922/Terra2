package com.example.terra;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.tensorflow.lite.Interpreter;

public class RiskReadinessActivity extends AppCompatActivity {

    private Firebase mRef;
    int checkedItems; //stores weighted amount of how many items are checked
    public int total;
    public int SIZE_OF_CHECKLIST;
    public double readinessScore;
    ImageButton back;
    FloatingActionButton home;
    String disaster = " ";
    private static final String TAG = "RiskReadiness";

    FirebaseModelInterpreterOptions options;
    FirebaseModelInputOutputOptions inputOutputOptions;
    FirebaseModelInterpreter interpreter;
    FirebaseCustomLocalModel localModel;
    FirebaseModelInputs inputs;
    FirebaseCustomRemoteModel remoteModel;
    Float[][][] myInput = new Float[1][1][18]; //contains whether users have a certain item or not
    public double riskScore;

    public double riskFromLoc = 0.0;
    String county = "Alameda";
    Button doneButton;

    HashMap chklistOrder = new HashMap<String, Integer>();
    public int itemsMarked = 0; //just the number of items marked without weight

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_readiness_scores);

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        //Alan Button
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
                        Intent intent = new Intent(RiskReadinessActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")) {
                        Intent intent = new Intent(RiskReadinessActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("during")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(RiskReadinessActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(RiskReadinessActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("after")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(RiskReadinessActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(RiskReadinessActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("nearMe")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(RiskReadinessActivity.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(RiskReadinessActivity.this, DisasterMapActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("show"))
                    alan_button.playText("You are already on the emergency contacts screen.");
                else if (cmd.contains("checklist")) {
                    Intent intent = new Intent(RiskReadinessActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("near")) {
                    Intent intent = new Intent(RiskReadinessActivity.this, NearbyFacilitiesActivity.class);
                    startActivity(intent);
                }
                else if (cmd.contains("left")) {
                    //TODO
                    Intent intent = new Intent(RiskReadinessActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("about")) {
                    alan_button.playText("You can ask about anything related to natural disasters, risk scores, readiness scores, and other features of the app. Try asking, “What is my risk/readiness score?");
                }
                else if (cmd.contains("navigate")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("home")){
                        Intent intent = new Intent(RiskReadinessActivity.this, HomeScreenActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency contacts") || cmd.substring(i, j).equals("contacts")){
                        Intent intent = new Intent(RiskReadinessActivity.this, EmergencyContactsActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency checklist") || cmd.substring(i, j).equals("checklist")) {
                        Intent intent = new Intent(RiskReadinessActivity.this, ChecklistActivity2.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("nearby facilities")){
                        Intent intent = new Intent(RiskReadinessActivity.this, NearbyFacilitiesActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("disaster warnings") || cmd.substring(i, j).equals("disaster updates") || cmd.substring(i, j).equals("map")) {
                        Intent intent = new Intent(RiskReadinessActivity.this, DisasterMapActivity.class);
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

        for (int i = 0; i < 18; i++)
            myInput[0][0][i] = Float.valueOf(0);

        getFirebase();

        Toast.makeText(this, "score " + readinessScore, Toast.LENGTH_SHORT).show();

        //current order of the checklist for ml model, no dust masks for now
        chklistOrder.put("Battery-powered Radio", 1);
        chklistOrder.put("Extra Batteries", 2);
        chklistOrder.put("Extra Cash", 3);
        chklistOrder.put("First-aid Kit", 4);
        chklistOrder.put("Flashlight", 5);
        chklistOrder.put("Garbage Bags", 6);
        chklistOrder.put("Hand Sanitizer", 7);
        chklistOrder.put("Local Maps", 8);
        chklistOrder.put("Manual Can Opener", 9);
        chklistOrder.put("Matchsticks", 10);
        chklistOrder.put("Moist Towelettes", 11);
        chklistOrder.put("Non-perishable Food", 12);
        chklistOrder.put("Portable Charger", 13);
        chklistOrder.put("Prescription Medications", 14);
        chklistOrder.put("Water", 15);
        chklistOrder.put("Whistles", 16);
        chklistOrder.put("Wrench or Pliers", 17);

        //risk of natural disaster based on location
        doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextCounty = findViewById(R.id.editTextCounty);
                county = editTextCounty.getText().toString();
                System.out.println(county);
                InputStream inputStream; //default to earthquakes
                inputStream = getResources().openRawResource(R.raw.eqdata);;
                if (disaster.equalsIgnoreCase("Earthquakes"))
                    inputStream = getResources().openRawResource(R.raw.eqdata);
                else if (disaster.equalsIgnoreCase("Wildfires"))
                    inputStream = getResources().openRawResource(R.raw.firedata);
                System.out.println("which disaster? it's " + disaster);
                CSVFile csvFile = new CSVFile(inputStream);
                ArrayList<ArrayList<String>> riskList = csvFile.read();
                System.out.println(riskList.get(1));
                System.out.println("COUNTYYYYYYYY");
                for(ArrayList<String> scoreData:riskList ) {
                    for (String s:scoreData) {
                        if (s.equals(county)) {
                            String locRisk = scoreData.get(2);
                            if (locRisk.equals("High"))
                                riskFromLoc = 2.0;
                            else if (locRisk.equals("Medium"))
                                riskFromLoc = 1.0;
                            else
                                riskFromLoc = 0.5;
                            System.out.println("At " + locRisk + " risk");
                        }
                    }
                }
                //risk score mathematical model
                double ratio = 0.5;
                if (itemsMarked != 0 && checkedItems != 0)
                    ratio = (double)((checkedItems - itemsMarked)/checkedItems) + 0.1;
                riskScore = ratio * riskFromLoc;
                System.out.println("risk score: " + riskScore);

            }
        });

        //Risk Score ml model
//        remoteModel = new FirebaseCustomRemoteModel.Builder("EQ_model").build();
//        localModel = new FirebaseCustomLocalModel.Builder()
//                .setAssetFilePath("eq_model.tflite")
//                .build(); //local and remote model in case of no internet
//        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
//                .requireWifi()
//                .build();
//        FirebaseModelManager.getInstance().download(remoteModel, conditions)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void v) {
//                        Toast.makeText(RiskReadinessActivity.this, "Model downloaded!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
//                .addOnSuccessListener(new OnSuccessListener<Boolean>() {
//                    @Override
//                    public void onSuccess(Boolean isDownloaded) {
//                        //use either remote model in firebase or local model
//                        if (isDownloaded) {
//                            System.out.println("remote");
//                            options = new FirebaseModelInterpreterOptions.Builder(remoteModel).build();
//                        }
//                        else {
//                            System.out.println("local");
//                            options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
//                        }
//
//                        try {
//                            System.out.println(87);
//                            FirebaseModelInterpreter interpreter = FirebaseModelInterpreter.getInstance(options);
//                            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
//                                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 1, 18})
//                                    .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 1})
//                                    .build();
//                            //get myInput in the getFirebase method
//                            inputs = new FirebaseModelInputs.Builder()
//                                    .add(myInput)  // add() as many input arrays as your model requires
//                                    .build();
//                            System.out.println(107);
//                            interpreter.run(inputs, inputOutputOptions)
//                                    .addOnSuccessListener(
//                                            new OnSuccessListener<FirebaseModelOutputs>() {
//                                                @Override
//                                                public void onSuccess(FirebaseModelOutputs result) {
//                                                    Toast.makeText(RiskReadinessActivity.this, "Model is running!", Toast.LENGTH_SHORT).show();
//                                                    float[][] output = result.getOutput(0);
//                                                    System.out.println("probability: " + output[0][0]);
//                                                    riskScore = readinessScore * output[0][0];
//                                                }
//                                            })
//                                    .addOnFailureListener(
//                                            new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(RiskReadinessActivity.this, "Model failed to run.", Toast.LENGTH_SHORT).show();
//                                                    e.printStackTrace();
//                                                }
//                                            });
//                        } catch (FirebaseMLException e) {
//                            e.printStackTrace();
//                            //continue with mathematical model
//                            double ratio = 1.0;
//                            if (itemsMarked != 0 && checkedItems != 0)
//                                ratio = (double)((checkedItems - itemsMarked)/itemsMarked);
//
//                        }
//                    }
//                });

        //Buttons
        back = findViewById(R.id.back_button);
        home = findViewById(R.id.home_button);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(RiskReadinessActivity.this, DisasterMenuActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //was pointing to disaster menu activity... changed it to this?
                Intent intent1 = new Intent(RiskReadinessActivity.this, HomeScreenActivity.class);
                startActivity(intent1);
            }
        });

    }

    public void getFirebase() {
        Firebase mRefChild1 = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist");
        mRefChild1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("That's");
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        Boolean value = (Boolean) entry.getValue();
                        String item = entry.getKey();
                        //getting which items are checked on the checklist
                        if (chklistOrder.get(item) != null) {
                            int idx = (int) chklistOrder.get(item);
                            if (value) {
                                myInput[0][0][idx] = Float.valueOf(1);
                                itemsMarked++;
                            }
                            else
                                myInput[0][0][idx] = Float.valueOf(0);
                        }
                        //get readiness score
                        if (item.equals("Water") || item.equals("Non-perishable Food") || item.equals("First-aid Kit")) {
                            SIZE_OF_CHECKLIST +=3;
                            System.out.println("rough");
                            if (value){
                                checkedItems += 3;
                            }
                        }
                        else if (item.equals("Portable Charger") || item.equals("Extra Cash") || item.equals("Flashlight") || item.equals("Extra Batteries")) {
                            SIZE_OF_CHECKLIST +=2;
                            System.out.println("buddy");
                            if (value){
                                checkedItems += 2;
                            }
                        }
                        else {
                            SIZE_OF_CHECKLIST++;
                            System.out.println("- Z");
                            if (value){
                                checkedItems ++;
                            }
                        }
                    }
                    Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("readiness_score");
                    readinessScore = (double) checkedItems / (double) SIZE_OF_CHECKLIST;
                    mRefChild.setValue((readinessScore*100) + "%");
                    mRefChild.setValue(String.format("%.2f", (readinessScore*100)) + "%");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
