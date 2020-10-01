package onyx.example.terra;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
//import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
//import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
//import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
//import com.google.firebase.ml.custom.FirebaseModelInputs;
//import com.google.firebase.ml.custom.FirebaseModelInterpreter;
//import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HazardPrepActivity extends AppCompatActivity {

    //initialize variables
    private Firebase mRef;
    int checkedItems; //stores weighted amount of how many items are checked
    public int total;
    public int SIZE_OF_CHECKLIST;
    public double preparationIndex;
    ImageButton back;
    FloatingActionButton home;
    String disaster = " ";
    TextView hazardText;
    TextView preparationText;

    private static final String TAG = "HazardPrepActivity";
    Float[][][] myInput = new Float[1][1][18]; //contains whether users have a certain item or not
    public double hazardIndex;

    public double hazardFromLoc = 0.0;
    String county = "Alameda";
    String parsedCountyName = "";
    Button doneButton;

    HashMap chklistOrder = new HashMap<String, Integer>();
    public int itemsMarked = 0; // the number of items marked without weight

    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    ImageButton hazardInfoButton;
    ImageButton preparationInfoButton;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazard_prep_indices);

        //Connect to Firebase
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        //Retrieve the relevant views from the xml layout
        hazardText = findViewById(R.id.hazard_index);
        preparationText = findViewById(R.id.prep_index);
        hazardInfoButton = findViewById(R.id.hazard_info);
        preparationInfoButton = findViewById(R.id.prep_info);

        //Show the popup window with the user's hazard index
        hazardInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                startActivity(new Intent(HazardPrepActivity.this, PopRisk.class));
            }
        });

        //Show the popup window with the user's preparation index
        preparationInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                startActivity(new Intent(HazardPrepActivity.this, PopReadiness.class));
            }
        });

        JSONObject commandJson = null;

        try {
            commandJson = new JSONObject("{\"command\":\"navigate\", \"screen\": \"settings\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        //Define the Alan Callback
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
                        Intent intent = new Intent(HazardPrepActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")) {
                        Intent intent = new Intent(HazardPrepActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("during")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(HazardPrepActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(HazardPrepActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("after")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(HazardPrepActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(HazardPrepActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("nearMe")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(HazardPrepActivity.this, EarthquakeMapActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(HazardPrepActivity.this, EarthquakeMapActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("show")) {
                    Intent intent = new Intent(HazardPrepActivity.this, EmergencyContactsActivity.class);
                    startActivity(intent);
                }
                else if (cmd.contains("checklist")) {
                    Intent intent = new Intent(HazardPrepActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("near")) {
                    Intent intent = new Intent(HazardPrepActivity.this, NearbyFacilitiesActivity.class);
                    startActivity(intent);
                }
                else if (cmd.contains("navigate")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("home")){
                        Intent intent = new Intent(HazardPrepActivity.this, HomeScreenActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency contacts") || cmd.substring(i, j).equals("contacts")){
                        Intent intent = new Intent(HazardPrepActivity.this, EmergencyContactsActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency checklist") || cmd.substring(i, j).equals("checklist")) {
                        Intent intent = new Intent(HazardPrepActivity.this, ChecklistActivity2.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("nearby facilities")){
                        Intent intent = new Intent(HazardPrepActivity.this, NearbyFacilitiesActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("disaster warnings") || cmd.substring(i, j).equals("disaster updates") || cmd.substring(i, j).equals("map")) {
                        Intent intent = new Intent(HazardPrepActivity.this, EarthquakeMapActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };

        alan_button.registerCallback(myCallback);

        //check which disaster the user wants the hazard index for
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            disaster = (String) bundle.get("Disaster");
        }

        for (int i = 0; i < 18; i++)
            myInput[0][0][i] = Float.valueOf(0);

        getFirebase();

        //current order of the checklist
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

        //hazard of natural disaster based on location
        doneButton = findViewById(R.id.show_hazard_button);
        System.out.println("The disaster is " + disaster);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextCounty = findViewById(R.id.editTextCounty);
                county = editTextCounty.getText().toString();
                String[] splitCounty = county.split(" ");
                for (int i = 0; i < splitCounty.length; i++) {
                    if (splitCounty[i].equalsIgnoreCase("county")) {
                        parsedCountyName = parsedCountyName.substring(0, parsedCountyName.length()-1);
                        break;
                    }
                    else {
                        parsedCountyName += splitCounty[i];
                        if (i != splitCounty.length - 1)
                            parsedCountyName += " ";
                    }
                }
                InputStream inputStream; //default to earthquakes
                if (disaster.equalsIgnoreCase("Wildfires"))
                    inputStream = getResources().openRawResource(R.raw.firedata);
                else {
                    disaster = "Earthquakes";
                    inputStream = getResources().openRawResource(R.raw.eqdata);
                }
                CSVFile csvFile = new CSVFile(inputStream);
                ArrayList<ArrayList<String>> hazardList = csvFile.read();
                for(ArrayList<String> scoreData:hazardList ) {
                    for (String s:scoreData) {
                        if (s.equalsIgnoreCase(parsedCountyName)) {
                            String locHazard = scoreData.get(2);
                            if (locHazard.equalsIgnoreCase("High"))
                                hazardFromLoc = 2.0;
                            else if (locHazard.equalsIgnoreCase("Medium"))
                                hazardFromLoc = 1.0;
                            else
                                hazardFromLoc = 0.5;
                            Log.v(TAG, "At " + locHazard + " risk");
                        }
                    }
                }
                //hazard score mathematical model
                double ratio = 0.5;
                if (SIZE_OF_CHECKLIST != 0 && checkedItems != 0)
                    ratio = (1 - (preparationIndex * 0.9)) * 0.5;
                System.out.println(ratio + " " + hazardFromLoc);
                hazardIndex = ratio * hazardFromLoc;
                if (hazardIndex > 0.75)
                    hazardText.setTextColor(Color.rgb(181, 9, 0)); //RED FOR HIGH HAZARD
                else if (hazardIndex > 0.5)
                    hazardText.setTextColor(Color.rgb(237, 174, 0)); //YELLOW FOR MEDIUM HAZARD
                else
                    hazardText.setTextColor(Color.rgb(21, 176, 0)); //GREEN FOR LOW HAZARD
                DecimalFormat df = new DecimalFormat("#.###");
                Log.v(TAG, "hazard index: " + hazardIndex);
                hazardText.setText("Your Hazard Index: " + df.format(hazardIndex));
                parsedCountyName = "";
                //update the hazard index value in firebase
                Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("risk_score").child(disaster);
                mRefChild.setValue(df.format(hazardIndex));
            }
        });

        //Buttons
        back = findViewById(R.id.back_button);
        home = findViewById(R.id.home_button);

        //go back to the disaster menu activity
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HazardPrepActivity.this, DisasterMenuActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        //go to the home screen
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(HazardPrepActivity.this, HomeScreenActivity.class);
                startActivity(intent1);
            }
        });

    }

    //get the firebase values
    public void getFirebase() {
        Firebase mRefChild1 = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist");
        mRefChild1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                        //get preparation index
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
                    preparationIndex = (double) checkedItems / (double) SIZE_OF_CHECKLIST;
                    Log.v(TAG, "preparation index: " + preparationIndex);
                    String formattedPreparationIndex = String.format("%.2f", (preparationIndex*100)) + "%";
                    mRefChild.setValue(formattedPreparationIndex);
                    preparationText.setText("Your Preparation Index " + formattedPreparationIndex);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "There is a firebase error");
            }
        });
    }
}
