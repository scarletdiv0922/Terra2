package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.button.AlanButton;

public class BeforeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before);
//        getSupportActionBar().hide();

        AlanButton alanButton;
        alanButton = findViewById(R.id.alanbutton);
        AlanConfig config = AlanConfig.builder()
                .setProjectId("9852fc5b086a6dc66a51e5f59c5b909a2e956eca572e1d8b807a3e2338fdd0dc/stage")
                .build();
        alanButton.initWithConfig(config);
    }
}
