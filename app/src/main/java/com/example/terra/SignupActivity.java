package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPass;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Firebase mRef;
    ArrayList<String> items = new ArrayList<>();
    ImageButton back;
    ArrayList<String> disastersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
//        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        progressDialog = new ProgressDialog(this);

        editTextEmail = findViewById(R.id.email);
        editTextPass = findViewById(R.id.password);

        items.add("Water");
        items.add("Non-perishable Food");
        items.add("Battery-powered Radio");
        items.add("Flashlight");
        items.add("First-aid Kit");
        items.add("Extra Batteries");
        items.add("Whistle");
        items.add("Dust Masks");
        items.add("Moist Towelettes");
        items.add("Garbage Bags");
        items.add("Wrench or Pliers");
        items.add("Manual Can Opener");
        items.add("Local Maps");
        items.add("Hand Sanitizer");
        items.add("Prescription Medications");
        items.add("Matchsticks");
        items.add("Extra Cash");
        items.add("Portable Charger");
        //REMINDER: If you add any extra items to this list, update the SIZE_OF_CHECKLIST
        //class constant in ChecklistActivity2

        disastersList.add("Earthquakes");
        disastersList.add("Wildfires");

        Button signup = findViewById(R.id.sign_up_button);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
                startActivity(new Intent(SignupActivity.this, PrivacyAgreementActivity.class));
            }
        });

        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void registerUser(){
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

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        //user is successfully registered
                        Toast.makeText(this, "User is successfully registered.", Toast.LENGTH_LONG).show();
                        addToFirebase();

                        Intent intent = new Intent(getBaseContext(), DisastersActivity.class);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(this, "Failed to register. Please try again.", Toast.LENGTH_LONG).show();
                });
    }

    public void addToFirebase() {
        for (int i = 0; i < items.size(); i++) {
            Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist")
                    .child(items.get(i));
            mRefChild.setValue(false);
        }

        Firebase mRefChild1 = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("readiness_score");
        mRefChild1.setValue("0 %");

        for (int i = 0; i < disastersList.size(); i++) {
            Firebase mRefChild2 = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("risk_score")
                    .child(disastersList.get(i));
            mRefChild2.setValue("undefined");
        }
    }
}
