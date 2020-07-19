package com.example.terra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.button.AlanButton;
import com.getbase.floatingactionbutton.FloatingActionButton;

public class AfterActivity extends AppCompatActivity {

    ImageButton backButton;
    FloatingActionButton home;
    String disaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after);

//        AlanButton alanButton;
//        alanButton = findViewById(R.id.alanbutton);
//        AlanConfig config = AlanConfig.builder()
//                .setProjectId("9852fc5b086a6dc66a51e5f59c5b909a2e956eca572e1d8b807a3e2338fdd0dc/stage")
//                .build();
//        alanButton.initWithConfig(config);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        if (bundle != null) {
            disaster = (String) bundle.get("Disaster");
        }

        backButton = findViewById(R.id.back_button);
        home = findViewById(R.id.home_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterActivity.this, BDAActivity.class);
                intent.putExtra("Disaster", disaster);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterActivity.this, HomeScreenActivity.class);
                intent.putExtra("Disaster", disaster);
                startActivity(intent);
            }
        });
    }
}
