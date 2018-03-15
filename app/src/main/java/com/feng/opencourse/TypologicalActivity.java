package com.feng.opencourse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.feng.opencourse.adapter.TypologicalListViewAdapter;
import com.feng.opencourse.util.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class TypologicalActivity extends AppCompatActivity {
    private ListView lvTypological;
    private List<String> typologicalList = new ArrayList<String>(){{
        add("Python");
        add("Java");
        add("Android");
        add("MachineLearning");
        add("CloudComputing");}};
    private MyApplication myapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typological);

        myapp = (MyApplication) getApplication();
        lvTypological = (ListView) findViewById(R.id.lv_typological);
        lvTypological.setAdapter(new TypologicalListViewAdapter(myapp.getApplicationContext(),typologicalList));
        lvTypological.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int clickType = position + 1;
                Intent intent = new Intent();
                intent.putExtra("clickType",clickType);
                intent.setClass(myapp.getApplicationContext(),ClassifyActivity.class);
                startActivity(intent);
            }
        });
    }
}
