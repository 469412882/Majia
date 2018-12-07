package com.haoyun.demo;

import android.app.Application;

import com.lzy.okgo.OkGo;

/**
 *
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this);
    }


}
