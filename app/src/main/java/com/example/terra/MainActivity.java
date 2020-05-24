package com.example.terra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.alanbase.ConnectionState;
import com.alan.alansdk.alanbase.DialogState;
import com.alan.alansdk.button.AlanButton;
import com.alan.alansdk.events.EventCommand;
import com.alan.alansdk.events.EventRecognised;
import com.alan.alansdk.events.EventText;

public class MainActivity extends AppCompatActivity {
    private AlanButton alanButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        alanButton = findViewById(R.id.alanbutton);
        AlanConfig config = AlanConfig.builder()
                .setProjectId("9852fc5b086a6dc66a51e5f59c5b909a2e956eca572e1d8b807a3e2338fdd0dc/stage")
                .build();
        alanButton.initWithConfig(config);
        Button signup = findViewById(R.id.sign_up_button);
        signup.setOnClickListener((v) -> {
            //Toast.makeText(this, "Moving to signup activity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), SignupActivity.class);
            startActivity(intent);
        });
        Button login = findViewById(R.id.login_button);
        login.setOnClickListener((v) -> {
            //Toast.makeText(this, "Moving to login activity", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        });
    }
    /**
     * Invokes when connection state with backend changed
     * @param connectState
     */
//    public void onConnectStateChanged(@NonNull ConnectionState connectState) {
//
//    }

    /**
     * Invokes on button interaction state changed
     * @param dialogState
     */
//    public void onDialogStateChanged(@NonNull DialogState dialogState) {
//
//    }

    /**
     * Invokes when user speech is recognized on backend
     * @param eventRecognised
     */
    public void onRecognizedEvent(EventRecognised eventRecognised) {
        Toast.makeText(this, "Recognized that the user is talking", Toast.LENGTH_SHORT).show();
    }

    /**
     * Invokes when some command from the script side is received
     * @param eventCommand
     */
//    public void onCommandReceived(EventCommand eventCommand) {
//
//    }

    /**
     * Invokes on answer from the script
     * @param eventText
     */
//    public void onTextEvent(EventText eventText) {
//
//    }

    /**
     * Invokes on any other event
     * @param event
     * @param payload
     */
//    public void onEvent(String event, String payload) {
//
//    }

    /**
     * Invokes on Alan errors
     */
    public void onError(String error) {
        Toast.makeText(this, "Alan AI error", Toast.LENGTH_SHORT).show();
    }
}
