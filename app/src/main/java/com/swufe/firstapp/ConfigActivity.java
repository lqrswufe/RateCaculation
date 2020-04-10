package com.swufe.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {
    private final String TAG ="ConfigActivity";
    EditText dollarText;
    EditText euroText;
    EditText wonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
       Intent intent =  getIntent();
       //把数据取出来，与RateActivity存进去的操作对比
        float dollar2= intent.getFloatExtra("dollar_rate_key",0.0f);//根据标签取，0.0f是默认值，就是当你取不到时，就会把这个默认值赋给dollar2
        float euro2= intent.getFloatExtra("euro_rate_key",0.0f);
        float won2= intent.getFloatExtra("won_rate_key",0.0f);

        Log.i(TAG, "onCreate: dollar2= " +dollar2);
        Log.i(TAG, "onCreate: euro2= " +euro2);
        Log.i(TAG, "onCreate: won2= " +won2);

        dollarText =(EditText) findViewById(R.id.dollar_rate);
        euroText =(EditText) findViewById(R.id.euro_rate);
        wonText =(EditText) findViewById(R.id.won_rate);

        //显示数据到控件
        dollarText.setText(String.valueOf(dollar2));
        euroText.setText(String.valueOf(euro2));
        wonText.setText(String.valueOf(won2));
    }

    public void save(View btn){
        Log.i(TAG,"save: ");
        //获取新的输入数据
        float newDollar = Float.parseFloat(dollarText.getText().toString());//有可能没有数值，考虑异常情况
        float newEuro = Float.parseFloat(euroText.getText().toString());
        float newWon = Float.parseFloat(wonText.getText().toString());
        Log.i(TAG, "save: 获取到新的值");
        Log.i(TAG, "save: newDollar= " +newDollar);
        Log.i(TAG, "save: newEuro= " +newEuro);
        Log.i(TAG, "save: newWon= " +newWon);


        //保存到Bundle或放入到Extra
        Intent intent = getIntent();
        Bundle bdl  = new Bundle();
        bdl.putFloat("key_dollar",newDollar);
        bdl.putFloat("key_euro",newEuro);
        bdl.putFloat("key_won",newWon);
        intent.putExtras(bdl);
        setResult(2,intent);//响应代码，前面有请求代码
        //返回到调用页面
        finish();

    }
}
//参数传递的过程:intent传递 用getitent获得对象 还有putExtra传标签,,还可以用bundle打包一起传
