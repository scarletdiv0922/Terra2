package com.example.terra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        //add intent

        Button menuFAB = findViewById(R.id.menu_button);
        Button emergencyContactsFAB = findViewById(R.id.emergency_contacts_button);
        Button infoFAB = findViewById(R.id.info_button);

        emergencyContactsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(HomeScreenActivity.this, EmergencyContactsActivity.class);

            }
        });

        infoFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(HomeScreenActivity.this, BDAActivity.class);

            }
        });
    }
}