package com.example.terra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.button.AlanButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        //add intent
        AlanButton alanButton;
        alanButton = findViewById(R.id.alan_button_home_screen);
        AlanConfig config = AlanConfig.builder()
                .setProjectId("9852fc5b086a6dc66a51e5f59c5b909a2e956eca572e1d8b807a3e2338fdd0dc/stage")
                .build();
        alanButton.initWithConfig(config);
        FloatingActionButton menuFAB = findViewById(R.id.menu_button);
        FloatingActionButton emergencyContactsFAB = findViewById(R.id.emergency_contacts_button);
        FloatingActionButton infoFAB = findViewById(R.id.info_button);

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

                Intent intent = new Intent(HomeScreenActivity.this, BDAActivity.class);
                startActivity(intent);

            }
        });
    }
}