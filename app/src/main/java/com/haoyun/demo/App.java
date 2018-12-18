package com.haoyun.demo;

import android.app.Application;

import com.jzb.qipaisdk.Constants;


/**
 *
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        Constants.init(this, "0056700115", MainActivity.class);
    }


}
