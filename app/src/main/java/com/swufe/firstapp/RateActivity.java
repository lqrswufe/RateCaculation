package com.swufe.firstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RateActivity extends AppCompatActivity {
    EditText rmb;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.showOut);
    }
    public void onClick(View btn){
        String str = rmb.getText().toString();
        float r =0;
        if(str.length()>0){
        r = Float.parseFloat(str);
        }else{
            //提示用户输入信息
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
        }
        float val =0;
        if(btn.getId()==R.id.btn_dollar){
            val = r* (1/6.7f);//强转直接加f
        }else if(btn.getId()==R.id.btn_euro){
            val = r*(1/11f);//强转直接加f
        }else {
            val = r* 500;//强转直接加f
        }
            show.setText(val + "");
    }
    public void openOne(View btn){//超神奇！！
        //打开一个页面Activity
        Log.i("open","openOne: ");
        Intent hello = new  Intent(this,SencondActivity.class);//打开积分程序窗口
        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jd.com"));
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:87092173"));
        startActivity(intent);
    }
}
