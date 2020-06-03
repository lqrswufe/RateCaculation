package com.swufe.firstapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class MyList2Activity extends ListActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    Handler handler;
    private String TAG = "mylist2";
    private List<HashMap<String, String>> listItems;//存放图片,文字信息
    private SimpleAdapter listItemAdapter;//适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListView();
      //  MyAdapter myAdapter = new MyAdapter(this,R.layout.list_item,listItems);
        //this.setListAdapter(myAdapter);
        this.setListAdapter(listItemAdapter);

        Thread thread = new Thread(this);
        thread.start();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==7){
                    listItems = (List<HashMap<String,String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(MyList2Activity.this, listItems,// listItems数据源
                            R.layout.list_item,
                            new String[]{"ItemTitle", "ItemDetail"},
                            new int[]{R.id.itemTitle, R.id.itemDetail}
                    );
                  setListAdapter(listItemAdapter);
                }

                super.handleMessage(msg);
            }
        };
        getListView().setOnItemClickListener(this);//获得控件内容
        getListView().setOnItemLongClickListener(this);

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
                R.layout.list_item,
                new String[]{"ItemTitle", "ItemDetail"},
                new int[]{R.id.itemTitle, R.id.itemDetail}
        );
    }
    public void run() {
        Bundle bundle = new Bundle();
        Document doc = null;
        List<HashMap<String,String>> relist =new ArrayList<HashMap<String,String>>();
        try {
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj").get();
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
                Log.i(TAG, "run: "+str1+"==>"+val);
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("ItemTitle",str1);
                map.put("ItemDetail",val);
                relist.add(map);
                }
            /*for(Element href :hrefes) {
                String webhref = href.attr("href");
                Log.i(TAG, "getFromInfo: text= "+webhref);
                bundle.putString("keyword",webhref);
                HashMap<String,String> map = new HashMap<String,String>();

                map.put("ItemDetail",webhref);
                relist.add(map);
            }
*/

        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(7);//取出来一个消息队列
        msg.obj = relist;
        handler.sendMessage(msg);//将msg发送到队列里

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


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position="+position);
        //删除操作,在ArrayAdaoter里面有remove方法,其他的adapter就是先删除再刷新
        //listItems.remove(position);
        //listItemAdapter.notifyDataSetChanged();//数据发生了改变，是来自与list2的
//构造对话框进行操作AlertFialog
        AlertDialog.Builder bulider = new AlertDialog.Builder(this);
        bulider.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "onClick: 对话框事件处理");
                listItems.remove(position);
                listItemAdapter.notifyDataSetChanged();//数据发生了改变，是来自与list2的
            }
        })
                .setNegativeButton("否",null);
        bulider.create().show();
        return true;//短按事件依旧生效
    }
}
