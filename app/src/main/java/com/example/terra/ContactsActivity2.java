package com.example.terra;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.client.Firebase;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ContactsActivity2 extends AppCompatActivity {

    ListView l1;
    ArrayList<String> StoreContacts;
    ArrayList<String> contactNames;
    ArrayList<String> phoneNumbers;
    ArrayAdapter<String> arrayAdapter;
    ImageButton backButton;
    FloatingActionButton done;
    private Firebase mRef;
    private static final String TAG = "ContactsActivity2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        l1 = findViewById(R.id.listv);
        StoreContacts = new ArrayList<>();
        l1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //Connect to Firebase
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        contactNames = new ArrayList<>();
        phoneNumbers = new ArrayList<>();

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity2.this, EmergencyContactsActivity.class);
                startActivity(intent);
            }
        });

        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishSelectingContacts();
            }
        });

        //Set onItemClickListener to set item as checked when user clicks on it
        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                l1.setItemChecked(position, true);
                Log.v(TAG, "User clicked on " + l1.getItemAtPosition(position).toString() + " at position " + position);
            }
        });

        showContacts();

    }

    //Get user permission to access contacts, then populate the contacts ListView
    private void showContacts() {
        //If the permission isn't granted and the SDK version isn't high enough
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            //Then request the user permission to access contacts
            ActivityCompat.requestPermissions(ContactsActivity2.this,
                    new String[] { Manifest.permission.READ_CONTACTS }, 1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method
        }

        //If the user has already accepted the permission
        else {

            //Get the user's contacts
            getContactList();

            //Populate the ListView
            arrayAdapter = new ArrayAdapter<String>(
                    ContactsActivity2.this,
                    R.layout.list_item,
                    StoreContacts);

            l1.setAdapter(arrayAdapter);

        }
    }

    //Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {

            //If the permission has been granted, run showContacts() again and move on to the next step
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            }

            //If the permission hasn't been granted, handle it with an error message
            else {
                Toast.makeText(this, "Without your permission, Terra cannot access your contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Retrieve the user's contact list
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
                        StoreContacts.add(name + "\n" + phoneNo); //Format each contact name and number and store it in StoreContacts
                        contactNames.add(name); //Add each contact name to the ArrayList of contact names
                        phoneNumbers.add(phoneNo); //Add each phone number to the ArrayList of phone numbers
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null){
            cur.close();
        }

    }

    //Once the user is done selecting their emergency contacts from their contact list, update Firebase with their selected contacts
    protected void finishSelectingContacts() {

        SparseBooleanArray sp = l1.getCheckedItemPositions(); //Check which items in the ListView were selected

        for (int i = 0; i < sp.size(); i++) {

            //If the contact was selected, then add it to Firebase
            if (sp.valueAt(i)) {
                Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_contacts")
                        .child(contactNames.get(sp.keyAt(i)));
                mRefChild.setValue(phoneNumbers.get(sp.keyAt(i)));
            }
        }

        //Then, start the intent to go back to the EmergencyContacts activity
        Intent intent = new Intent(ContactsActivity2.this, EmergencyContactsActivity.class);
        startActivity(intent);
    }

}
