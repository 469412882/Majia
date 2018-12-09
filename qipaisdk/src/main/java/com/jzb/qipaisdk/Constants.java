package com.jzb.qipaisdk;

import android.app.Application;

import com.lzy.okgo.OkGo;

public class Constants {
    public static String APP_ID = "";
    public static void init(Application application, String appId){
        OkGo.getInstance().init(application);
        APP_ID = appId;
    }
}
