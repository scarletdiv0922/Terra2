package com.example.terra;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Map;

public class ChecklistActivity extends AppCompatActivity {

    ImageButton back;
    ListView checklist;
    ArrayAdapter<String> displayChecklist;
    private Firebase mRef;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<Boolean> values = new ArrayList<>();
//    RecyclerView recyclerView;
//    RecyclerView.Adapter mAdapter;
//    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

//        for (int i = 0; i < values.size(); i++) {
////            if(values.get(i)) {
////                System.out.println("SELECTED " + items.get(i));
////                checklist.setSelection(i);
////            }
////        }

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        back = findViewById(R.id.back_button);
        checklist = findViewById(R.id.listv);

//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        mAdapter = new MyAdapter(items);
//        recyclerView.setAdapter(mAdapter);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChecklistActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });

        getChecklist();
//        recyclerView();


        checklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) checklist.getItemAtPosition(position);
                System.out.println("HERE " + item + " position: " + position);
                Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist")
                        .child(item);
                if (values.get(items.indexOf(item))) {
                    mRefChild.setValue(false);
                }

                else {
                    mRefChild.setValue(true);
                }
                items.clear();
                values.clear();
                displayChecklist.clear();
            }
        });


    }

    public void getChecklist() {

        Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist");
        mRefChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("data changed!");
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : map.entrySet()){
                        //Get user map
                        Boolean value = (Boolean) entry.getValue();
                        String item = entry.getKey();

                        items.add(item);
                        values.add(value);
                        System.out.println("item:" + item + "value:" + value);
                    }

                    displayChecklist = new ArrayAdapter<String>(
                                ChecklistActivity.this,
                                R.layout.list_item,
                                items
                        );

                    checklist.setAdapter(displayChecklist);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("firebase canceled oops");
            }
        });
    }

//    public void recyclerView() {
//        System.out.println("RECYCLE");
//        RecyclerViewAdapter adapter = new RecyclerViewAdapter(items, this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }
}
