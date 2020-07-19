package com.example.terra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class RiskReadinessActivity extends AppCompatActivity {

    private Firebase mRef;
    public int checkedItems;
    public int total;
    public int SIZE_OF_CHECKLIST = 16;
    public int readinessScore;
    ImageButton back;
    FloatingActionButton home;
    String disaster;
    private static final String TAG = "RiskReadiness";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_readiness_scores);

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            disaster = (String) bundle.get("Disaster");
        }

        getFirebase();

        Log.v(TAG, "Checked items: " + checkedItems);
        readinessScore = checkedItems / SIZE_OF_CHECKLIST;

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
            public void onClick(View v) {
                Intent intent1 = new Intent(RiskReadinessActivity.this, DisasterMenuActivity.class);
                startActivity(intent1);
            }
        });

    }

    public void getFirebase() {

        Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist");
        mRefChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        Boolean value = (Boolean) entry.getValue();
                        String item = entry.getKey();

                        if (value) {
                            if (item.equals("Water") || item.equals("Non-perishable Food") || item.equals("First-aid Kit")) {
                                checkedItems += 3;
                            }
                            else if (item.equals("Portable Charger") || item.equals("Extra Cash") || item.equals("Flashlight") || item.equals("Extra Batteries")) {
                                checkedItems += 2;
                            }
                            else {
                                checkedItems++;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
