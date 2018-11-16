package com.xq.mywifibluetooth;

import android.app.Application;


public class MyApplication extends Application {

    private MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public MyApplication getInstance() {
        return myApplication;
    }
}
