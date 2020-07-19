package com.example.terra;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Map;

public class ChecklistActivity2 extends AppCompatActivity {

    ImageButton backButton;
    ListView checklist;
    ArrayAdapter<String> displayChecklist;
    private Firebase mRef;
    ArrayList<String> checklistItems = new ArrayList<>();
    ArrayList<Boolean> updatedValues = new ArrayList<>();
    ArrayList<Boolean> firebaseValues = new ArrayList<>();
    public int SIZE_OF_CHECKLIST = 18;
    private static final String TAG = "ChecklistActivity2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        //Connect activity to Firebase
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        backButton = findViewById(R.id.back_button);
        checklist = findViewById(R.id.listv);

        //Set onClickListener to go back to Home Screen
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChecklistActivity2.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });

        getChecklist();

        //Set onItemClickListener to check when list item is clicked
        checklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) checklist.getItemAtPosition(position); //Store checklist item that was clicked
                Log.v(TAG, "Clicked on " + item + "at position " + position);

                //If the value of the item was true, then update it to false
                if (updatedValues.get(checklistItems.indexOf(item))) {
                    updatedValues.set(checklistItems.indexOf(item), false);
                    view.setSelected(false);
                }

                //If the value of the item was false, then update it to true
                else {
                    updatedValues.set(checklistItems.indexOf(item), true);
                    view.setSelected(true);
                }
            }
        });
    }

    //When the user leaves the activity, empty the lists so that they can be loaded from
    //Firebase when the user comes back.
    @Override
    protected void onPause() {
        super.onPause();
        setChecklist();
        checklistItems.clear();
        updatedValues.clear();
        firebaseValues.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setChecklist();
        checklistItems.clear();
        updatedValues.clear();
        firebaseValues.clear();
    }

    //Load the items in the checklist from Firebase when the user enters the activity
    public void getChecklist() {
        Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist"); //Get the child element
        mRefChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : map.entrySet()){

                        //Get the emergency checklist item and its value
                        Boolean value = (Boolean) entry.getValue();
                        String item = entry.getKey();

                        //Add the item and value to their respective ArrayLists
                        checklistItems.add(item);
                        firebaseValues.add(value);
                    }

                    //Make a copy of the original Firebase values to be updated as the user selects items in the activity
                    updatedValues.addAll(firebaseValues);

                    //Inflate the checklist ListView with the items retrieved from Firebase
                    displayChecklist = new ArrayAdapter<String>(
                            ChecklistActivity2.this,
                            R.layout.list_item,
                            checklistItems
                    );

                    checklist.setAdapter(displayChecklist);
                }

                //If the item is already checked as true, then mark it as checked when the user enters the activity
                for (int i = 0; i < SIZE_OF_CHECKLIST; i++) {
                    if (firebaseValues.get(i)) {
                        checklist.setItemChecked(i,true);
                    }
                }

             }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "Error connecting to Firebase");
            }
        });
    }

    //When the user leaves the activity, take the updated values and update Firebase with them
    public void setChecklist() {
        if (updatedValues.size() > 0) {
            for (int i = 0; i < SIZE_OF_CHECKLIST; i++) {
                Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist")
                        .child(checklistItems.get(i)); //Get the Firebase children under emergency_checklist
                if (updatedValues.get(i) != firebaseValues.get(i)) { //If the new value isn't equal to the original value in Firebase, then update the Firebase value to match
                    if (firebaseValues.get(i)) {
                        Log.v(TAG, checklistItems.get(i) + " is now set to false");
                        mRefChild.setValue(false);
                    }
                    else {
                        Log.v(TAG, checklistItems.get(i) + " is now set to true");
                        mRefChild.setValue(true);
                    }
                }
            }
        }
    }
}
