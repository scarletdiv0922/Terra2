package com.example.terra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class DisasterMenuActivity extends AppCompatActivity {

    String disaster;
    ImageButton information;
    ImageButton scores;
    ImageButton updates;
    FloatingActionButton home;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            disaster = (String) bundle.get("Disaster");
        }

        if (disaster.equals("Earthquakes")) {
            setContentView(R.layout.activity_earthquake_menu);
        }

        else if (disaster.equals("Wildfires")) {
            setContentView(R.layout.activity_wildfire_menu);
        }

        information = findViewById(R.id.info_button);
        scores = findViewById(R.id.scores_button);
        updates = findViewById(R.id.updates_button);
        home = findViewById(R.id.home_button);
        back = findViewById(R.id.back_button);

        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, BDAActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, HomeScreenActivity.class);
                startActivity(intent1);
            }
        });

        scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, RiskReadinessActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, DisasterMapActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisasterMenuActivity.this, HomeScreenActivity.class);
                startActivity(intent1);
            }
        });
    }
}
