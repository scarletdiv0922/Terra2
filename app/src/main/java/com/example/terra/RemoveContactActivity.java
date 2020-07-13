package com.example.terra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Map;

public class RemoveContactActivity extends AppCompatActivity {

    ListView emergContacts;
    FloatingActionButton done;
    ImageButton back;
    TextView noContacts;
    TextView instructions;
    private Firebase mRef;
    ArrayList<String> contacts2 = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    private ArrayAdapter<String> displayContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_contacts);

        emergContacts = findViewById(R.id.listv);
        done = findViewById(R.id.done);
        back = findViewById(R.id.backButton);
        noContacts = findViewById(R.id.no_contacts);
        instructions = findViewById(R.id.instructions);

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        updateContacts();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemoveContactActivity.this, EmergencyContactsActivity.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemoveContactActivity.this, EmergencyContactsActivity.class);
                startActivity(intent);
            }
        });

        emergContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("CONTACT: " + names.get(position));
                mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_contacts")
                        .child(names.get(position)).removeValue();
                contacts2.clear();
                names.clear();
                displayContacts.clear();
                System.out.println("CLEARED");
            }
        });
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
                        System.out.println("NAME2: " + contactName);
                        System.out.println("PHONE2: " + contactPhone);

                        contacts2.add(contactName + "\n" + contactPhone);
                        names.add(contactName);
                    }

                    if (contacts2.size() != 0) {
                        noContacts.setVisibility(View.INVISIBLE);
                        instructions.setVisibility(View.VISIBLE);

                        displayContacts = new ArrayAdapter<String>(
                                RemoveContactActivity.this,
                                R.layout.list_item,
                                contacts2
                        );

                        emergContacts.setAdapter(displayContacts);
                    }
                    else {
                        noContacts.setVisibility(View.VISIBLE);
                        instructions.setVisibility(View.INVISIBLE);
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
