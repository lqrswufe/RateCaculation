package com.swufe.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
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

public class CollegeActivity extends AppCompatActivity implements Runnable {
    EditText keywords;
    TextView show;
    private final String TAG = "Result";

    Handler handler;
    private String Updatedate = "";
    private String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college);
        keywords = (EditText) findViewById(R.id.keywords);
        show = (TextView) findViewById(R.id.showOut);
       show.setMovementMethod(LinkMovementMethod.getInstance());
        //获取SP里面的数据
        SharedPreferences sharedPreferences = getSharedPreferences("mywords", Activity.MODE_PRIVATE);//字符串，访问权限
        content = sharedPreferences.getString("keyword", "");
        Updatedate = sharedPreferences.getString("update_date", "");
        //获取当前系统时间
        int today = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        Log.i(TAG, "onCreate: sp keyword=" + content);
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
                    content = bdl.getString("keyword");
                    Log.i(TAG, "handleMessage: keyword=" + content);
                    //保存更新日期
                    SharedPreferences sharedPreferences = getSharedPreferences("mywords", Activity.MODE_PRIVATE);//要记得获取和写入的文件名要一样，都是myrate
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date", todayStr);
                    editor.putString("keyword", content);
                    editor.apply();
                    Toast.makeText(CollegeActivity.this, "通知已更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }


            //匿名类改写，相当于重新创建一个类 Handler就是拿到消息之后怎么处理

        };
    }



    public void onClick(View btn) {
        Log.i(TAG, "onClick: ");
        String str = keywords.getText().toString();
        Log.i(TAG, "onClick:get str= " + str);
        if (str.length() == 0 || str == null) {
            //提示用户输入信息
            Toast.makeText(this, "请输入关键字", Toast.LENGTH_SHORT).show();
        }
        if (btn.getId() == R.id.btn_opencfg) {
            if (content != null && content.contains((CharSequence) str)) {
                show.setText(content);
            } else {
                show.setText("无匹配通知标题");
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list = new Intent(this, MyList2Activity.class);

            startActivity(list);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==1 && resultCode==2){

            Bundle bundle  = data.getExtras();
            content = bundle.getString("keyword","");

            //将新设置的汇率写到SP里，就是把APP关掉以后会保存更改的数据，我们在APP里面把第一个汇率改成500，你下次打开后显示的就是500
            SharedPreferences sharedPreferences = getSharedPreferences("mywords",Activity.MODE_PRIVATE);//要记得获取和写入的文件名要一样，都是myrate
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("keyword",content);
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

        bundle= getFromInfo();
        //Bundle中保存所获得的对象

        Message msg = handler.obtainMessage(5);//取出来一个消息队列

        msg.obj = bundle;
        handler.sendMessage(msg);//将msg发送到队列里


        //2同步加引入包

    }
    /*
    从bank of china 获取数据* */


    private Bundle getFromInfo() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG, "run: " + doc.title());
            Elements spans = doc.getElementsByTag("span");
            Elements hrefes = doc.getElementsByTag("a");
            for (int i = 0; i < spans.size(); i ++) {
                Element td1 = spans.get(i);
                Log.i(TAG, "run: text=" + td1.text() );
                String str1 = td1.text();
                bundle.putString("keyword",str1);
            }
            for(Element href :hrefes) {
                String webhref = href.attr("href");
                Log.i(TAG, "getFromInfo: text= "+webhref);
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

