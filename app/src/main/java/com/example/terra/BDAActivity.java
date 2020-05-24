package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.button.AlanButton;

public class BDAActivity extends AppCompatActivity {
    private AlanButton alanButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bda);
        getSupportActionBar().hide();

        alanButton = findViewById(R.id.alanbutton);
        AlanConfig config = AlanConfig.builder()
                .setProjectId("9852fc5b086a6dc66a51e5f59c5b909a2e956eca572e1d8b807a3e2338fdd0dc/stage")
                .build();
        alanButton.initWithConfig(config);

        Button beforeButton = findViewById(R.id.before_button);
        Button duringButton = findViewById(R.id.during_button);;
        Button afterButton = findViewById(R.id.after_button);;

        beforeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(BDAActivity.this, BeforeActivity.class);
                startActivity(intent);

            }
        });

        duringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(BDAActivity.this, DuringActivity.class);
                startActivity(intent);

            }
        });

        afterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(BDAActivity.this, AfterActivity.class);
                startActivity(intent);

            }
        });

    }
}
