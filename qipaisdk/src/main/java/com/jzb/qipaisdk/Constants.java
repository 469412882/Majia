package com.jzb.qipaisdk;

import android.app.Activity;
import android.app.Application;

import com.lzy.okgo.OkGo;

import cn.jpush.android.api.JPushInterface;

public class Constants {
    public static String APP_ID = "";
    public static Class mainClass;
    public static void init(Application application, String appId, Class mainActivity){
        OkGo.getInstance().init(application);
        JPushInterface.init(application);
        APP_ID = appId;
        mainClass = mainActivity;
    }
}
