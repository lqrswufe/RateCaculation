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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable {//多线程，Runnable只有一个Run方法，我们要线程做的事情都要放在Run里面
    EditText rmb;
    TextView show;
    private final String TAG = "Rate";
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;
    Handler handler;
    private String Updatedate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.showOut);
        //获取SP里面的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);//字符串，访问权限
        dollarRate = sharedPreferences.getFloat("dollar_rate", 0.0f);//0.0f也是默认值
        euroRate = sharedPreferences.getFloat("euro_rate", 0.0f);
        wonRate = sharedPreferences.getFloat("won_rate", 0.0f);
        Updatedate = sharedPreferences.getString("update_date", "");
        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        Log.i(TAG, "onCreate: sp dollarRate=" + dollarRate);
        Log.i(TAG, "onCreate: sp euroRate=" + euroRate);
        Log.i(TAG, "onCreate: sp wonRate=" + wonRate);
        Log.i(TAG, "onCreate: sp Updatedate=" + Updatedate);

        //判断时间
        if (!todayStr.equals(Updatedate)) {
            Log.i(TAG, "onCreate: 需要更新");
            Thread t = new Thread(this);//要记得加当前对象,才能调用到Run方法,t就代表当前线程
            t.start();

        } else {
            Log.i(TAG, "onCreate: 不需要更新");
        }

        handler = new Handler() {
            public void handleMessage(Message msg) {
                //将子线程带回到主线程
                if (msg.what == 5) {//5是判断从哪个线程得到的数据
                    Bundle bdl = (Bundle) msg.obj;
                    dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    wonRate = bdl.getFloat("won-rate");
                    Log.i(TAG, "handleMessage: dollarRate=" + dollarRate);
                    Log.i(TAG, "handleMessage: euroRate=" + euroRate);
                    Log.i(TAG, "handleMessage: wonRate=" + wonRate);
                    //保存更新日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);//要记得获取和写入的文件名要一样，都是myrate
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date", todayStr);
                    editor.putFloat("dollar_rate", dollarRate);
                    editor.putFloat("euro_rate", euroRate);
                    editor.putFloat("won_rate", wonRate);
                    editor.apply();
                    Toast.makeText(RateActivity.this, "汇率已更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }


            //匿名类改写，相当于重新创建一个类 Handler就是拿到消息之后怎么处理

        };
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
        if(item.getItemId()==R.id.menu_set){
            openConfig();
        }else if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list = new Intent(this, MyListActivity.class);//打开积分程序窗口

            startActivity(list);

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
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }//做一个延时操作
        //用户用于保存获取的数据
            Bundle bundle = new Bundle();
            //获取Msg对象，用于返回主线程

            //获取网络数据
            URL url = null;
        /*try {
            url = new URL("http://www.usd-cny.com/bankofchina.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();//输入流
            String html = inputStreamToString(in);
            Log.i(TAG, "run: html=" + html);
            Document doc = Jsoup.parse(html);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }*/
        bundle= getFromBOC();
        //Bundle中保存所获得的对象

            Message msg = handler.obtainMessage(5);//取出来一个消息队列
            // msg.what = 5;//what用于标记当前数据的类型,跟上列的括号里面的功能一样
            // msg.obj = "Hello from run()";
            msg.obj = bundle;
            handler.sendMessage(msg);//将msg发送到队列里


            //2同步加引入包

    }
/*
从bank of china 获取数据* */
    private Bundle getFromBOC() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG, "run: " + doc.title());
            Elements tables = doc.getElementsByTag("table");
            /*int i = 1;
            for(Element table : tables) {
                Log.i(TAG, "run: table["+i+"]s=" + table);
                i++;
            }*/
            Element table1 = tables.get(0);
            // Log.i(TAG, "run: table1=" +table1);
            //获取TD中的元素
            Elements tds = table1.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i += 6) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                Log.i(TAG, "run: text=" + td1.text() + "==>" + td2.text());
                String str1 = td1.text();
                String val = td2.text();
                if ("美元".equals(str1)) {
                    bundle.putFloat("dollar-rate", 100f / Float.parseFloat(val));
                } else if ("欧元".equals(str1)) {
                    bundle.putFloat("euro-rate", 100f / Float.parseFloat(val));
                } else if ("韩元".equals(str1)) {
                    bundle.putFloat("won-rate", 100f / Float.parseFloat(val));
                }

            }
            /*for(Element td :tds){
                Log.i(TAG, "run: td="+tds);
                Log.i(TAG, "run: text="+td.text());
                Log.i(TAG, "run: text="+td.html());//注意两者之间的差别,获取币种、价格
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }
    private Bundle getFromUsdCny() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG, "run: " + doc.title());
            Elements tables = doc.getElementsByTag("table");
            /*int i = 1;
            for(Element table : tables) {
                Log.i(TAG, "run: table["+i+"]s=" + table);
                i++;
            }*/
            Element table1 = tables.get(0);
            // Log.i(TAG, "run: table1=" +table1);
            //获取TD中的元素
            Elements tds = table1.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i += 6) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                Log.i(TAG, "run: text=" + td1.text() + "==>" + td2.text());
                String str1 = td1.text();
                String val = td2.text();
                if ("美元".equals(str1)) {
                    bundle.putFloat("dollar-rate", 100f / Float.parseFloat(val));
                } else if ("欧元".equals(str1)) {
                    bundle.putFloat("euro-rate", 100f / Float.parseFloat(val));
                } else if ("韩元".equals(str1)) {
                    bundle.putFloat("won-rate", 100f / Float.parseFloat(val));
                }

            }
            /*for(Element td :tds){
                Log.i(TAG, "run: td="+tds);
                Log.i(TAG, "run: text="+td.text());
                Log.i(TAG, "run: text="+td.html());//注意两者之间的差别,获取币种、价格
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
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