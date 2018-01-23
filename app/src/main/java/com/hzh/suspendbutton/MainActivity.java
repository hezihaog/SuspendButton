package com.hzh.suspendbutton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hzh.suspendbutton.widget.CanDragLayout;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CanDragLayout dragLayout = (CanDragLayout) findViewById(R.id.dragLayout);
        dragLayout.setOnDragLayoutClickListener(new CanDragLayout.OnDragLayoutClickListener() {
            @Override
            public void onClick() {
                String url = "http://www.baidu.com";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }
}