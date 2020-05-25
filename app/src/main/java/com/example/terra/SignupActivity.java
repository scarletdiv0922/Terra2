package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        Button signup = findViewById(R.id.sign_up_button);
        signup.setOnClickListener((v) -> {
//            Toast.makeText(this, "Moving to home activity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), HomeScreenActivity.class);
            startActivity(intent);
        });
    }
}
