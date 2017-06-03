package com.laocuo.jumpjump.home;

/**
 * Created by hoperun on 11/28/16.
 */

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.laocuo.jumpjump.JumpApp;
import com.laocuo.jumpjump.R;
import com.laocuo.jumpjump.chat.ChatActivity;
import com.laocuo.jumpjump.chat.ChatNewActivity;
import com.laocuo.jumpjump.game.GameActivity;
import com.laocuo.jumpjump.login.JumpUser;
import com.laocuo.jumpjump.login.LoginActivity;
import com.laocuo.jumpjump.setting.SettingActivity;
import com.laocuo.jumpjump.utils.L;
import com.laocuo.jumpjump.utils.NetWorkUtil;
import com.laocuo.jumpjump.utils.SnackbarUtil;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;

public class HomeActivity extends AppCompatActivity {

    private static final int BATTLE_REQUEST_LOGIN = 1;
    private static final int CHAT_REQUEST_LOGIN = 2;
    private Button start, setting, battle, chat;
    private HomeButtonClick hbc;
    private LinearLayout mLayout;
    private Handler mHandler;
    private boolean isExit;
    private ExitRunnable mRunnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        setListener();
    }

    private void setListener() {
        // TODO Auto-generated method stub
        start.setOnClickListener(hbc);
        setting.setOnClickListener(hbc);
        battle.setOnClickListener(hbc);
        chat.setOnClickListener(hbc);
    }

    private void initView() {
        // TODO Auto-generated method stub
//        L.d("Bmob.initialize");
//        Bmob.initialize(HomeActivity.this, JumpApp.getContext().getApplicationID());
//        BmobUpdateAgent.initAppVersion();
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.setUpdateCheckConfig(false);
        BmobUpdateAgent.update(this);
        mLayout = (LinearLayout) findViewById(R.id.buttongroup);
        start = (Button) findViewById(R.id.start);
        setting = (Button) findViewById(R.id.setting);
        battle = (Button) findViewById(R.id.battle);
        chat = (Button) findViewById(R.id.chat);
        hbc = new HomeButtonClick();
        mHandler = new Handler();
        mRunnable = new ExitRunnable();
    }

    @Override
    public void onBackPressed() {
        if (isExit) {
            mHandler.removeCallbacks(mRunnable);
            finish();
        } else {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            isExit = true;
            mHandler.postDelayed(mRunnable, 2000);
        }
    }

    private class ExitRunnable implements Runnable {

        @Override
        public void run() {
            isExit = false;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BATTLE_REQUEST_LOGIN) {
            if (resultCode == 1) {
                enterGame(true);
            }
        } else if (requestCode == CHAT_REQUEST_LOGIN) {
            if (resultCode == 1) {
                enterChat();
            }
        }
    }

    private void enterGame(boolean battle) {
        Intent i = new Intent(HomeActivity.this, GameActivity.class);
        if (battle) {
            i.putExtra("NETWORK_BATTLE", true);
        }
        startActivity(i);
    }

    private void enterChat() {
        if (NetWorkUtil.isNetWorkAvailable(this)) {
            startActivity(new Intent(HomeActivity.this, ChatActivity.class));
        } else {
            SnackbarUtil.showShortSnackbar(mLayout, "DATA DISCONNECTED!");
        }
    }

    private void requestLogin() {
        startActivityForResult(new Intent(HomeActivity.this, LoginActivity.class), CHAT_REQUEST_LOGIN);
    }

    protected class HomeButtonClick implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                case R.id.start: {
                    enterGame(false);
                    break;
                }

                case R.id.setting: {
                    Intent i = new Intent(HomeActivity.this, SettingActivity.class);
                    startActivity(i);
                    break;
                }

                case R.id.battle: {
                    final JumpUser user = BmobUser.getCurrentUser(JumpUser.class);
                    if (user != null) {
                        enterGame(true);
                    } else {
                        requestLogin();
                    }
                    break;
                }

                case R.id.chat: {
                    final JumpUser user = BmobUser.getCurrentUser(JumpUser.class);
                    if (user != null) {
                        enterChat();
                    } else {
                        requestLogin();
                    }
                    break;
                }
            }
        }
    }
}
