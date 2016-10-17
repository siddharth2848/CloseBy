package com.malhotra.closeby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "TUTORIAL-ACTIVITY";

    Button go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        SharedPreferences sp = getSharedPreferences("new_user" , MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("used" , "1");
        ed.commit();

        go = (Button) findViewById(R.id.btn_go);
        go.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
        startActivity(intent);
        finish();
    }
}
