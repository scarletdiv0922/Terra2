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
