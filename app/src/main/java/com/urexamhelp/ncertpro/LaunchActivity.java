package com.urexamhelp.ncertpro;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.urexamhelp.ncertpro.R;

import io.fabric.sdk.android.Fabric;


public class LaunchActivity extends AppCompatActivity {
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Log.i("SHAGUN", "Starting Activity");
        setContentView(R.layout.activity_launch);
        button = (Button) findViewById(R.id.launch_button);


        //Removing the status bar

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().setStatusBarColor(Color.TRANSPARENT);


        SharedPreferences profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
        final String class_id = profile_data.getString(Constants.class_id,"");
        Log.i(Constants.TAG,class_id);
        if(!class_id.equals(""))
        {
            Toast.makeText(LaunchActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
        }

        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                // Start Grade Activity.class

                if (Constants.isNetworkAvailable(LaunchActivity.this)) {

                    Log.i("SHAGUN", "Button Clicked");
                    if (class_id.equals("")) {
                        Intent intent = new Intent(LaunchActivity.this, OTPActivity.class);
                        LaunchActivity.this.startActivity(intent);
                        finish();
                    } else {

                        Intent intent = new Intent(LaunchActivity.this, SubjectActivity.class);
                        intent.putExtra("class_id", class_id);
                        LaunchActivity.this.startActivity(intent);
                        finish();
                    }
                }

            }
        });
    }
}
