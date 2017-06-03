package com.laocuo.jumpjump.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.laocuo.jumpjump.JumpApp;
import com.laocuo.jumpjump.R;
import com.laocuo.jumpjump.home.HomeActivity;

import cn.bmob.v3.Bmob;

/**
 * Created by hoperun on 11/24/16.
 */

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean mIsSkip = false;
    private TextView mTips;
    private int timeleft = 3;
    private TimeRunnable mTimeRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mTips = (TextView) findViewById(R.id.wel_txt);
        setTips();
        mTips.setOnClickListener(this);
        mTimeRunnable = new TimeRunnable();
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTips.postDelayed(mTimeRunnable, 1000);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private class TimeRunnable implements Runnable {

        @Override
        public void run() {
            if (!mIsSkip) {
                timeleft--;
                setTips();
                if (timeleft == 0) {
                    _doSkip();
                } else {
                    mTips.postDelayed(mTimeRunnable, 1000);
                }
            }
        }
    }

    private void init() {
        //提供以下两种方式进行初始化操作：

        //第一：默认初始化
        Bmob.initialize(this, JumpApp.getContext().getApplicationID());

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

    private void setTips() {
        mTips.setText(timeleft+"S");
    }

    private void _doSkip() {
        if (!mIsSkip) {
            mIsSkip = true;
            finish();
            startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
//            overridePendingTransition(R.anim.hold, R.anim.zoom_in_exit);
        }
    }

    @Override
    public void onClick(View view) {
        _doSkip();
    }
}
