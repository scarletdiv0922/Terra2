package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPass;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            Toast.makeText(this, "User is already logged in.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), HomeScreenActivity.class);
            startActivity(intent);
        }

        progressDialog = new ProgressDialog(this);

        editTextEmail = findViewById(R.id.email);
        editTextPass = findViewById(R.id.password);
        back = findViewById(R.id.back_button);

        Button login = findViewById(R.id.loginbutton);
        login.setOnClickListener((v) -> userLogin());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String pass = editTextPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            //user needs to type the email
            Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)){
            //user needs to type the password
            Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show();
            return;
        }
        //email and password are entered

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        //user is successfully registered
                        Toast.makeText(this, "User is successfully logged in.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), HomeScreenActivity.class);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(this, "Failed to login. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
