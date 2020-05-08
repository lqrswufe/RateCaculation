package com.swufe.firstapp;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class RateListActivity extends ListActivity implements Runnable{
    Handler handler;

    String data[] = {"one","two","three"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);
        List<String> list1=new ArrayList<String>();
        for(int i =1;i<100;i++){
            list1.add("item"+i);
        }
        ListAdapter adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        setListAdapter(adapter);
        Thread thread = new Thread(this);
        thread.start();
        handler = new Handler(){
            public void handleMessage(Message msg){
                if(msg.what==7){
                    List<String> list2 = (List<String>) msg.obj;
                    ListAdapter adapter= new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);
                    setListAdapter(adapter);
                }

                super.handleMessage(msg);
            }
        };
    }
    public void run(){
        List<String>relist = new ArrayList<String>();
        Document doc = null;
        try {
            Thread.sleep(30000);
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG, "run: " + doc.title());
            Elements tables = doc.getElementsByTag("table");
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
               relist.add(td1.text() + "==>" + td2.text());

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(5);//取出来一个消息队列
        msg.obj = relist;
        handler.sendMessage(msg);//将msg发送到队列里
    }
}
