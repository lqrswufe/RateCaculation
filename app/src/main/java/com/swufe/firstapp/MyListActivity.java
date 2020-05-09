package com.swufe.firstapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        ListView listView = findViewById(R.id.mylist);
        String data[] = {"关于经济信息工程学院2019级专业分流预报名的通知","关于开展2020年西南财经大学国家级（省级）大学生创业训练计划项目、创业实践项目立项申报的通知","关于组织申报2020年度大学生创新训练计划项目的通知"};
        ListAdapter adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
    }
}
