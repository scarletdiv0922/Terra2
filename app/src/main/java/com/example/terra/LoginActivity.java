package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = findViewById(R.id.loginbutton);
        login.setOnClickListener((v) -> {
            Toast.makeText(this, "Moving to home activity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), HomeScreenActivity.class);
            startActivity(intent);
        });
    }
}
