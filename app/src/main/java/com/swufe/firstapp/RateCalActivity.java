package com.swufe.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class RateCalActivity extends AppCompatActivity {
float rate = 0f;
String TAG = "rateCal";
EditText inp2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_cal);
        String title = getIntent().getStringExtra("title");
        getIntent().getFloatExtra("rate",0f);
        Log.i(TAG, "onCreate: title="+title);
        Log.i(TAG, "onCreate: rate="+rate);
        ((TextView)findViewById(R.id.title2)).setText(title);
    }
}
