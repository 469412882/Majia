package com.jzb.qipaisdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jzb.qipaisdk.permission.PermissionListener;
import com.jzb.qipaisdk.permission.PermissionsUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class IntroActivity extends Activity {
    String dataValue;
    String updateDataValue;
    boolean getResponse = false;
    boolean leastWaitingOver = false;
    private String urls = "http://www.nnokwa.com/lottery/back/api.php?type=android&app_id=" + Constants.APP_ID;


    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 111:
                    Intent bundle = new Intent(IntroActivity.this, UpdateListActivity.class);
                    bundle.putExtra("json", updateDataValue);
                    startActivity(bundle);
                    finish();
                    break;
                case 1:
                    if (dataValue == null) {
                        Toast.makeText(getApplication(), "网络异常", Toast.LENGTH_LONG).show();
                        goMainActivity();
                        break;
                    }
                    Map<String, String> map = AppUtils.parseKeyAndValueToMap(dataValue);
                    if (map == null) {
                        goMainActivity();
                        break;
                    }
                    if (map.get("code").equals("201")) {
                        goMainActivity();
                        break;
                    }
                    String is_update = mGetValue("is_update");
                    String update_url = mGetValue("update_url");
                    // TODO: 2018/12/8
                    if (!TextUtils.isEmpty(update_url)) {
                        getUpdateInfo(update_url);//强更状态获取数据
                        return;
                    }
                    if (is_update.equals("1")) {
                        getUpdateInfo(update_url);//强更状态获取数据
                    } else {
                        String is_wap = mGetValue("is_wap");
                        String wap_url = mGetValue("wap_url");
                        if (is_wap.equals("1")) {
                            intentToWebViewActivity(wap_url);//跳转网页
                        } else {
                            goMainActivity();
                        }
                    }
                    break;
                default:
                    break;

            }
            super.handleMessage(msg);
        }
    };

    public String mGetValue(String s) {
        int ai = dataValue.indexOf(s);
        String as = dataValue.substring((ai + s.length() + 3), dataValue.length());
        return as.substring(0, as.indexOf("\"")).replace("\\/", "/");

    }

    private void goMainActivity() {
        intentToWebViewActivity("http://www.cwlchina.com/");
    }

    private void intentToWebViewActivity(String wap_url) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(this, WebViewActivity.class);
        intent.putExtra("url", wap_url);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(generateContentView());
        requestData();
    }


    public void requestData() {
        new Thread() {
            public void run() {
                dataValue = getPageSource(urls);
                getResponse = true;
                if (leastWaitingOver) {
                    mHandler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    public void getUpdateInfo(final String url) {
        new Thread() {
            public void run() {
                updateDataValue = getPageSource2(url);
                mHandler.sendEmptyMessage(111);
            }

        }.start();
    }

    public String getPageSource(String urls) {
        StringBuilder sb = new StringBuilder();
        BufferedReader in = null;
        try {
            URL url = new URL(urls);
            URLConnection connection = url.openConnection();
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
        } catch (Exception ex) {
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String uidFromBase64 = null;
        if (!sb.toString().contains("<html>")) {
            uidFromBase64 = getUidFromBase64(sb.toString());
        }
        return uidFromBase64;
    }

    public String getPageSource2(String urls) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            URL realUrl = new URL(urls);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        //得到的json数据
        return result.toString();
    }

    public String getUidFromBase64(String base64Id) {
        String result = "";
        if (!TextUtils.isEmpty(base64Id)) {
            if (!TextUtils.isEmpty(base64Id)) {
                result = new String(Base64.decode(base64Id.getBytes(), Base64.DEFAULT));
            }
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        goStart();
        requestPhoneSdCardPermission();
    }

    private void goStart() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                leastWaitingOver = true;
                if (getResponse) {
                    mHandler.sendEmptyMessage(1);
                }
            }
        }, 3000);
    }

    private View generateContentView() {
        LinearLayout rootView = new LinearLayout(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(lp);
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        int lanunchImg = AppUtils.getDrawableIdByName(this, "launchimg");
        if (lanunchImg != 0) {
            imageView.setImageResource(lanunchImg);
        }
        rootView.addView(imageView);
        return rootView;
    }

    private void requestPhoneSdCardPermission() {
        if (!PermissionsUtil.hasPermission(this, PERMISSIONS_STORAGE)) {
            PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {

                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {

                }
            }, PERMISSIONS_STORAGE);
        }
    }
}
