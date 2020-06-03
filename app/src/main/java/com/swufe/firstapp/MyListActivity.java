package com.swufe.firstapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    List<String> data = new ArrayList<String>();
    private  String TAG = "MyList";
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        //固定列数GridView listView = findViewById(R.id.mylist);
        ListView listView = findViewById(R.id.mylist);
        for(int i=0;i<10;i++){
            data.add("item"+i);
        }


        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.nodata));//列表没数据的时候，显示nodata的内容
        listView.setOnItemClickListener(this);//删除数据，添加点击事件
    }

    @Override
    public void onItemClick(AdapterView<?> listv, View view, int position, long id) {
        Log.i(TAG, "onItemClick: position:"+position);
        Log.i(TAG, "onItemClick: parent"+listv);
        adapter.remove(listv.getItemAtPosition(position));
       // adapter.notifyDataSetChanged();

    }
}
