package onyx.example.terra;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import onyx.example.terra.R;
import com.getbase.floatingactionbutton.FloatingActionButton;

public class ContactsActivity extends AppCompatActivity {

    ListView l1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_contacts);
//        getSupportActionBar().hide();

        l1 = findViewById(R.id.listv);

        ImageButton back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity.this, EmergencyContactsActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity.this, EmergencyContactsActivity.class);
                startActivity(intent);
            }
        });

        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                System.out.println(l1.getItemAtPosition(position));
//                l1.getChildAt(position).setBackgroundColor(Color.BLUE);
                l1.setItemChecked(position, true);
            }
        });

        get();

    }

    public void get() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        startManagingCursor(cursor);

        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID};

        System.out.println("FROM: " + from);

        int[] to = {android.R.id.text1, android.R.id.text2};

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
        l1.setAdapter(simpleCursorAdapter);
//        l1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
}
