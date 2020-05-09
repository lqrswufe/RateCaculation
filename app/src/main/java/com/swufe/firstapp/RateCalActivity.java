package com.swufe.firstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        ((TextView)findViewById(R.id.title2)).setText(title);
        Uri uri = Uri.parse("http://www.baidu.com");
        Intent it = new Intent();
        it.setAction(Intent.ACTION_VIEW);
        it.setData(uri);
        startActivity(it);
    }
}
