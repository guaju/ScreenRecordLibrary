package com.guaju.screenrecordlibrary;

import android.app.Application;

import com.guaju.screenrecorderlibrary.ScreenRecorderHelper;

/**
 * Created by guaju on 2017/12/29.
 */

public class MyApplication extends Application {

    private ScreenRecorderHelper instance;
    public static MyApplication app;
    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
        //init
        instance = ScreenRecorderHelper.getInstance(this);
    }

    public ScreenRecorderHelper getSRHelper(){
        return instance;
    }
    public static MyApplication getApp(){
        return app;
    }
}
