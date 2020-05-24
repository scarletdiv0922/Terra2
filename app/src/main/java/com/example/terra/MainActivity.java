package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.button.AlanButton;

public class MainActivity extends AppCompatActivity {
    private AlanButton alanButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alanButton = findViewById(R.id.alan_button);
        AlanConfig config = AlanConfig.builder()
                .setProjectId("")
                .build();
        alanButton.initWithConfig(config);
    }
}
