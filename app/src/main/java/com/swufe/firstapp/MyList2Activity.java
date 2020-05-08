package com.swufe.firstapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyList2Activity extends ListActivity {
    Handler handler;
    private ArrayList<HashMap<String, String>> listItems;
    private SimpleAdapter listItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_list2);

    }

    private void initListView() {
        listItems = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", "Rate:" + i);//标题文字
            map.put("ItemDetail", "detail" + i);
            listItems.add(map);
        }
//生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItems,// listItems数据源
                R.layout.activity_my_list2,
                new String[]{"ItemTitle", "ItemDetail"},
                new int[]{R.id.itemTitle, R.id.itemDetail}
        );
    }
    public void run() {
        Document doc = null;
        List<HashMap<String,String>> relist =new ArrayList<HashMap<String,String>>();
        try {
            doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
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
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("ItemTitle",str1);
                map.put("ItemDetail",val);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

}
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        Log.i(TAG, "onItemClick: parent="+parent);
        Log.i(TAG, "onItemClick: view="+view);
        Log.i(TAG, "onItemClick: position="+position);
        Log.i(TAG, "onItemClick: id="+id);
        HashMap<String, String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        Log.i(TAG, "onItemClick: titleStr="+titleStr);
        Log.i(TAG, "onItemClick: detailStr="+detailStr);
        TextView title = (TextView)view.findViewById(R.id.itemTitle);
        TextView detail = (TextView)view.findViewById(R.id.itemDetail);
        String title2=String.valueOf(title.getText());
        String detail2=String.valueOf(detail.getText());
        Log.i(TAG, "onItemClick: title2="+title2);
        Log.i(TAG, "onItemClick: detail2="+detail2);
        //打开新的页面
        Intent rateCal = new Intent(this,RateCalActivity.class);
        rateCal.putExtra("title",titleStr);
        rateCal.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCal);
    }
}
