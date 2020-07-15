package com.example.terra;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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

public class ChecklistActivity2 extends AppCompatActivity {

    ImageButton back;
    ListView checklist;
    ArrayAdapter<String> displayChecklist;
    private Firebase mRef;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<Boolean> values = new ArrayList<>();
    ArrayList<Boolean> valuesog = new ArrayList<>();
    TextView text;
    public int SIZE_OF_CHECKLIST = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);


        System.out.println("inside oncreate");
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://terra-alan.firebaseio.com/");

        back = findViewById(R.id.backButton);
        checklist = findViewById(R.id.listv);
        text = findViewById(R.id.txtitem);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChecklistActivity2.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });

        getChecklist();


        checklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) checklist.getItemAtPosition(position);
                System.out.println("HERE " + item + " position: " + position);
                if (values.get(items.indexOf(item))) {
                    values.set(items.indexOf(item), false);
                    view.setSelected(false);
                    System.out.println(item+" NOW FALSE");
                }
                else {
                    values.set(items.indexOf(item), true);
                    view.setSelected(true);
                    System.out.println(item+ " NOW TRUE");
                }

//                displayChecklist.getView(0, null, checklist).setBackgroundResource(R.drawable.list_bg);
                View view1;
                view1 = displayChecklist.getView(0, null, checklist);
                view1.setBackgroundResource(R.drawable.list_bg);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        setChecklist();
        items.clear();
        values.clear();
        valuesog.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setChecklist();
        items.clear();
        values.clear();
        valuesog.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onStartChecks();
    }

    public void getChecklist() {
        System.out.println("RUNNING GETCHECKLIST, SIZE " + items.size());
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
                        valuesog.add(value);
                    }
                    values.addAll(valuesog);

                    displayChecklist = new ArrayAdapter<String>(
                            ChecklistActivity2.this,
                            R.layout.list_item,
                            items
                    );

                    checklist.setAdapter(displayChecklist);

                }

                for (int i = 0; i < SIZE_OF_CHECKLIST; i++) {
                    if (values.get(i)) {
                        System.out.println("owo " + checklist.getItemAtPosition(i));
//                        displayChecklist.getView(i, null, checklist).setBackgroundResource(R.drawable.selected_item);
//                        TextView t = (TextView) displayChecklist.getView(3, text, checklist);
//                t.setBackgroundColor(Color.BLUE);

//                System.out.println(displayChecklist.getItemViewType(0));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("firebase canceled oops");
            }
        });
    }

    public void onStartChecks() {
        ListView checklistLocal;
        checklistLocal = findViewById(R.id.listv);
        checklistLocal.setItemChecked(0, true);
        checklistLocal.setItemChecked(2, true);
        if ( checklistLocal.getCount() > 0 ) {
            checklistLocal.getChildAt(0).setSelected(true);
            checklistLocal.getChildAt(1).setSelected(true);
        }
        System.out.println("here onstartchecks:" + checklistLocal.getCount());
    }

    public void setChecklist() {
        System.out.println("SETTING CHECKLIST, SIZE " + values.size());
        if (values.size() > 0) {
            for (int i = 0; i < SIZE_OF_CHECKLIST; i++) {
                Firebase mRefChild = mRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("emergency_checklist")
                        .child(items.get(i));
                if (values.get(i) != valuesog.get(i)) {
                    System.out.println(items.get(i) + " CHANGED");
                    if (valuesog.get(i)) {
                        System.out.println(items.get(i) + " SET TO FALSE");
                        mRefChild.setValue(false);
                    }
                    else {
                        System.out.println(items.get(i) + " SET TO TRUE");
                        mRefChild.setValue(true);
                    }
                }
            }
        }
        System.out.println("FINISHED SETTING, SIZE " + values.size());
    }
}
