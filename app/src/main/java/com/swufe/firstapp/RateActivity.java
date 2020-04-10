package com.swufe.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RateActivity extends AppCompatActivity {
    EditText rmb;
    TextView show;
    private final String TAG ="Rate";
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.showOut);
    }

    public void onClick(View btn) {
        Log.i(TAG,"onClick: ");
        String str = rmb.getText().toString();
        Log.i(TAG,"onClick:get str= "+str);
        float r = 0;
        if (str.length() > 0) {
            r = Float.parseFloat(str);
        } else {
            //提示用户输入信息
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }

        if (btn.getId() == R.id.btn_dollar) {
            show.setText(String.format("%.2f",r*dollarRate));
           // val = r * (1 / 6.7f);//强转直接加f
        } else if (btn.getId() == R.id.btn_euro) {
            show.setText(String.format("%.2f",r*euroRate));
            //val = r * (1 / 11f);//强转直接加f
        } else {
            show.setText(String.format("%.2f",r*wonRate));
           // val = r * 500;//强转直接加f
        }

    }

    public void openOne(View btn) {//超神奇！！
        //打开一个页面Activity
        Log.i("open", "openOne: ");
        openConfig();
//在添加了菜单栏之后把这个方法传过去，但是我们不希望在一个页面中出现两段一样的代码，所以可以把这些代码做成一个方法，调用就好
    }

    private void openConfig() {
        Intent config = new Intent(this, ConfigActivity.class);//打开积分程序窗口
        config.putExtra("dollar_rate_key",dollarRate);//传递到下个页面/取标签+变量本身
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);

        Log.i(TAG, "openOne: dollarRate=" + dollarRate);//检验是否把参数传过来了,调试
        Log.i(TAG, "openOne: euroRate=" + euroRate);
        Log.i(TAG, "openOne: wonRate=" + wonRate);
        // startActivity(config);
        startActivityForResult(config,1);//带回数据,1是请求参数
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//增加菜单栏
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set);{
            openConfig();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //三个参数的含义：RateActivity可以打开多个窗口，比如DOllar打开一个，euro打开一个,当请求返回时要区分是哪个窗口打开的，通过
        //requestcode就是分发出去的每个下层窗口的编码，resultcode返回时有可能是不同情况的数据，用怎样的格式去拆分
        if(requestCode==1 && resultCode==2){
            /**
             * bdl.putFloat("key_dollar",newDollar);
             *         bdl.putFloat("key_euror",newEuro);
             *         bdl.putFloat("key_won",newWon);
             */
            Bundle bundle  = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar",0.1f);
            euroRate = bundle.getFloat("key_euro",0.1f);
            wonRate = bundle.getFloat("key_won",0.1f);
            Log.i(TAG, "openOne: dollarRate=" + dollarRate);
            Log.i(TAG, "openOne: euroRate=" + euroRate);
            Log.i(TAG, "openOne: wonRate=" + wonRate);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
