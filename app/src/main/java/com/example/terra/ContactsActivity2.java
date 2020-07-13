package com.example.terra;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.client.Firebase;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ContactsActivity2 extends AppCompatActivity {

    ListView l1;
    ArrayList<String> StoreContacts;
    ArrayList<String> names;
    ArrayList<String> numbers;
    ArrayAdapter<String> arrayAdapter;
    Cursor cursor;
    ArrayList<String> contacts = new ArrayList<String>();
    private Firebase mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        l1 = findViewById(R.id.listv);
        StoreContacts = new ArrayList<String>();
        l1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        names = new ArrayList<>();
        numbers = new ArrayList<>();

        ImageButton back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity2.this, EmergencyContactsActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishSelectingContacts();
            }
        });

        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                l1.setItemChecked(position, true);
                System.out.println("CHECKED " + l1.getItemAtPosition(position).toString() +" AT " + position);
            }
        });


        showContacts();


    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ContactsActivity2.this,
                    new String[] { Manifest.permission.READ_CONTACTS }, 1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method
        }
        else {
            // Android version is lesser than 6.0 or the permission is already granted.
            getContactList();

            arrayAdapter = new ArrayAdapter<String>(
                    ContactsActivity2.this,
                    R.layout.list_item,
                    StoreContacts);

            l1.setAdapter(arrayAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Without your permission, app cannot access the Contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getContactList() {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        StoreContacts.add(name + "\n" + phoneNo);
                        names.add(name);
                        numbers.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null){
            cur.close();
        }

//        loadContacts();
    }

    protected void finishSelectingContacts() {

        ArrayList<String> selectedContacts = new ArrayList<String>();

        SparseBooleanArray sp = l1.getCheckedItemPositions();

        for (int i = 0; i < sp.size(); i++) {
            if (sp.valueAt(i)) {
                selectedContacts.add(StoreContacts.get(sp.keyAt(i)));

                Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_contacts")
                        .child(names.get(sp.keyAt(i)));
                mRefChild.setValue(numbers.get(sp.keyAt(i)));
            }
        }

        Intent intent = new Intent(ContactsActivity2.this, EmergencyContactsActivity.class);
        startActivity(intent);
    }

}
