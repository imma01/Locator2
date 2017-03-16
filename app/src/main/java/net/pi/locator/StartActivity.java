package net.pi.locator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    public static final String PREFS = "examplePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }


        setContentView(R.layout.activity_start);

        final EditText et = (EditText)findViewById(R.id.deviceId);
        final EditText et1 = (EditText)findViewById(R.id.hostname);
        Button deviceId = (Button)findViewById(R.id.register);

        deviceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceId1 = et.getText().toString();
                String hostName1 = et1.getText().toString();
                if(!et.getText().toString().isEmpty() && !et1.getText().toString().isEmpty()){
                    SharedPreferences examplePrefs = getSharedPreferences(PREFS, 0);
                    SharedPreferences.Editor editor = examplePrefs.edit();
                    editor.putString("hostName", hostName1);
                    editor.putString("deviceID", deviceId1);
                    editor.commit();

                    Toast.makeText(StartActivity.this, "Saved successfully.", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(StartActivity.this, "Please provide details.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
