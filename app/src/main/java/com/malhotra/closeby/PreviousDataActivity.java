package com.malhotra.closeby;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Malhotra G on 12/1/2016.
 */

public class PreviousDataActivity extends AppCompatActivity implements View.OnClickListener {

    Button bt1, bt2;
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previousdata);

        bt1 = (Button) findViewById(R.id.prev_saved);
        bt2 = (Button) findViewById(R.id.new_place);

        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.prev_saved:
                intent = new Intent(getApplicationContext(), MapFragment.class);
                startActivity(intent);
                finish();
                break;
            case R.id.new_place:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
        }
    }
}
