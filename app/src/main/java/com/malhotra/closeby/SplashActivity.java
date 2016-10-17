package com.malhotra.closeby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SPLASH-ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sp = getSharedPreferences("new_user", MODE_PRIVATE);

        if(sp.contains("used")) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(4000);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    } finally {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };
            thread.start();
        }
        else{
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(4000);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    } finally {
                        Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };
            thread.start();
        }

    }
}
