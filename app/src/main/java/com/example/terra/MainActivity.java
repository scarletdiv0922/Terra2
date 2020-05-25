package com.example.terra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.ScriptMethodCallback;
import com.alan.alansdk.alanbase.ConnectionState;
import com.alan.alansdk.alanbase.DialogState;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;
import com.alan.alansdk.events.EventRecognised;
import com.alan.alansdk.events.EventText;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private AlanButton alanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        Button signup = findViewById(R.id.sign_up_button);
        signup.setOnClickListener((v) -> {
            //Toast.makeText(this, "Moving to signup activity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), SignupActivity.class);
            startActivity(intent);
        });
        Button login = findViewById(R.id.login_button);
        login.setOnClickListener((v) -> {
            //Toast.makeText(this, "Moving to login activity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        });
    }
}
