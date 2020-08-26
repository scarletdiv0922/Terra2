package onyx.example.terra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import onyx.example.terra.R;

public class PrivacyAgreementActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_privacy_agreement);

        Button agreeButton = findViewById(R.id.agree_button);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrivacyAgreementActivity.this, DisastersActivity.class));
            }
        });

        TextView privacyText = findViewById(R.id.privacy_agreement);
        privacyText.setMovementMethod(new ScrollingMovementMethod());
    }

}
