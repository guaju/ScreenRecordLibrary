package com.guaju.screenrecordlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.guaju.screenrecorderlibrary.ScreenRecorderHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_start,bt_stop;
    private ScreenRecorderHelper srHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        srHelper = MyApplication.getApp().getSRHelper();
        srHelper.initRecordService(this);


    }

    private void initView() {
        bt_start = findViewById(R.id.bt_start);
        bt_stop = findViewById(R.id.bt_stop);
        bt_start.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.bt_start){
        srHelper.startRecord(this);
        }else if (v.getId()==R.id.bt_stop){
           srHelper.stopRecord(new ScreenRecorderHelper.OnRecordStatusChangeListener() {
               @Override
               public void onChangeSuccess() {
                   //当停止成功，做界面变化
                   bt_start.setText("开始录制");
                   Toast.makeText(MainActivity.this, "录屏成功"+srHelper.getRecordFilePath(), Toast.LENGTH_SHORT).show();
               }

               @Override
               public void onChangeFailed() {
                   //不作处理

               }
           });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          srHelper.onActivityResult(this, requestCode, resultCode, data, new ScreenRecorderHelper.OnRecordStatusChangeListener() {
              @Override
              public void onChangeSuccess() {
                  //控制开始按钮的文字变化
                  bt_start.setText("正在录制");
              }

              @Override
              public void onChangeFailed() {
                  //如果录制失败，则不作任何变化
                  bt_start.setText("开始录制");
              }
          });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        srHelper.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }
}
