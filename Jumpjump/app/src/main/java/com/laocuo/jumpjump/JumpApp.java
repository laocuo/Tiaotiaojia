package com.laocuo.jumpjump;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.laocuo.jumpjump.setting.SettingActivity;

import cn.bmob.v3.Bmob;

/**
 * Created by hoperun on 11/24/16.
 */

public class JumpApp extends Application {
    private static JumpApp instance;
    private static Object ob = new Object();
    private String ApplicationID = "9c1baae2db9defd5fb610af90e155830";
    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        bmobInit();
    }

    private void bmobInit() {
        //第一：默认初始化
        Bmob.initialize(this, ApplicationID);

        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        //BmobConfig config =new BmobConfig.Builder(this)
        ////设置appkey
        //.setApplicationId("Your Application ID")
        ////请求超时时间（单位为秒）：默认15s
        //.setConnectTimeout(30)
        ////文件分片上传时每片的大小（单位字节），默认512*1024
        //.setUploadBlockSize(1024*1024)
        ////文件的过期时间(单位为秒)：默认1800s
        //.setFileExpiration(2500)
        //.build();
        //Bmob.initialize(config);
    }

    public static JumpApp getContext() {
        synchronized (ob) {
            if (instance == null) {
                instance = new JumpApp();
            }
        }
        return instance;
    }

    public String getApplicationID() {
        return ApplicationID;
    }

    public boolean isVsComputer() {
        return sp.getBoolean(SettingActivity.COMSWITCH, false);
    }

    public boolean isAudioOpen() {
        return sp.getBoolean(SettingActivity.AUDIOSWITCH, false);
    }

    public int getComDifficulty() {
        String mDifficult = sp.getString(SettingActivity.COMDIFFCULTY, "0");
        return Integer.valueOf(mDifficult);
    }
}
