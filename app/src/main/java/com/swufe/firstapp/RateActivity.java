package com.swufe.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RateActivity extends AppCompatActivity implements Runnable {//多线程，Runnable只有一个Run方法，我们要线程做的事情都要放在Run里面
    EditText rmb;
    TextView show;
    private final String TAG ="Rate";
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.showOut);
        //获取SP里面的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);//字符串，访问权限
       dollarRate =  sharedPreferences.getFloat("dollar_rate",0.0f);//0.0f也是默认值
        euroRate =  sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate =  sharedPreferences.getFloat("won_rate",0.0f);
        Log.i(TAG, "onCreate: sp dollarRate=" +dollarRate);
        Log.i(TAG, "onCreate: sp euroRate=" +euroRate);
        Log.i(TAG, "onCreate: sp wonRate=" +wonRate);

        //在主线程里面开启子线程
        Thread t = new Thread(this);//要记得加当前对象,才能调用到Run方法,t就代表当前线程
        t.start();

         handler = new Handler(){//将子线程带回到主线程
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){//5是判断从哪个线程得到的数据
                    String str = (String) msg.obj;
                    Log.i(TAG, "handleMessage: getMessage msg =" +str);
                    show.setText(str);
                }
                super.handleMessage(msg);
            }
        };//匿名类改写，相当于重新创建一个类 Handler就是拿到消息之后怎么处理


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
//在添加了菜单栏之后把这个方法传过去，但是我们不希望在一个页面中出现两段一样的代码，所以可以把这些代码做成一个方法opemConfig，调用就好
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

            //将新设置的汇率写到SP里，就是把APP关掉以后会保存更改的数据，我们在APP里面把第一个汇率改成500，你下次打开后显示的就是500
            SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);//要记得获取和写入的文件名要一样，都是myrate
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();//保存数据
            Log.i(TAG, "onActivityResult: 数据已保存到sharedPreferences");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {
        Log.i(TAG, "run: run().....");
        for(int i =1;i<3;i++){
            Log.i(TAG, "run: i="+ i);
            try{
            Thread.sleep(2000);
        }catch(InterruptedException e){
                e.printStackTrace();
            }//做一个延时操作
    }
        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);//取出来一个消息队列
       // msg.what = 5;//what用于标记当前数据的类型,跟上列的括号里面的功能一样
        msg.obj = "Hello from run()";
        handler.sendMessage(msg);//将msg发送到队列里

        //获取网络数据
        URL url = null;
        try {
            url = new URL("http://www.usd-cny.com/icbc.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();//输入流

            String html = inputStreamToString(in);
            Log.i(TAG, "run: html=" + html);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
    private String inputStreamToString(InputStream inputStream) throws IOException {//将输入流转为字符串方法
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}

/*调试程序方法，我们之前是用log来进行调试，会显示在Logcat里面，还可以用另一种方式
*在列号后点击设置断点，然后点击最上方Run-Debug,进入调试模式，程序会在断点处停止运行，再点击LogCat的单步运行
* 一步一步的运行就可以检查到底是哪里出了问题了
 */