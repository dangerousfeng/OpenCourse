package com.feng.opencourse;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Handler handler=new Handler();
        Runnable timer=new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent();
                intent.setClass(LoadingActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(timer,500);
    }
}
