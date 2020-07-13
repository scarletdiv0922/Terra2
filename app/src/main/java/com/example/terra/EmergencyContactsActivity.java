package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.button.AlanButton;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class EmergencyContactsActivity extends AppCompatActivity {

    private AlanButton alanButton;
    TextView noContacts;
    TextView instructions;
    ArrayAdapter<String> displayContacts;
    ListView emergContacts;
    FloatingActionButton removeContacts;
    FloatingActionButton addContacts;
    FloatingActionButton safe;
    FloatingActionButton help;
    private Firebase mRef;
    ArrayList<String> contacts2 = new ArrayList<>();
    ArrayList<String> numbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");
        updateContacts();

        noContacts = findViewById(R.id.no_contacts);
        emergContacts = findViewById(R.id.listv);
        instructions = findViewById(R.id.instructions);
        safe = findViewById(R.id.safe);
        help = findViewById(R.id.help);

//        getSupportActionBar().hide();

//        alanButton = findViewById(R.id.alanbutton);
//        AlanConfig config = AlanConfig.builder()
//                .setProjectId("9852fc5b086a6dc66a51e5f59c5b909a2e956eca572e1d8b807a3e2338fdd0dc/stage")
//                .build();
//        alanButton.initWithConfig(config);

        addContacts = findViewById(R.id.add_contact);
        addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyContactsActivity.this, ContactsActivity2.class);
                startActivity(intent);
            }
        });

        removeContacts = findViewById(R.id.remove_contact);
        removeContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyContactsActivity.this, RemoveContactActivity.class);
                startActivity(intent);
            }
        });

        ImageButton back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmergencyContactsActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });

        if (!checkPermission(Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        safe.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (int i = 0; i < numbers.size(); i++) {
                    sendSafeText(numbers.get(i));
                }
                return false;
            }
        });

        help.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (int i = 0; i < numbers.size(); i++) {
                    sendHelpText(numbers.get(i));
                }
                return false;
            }
        });
    }

    public void sendSafeText(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            return;
        }
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null,"I have been hit by a natural disaster, but I'm safe! From, Terra.", null, null);
            Toast.makeText(EmergencyContactsActivity.this, "Your contacts have been informed you are safe.", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(EmergencyContactsActivity.this, "You did not give Terra permission to send text messages.", Toast.LENGTH_LONG).show();
        }
    }

    public void sendHelpText(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            return;
        }
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null,"I have been hit by a natural disaster, and I need help! From, Terra.", null, null);
            Toast.makeText(EmergencyContactsActivity.this, "Your contacts have been informed you need help.", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(EmergencyContactsActivity.this, "You did not give Terra permission to send text messages.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    public void updateContacts() {
        Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_contacts");

        mRefChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : map.entrySet()){
                        //Get user map
                        String contactPhone = (String) entry.getValue();
                        String contactName = entry.getKey();
                        //Get phone field and append to list
                        System.out.println("NAME: " + contactName);
                        System.out.println("PHONE: " + contactPhone);

                        contacts2.add(contactName + "\n" + contactPhone);
                        numbers.add(contactPhone);
                    }

                    if (contacts2.size() != 0) {
                        noContacts.setVisibility(View.INVISIBLE);

                        displayContacts = new ArrayAdapter<String>(
                                EmergencyContactsActivity.this,
                                R.layout.list_item,
                                contacts2
                        );

                        emergContacts.setAdapter(displayContacts);
                    }
                    else {
                        noContacts.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("firebase oops");
            }
        });
    }
}
