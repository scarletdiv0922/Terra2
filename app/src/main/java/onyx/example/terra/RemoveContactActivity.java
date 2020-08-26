package onyx.example.terra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class RemoveContactActivity extends AppCompatActivity {

    ListView emergContacts;
    FloatingActionButton done;
    ImageButton back;
    TextView noContacts;
    TextView instructions;
    private Firebase mRef;
    ArrayList<String> contacts2 = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    private ArrayAdapter<String> displayContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_contacts);

        emergContacts = findViewById(R.id.listv);
        done = findViewById(R.id.done);
        back = findViewById(R.id.back_button);
        noContacts = findViewById(R.id.no_contacts);
        instructions = findViewById(R.id.instructions);

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
                if (cmd.contains("before")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(RemoveContactActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")) {
                        Intent intent = new Intent(RemoveContactActivity.this, BeforeActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("during")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(RemoveContactActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") || cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(RemoveContactActivity.this, DuringActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("after")){
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")){
                        Intent intent = new Intent(RemoveContactActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(RemoveContactActivity.this, AfterActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("nearMe")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("earthquake") || cmd.substring(i, j).equals("earthquakes")) {
                        Intent intent = new Intent(RemoveContactActivity.this, EarthquakeMapActivity.class);
                        intent.putExtra("Disaster", "Earthquakes");
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("wildfire") ||cmd.substring(i, j).equals("wildfires")){
                        Intent intent = new Intent(RemoveContactActivity.this, EarthquakeMapActivity.class);
                        intent.putExtra("Disaster", "Wildfires");
                        startActivity(intent);
                    }
                }
                else if (cmd.contains("show")) {
                    Intent intent = new Intent(RemoveContactActivity.this, EmergencyContactsActivity.class);
                    startActivity(intent);
                }
                else if (cmd.contains("checklist")) {
                    Intent intent = new Intent(RemoveContactActivity.this, ChecklistActivity2.class);
                    startActivity(intent);
                }
                else if (cmd.contains("near")) {
                    Intent intent = new Intent(RemoveContactActivity.this, NearbyFacilitiesActivity.class);
                    startActivity(intent);
                }
                else if (cmd.contains("navigate")) {
                    int i = cmd.indexOf("value")+8;
                    int j = cmd.indexOf("\"}");
                    if (cmd.substring(i, j).equals("home")){
                        Intent intent = new Intent(RemoveContactActivity.this, HomeScreenActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency contacts") || cmd.substring(i, j).equals("contacts")) {
                        Intent intent = new Intent(RemoveContactActivity.this, EmergencyContactsActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("emergency checklist") || cmd.substring(i, j).equals("checklist")) {
                        Intent intent = new Intent(RemoveContactActivity.this, ChecklistActivity2.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("nearby facilities")){
                        Intent intent = new Intent(RemoveContactActivity.this, NearbyFacilitiesActivity.class);
                        startActivity(intent);
                    }
                    else if (cmd.substring(i, j).equals("disaster warnings") || cmd.substring(i, j).equals("disaster updates") || cmd.substring(i, j).equals("map")) {
                        Intent intent = new Intent(RemoveContactActivity.this, EarthquakeMapActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    alan_button.playText("Something went wrong. Please try again.");
                }
            }
        };

        alan_button.registerCallback(myCallback);

        updateContacts();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemoveContactActivity.this, EmergencyContactsActivity.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemoveContactActivity.this, EmergencyContactsActivity.class);
                startActivity(intent);
            }
        });

        emergContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("CONTACT: " + names.get(position));
                mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_contacts")
                        .child(names.get(position)).removeValue();
                contacts2.clear();
                names.clear();
                displayContacts.clear();
                System.out.println("CLEARED");
            }
        });
    }

    public void updateContacts() {
        Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_contacts");

        mRefChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : map.entrySet()){
                        //Get user map
                        String contactPhone = (String) entry.getValue();
                        String contactName = entry.getKey();
                        //Get phone field and append to list
                        System.out.println("NAME2: " + contactName);
                        System.out.println("PHONE2: " + contactPhone);

                        contacts2.add(contactName + "\n" + contactPhone);
                        names.add(contactName);
                    }

                    if (contacts2.size() != 0) {
                        noContacts.setVisibility(View.INVISIBLE);
                        instructions.setVisibility(View.VISIBLE);

                        displayContacts = new ArrayAdapter<String>(
                                RemoveContactActivity.this,
                                R.layout.list_item,
                                contacts2
                        );

                        emergContacts.setAdapter(displayContacts);
                    }
                    else {
                        noContacts.setVisibility(View.VISIBLE);
                        instructions.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("firebase oops");
            }
        });
    }
}
