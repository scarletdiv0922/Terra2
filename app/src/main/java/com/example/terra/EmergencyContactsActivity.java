package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.button.AlanButton;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EmergencyContactsActivity extends AppCompatActivity {

    private AlanButton alanButton;
    ArrayList<String> contacts = new ArrayList<String>();
    TextView noContacts;
    ArrayAdapter<String> displayContacts;
    ListView emergContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);
//        getSupportActionBar().hide();

//        alanButton = findViewById(R.id.alanbutton);
//        AlanConfig config = AlanConfig.builder()
//                .setProjectId("9852fc5b086a6dc66a51e5f59c5b909a2e956eca572e1d8b807a3e2338fdd0dc/stage")
//                .build();
//        alanButton.initWithConfig(config);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            contacts = bundle.getStringArrayList("EmergencyContacts");
            for (int i = 0; i < contacts.size(); i++) {
                System.out.println("Contact #" + i + ": " + contacts.get(i));
            }
        }

        noContacts = findViewById(R.id.no_contacts);
        emergContacts = findViewById(R.id.listv);
        if (contacts.size() != 0) {
            noContacts.setVisibility(View.INVISIBLE);

            displayContacts = new ArrayAdapter<String>(
                    EmergencyContactsActivity.this,
                    R.layout.list_item,
                    contacts
            );

            emergContacts.setAdapter(displayContacts);
        }
        else {
            noContacts.setVisibility(View.VISIBLE);
        }


        FloatingActionButton addContacts = findViewById(R.id.add_contact);
        addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsPage();
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
    }

    public void contactsPage() {
        Intent intent = new Intent(EmergencyContactsActivity.this, ContactsActivity2.class);
        intent.putStringArrayListExtra("Emergency Contacts", contacts);
        startActivity(intent);
    }
}
