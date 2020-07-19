package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.button.AlanButton;
import com.getbase.floatingactionbutton.FloatingActionButton;

public class BDAActivity extends AppCompatActivity {

    private AlanButton alanButton;
    String disaster;
    FloatingActionButton home;
    ImageButton before;
    ImageButton during;
    ImageButton after;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        alanButton = findViewById(R.id.alanbutton);
//        AlanConfig config = AlanConfig.builder()
//                .setProjectId("9852fc5b086a6dc66a51e5f59c5b909a2e956eca572e1d8b807a3e2338fdd0dc/stage")
//                .build();
//        alanButton.initWithConfig(config);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            disaster = (String) bundle.get("Disaster");
        }

        if (disaster.equals("Earthquakes")) {
            setContentView(R.layout.activity_earthquake_bda);
        }

        else if (disaster.equals("Wildfires")) {
            setContentView(R.layout.activity_wildfire_bda);
        }

        home = findViewById(R.id.home_button);
        before = findViewById(R.id.before_button);
        during = findViewById(R.id.during_button);
        after = findViewById(R.id.after_button);
        back = findViewById(R.id.back_button);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(BDAActivity.this, HomeScreenActivity.class);
                startActivity(intent1);
            }
        });

        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(BDAActivity.this, BeforeActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        during.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(BDAActivity.this, DuringActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(BDAActivity.this, AfterActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(BDAActivity.this, DisasterMenuActivity.class);
                intent1.putExtra("Disaster", disaster);
                startActivity(intent1);
            }
        });
    }
}
