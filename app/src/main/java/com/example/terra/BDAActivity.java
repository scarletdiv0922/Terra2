package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BDAActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bda);

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
