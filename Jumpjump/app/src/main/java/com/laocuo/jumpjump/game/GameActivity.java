/*
* Copyright (C) 2013-2016 laocuo@163.com .
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.laocuo.jumpjump.game;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.laocuo.jumpjump.JumpApp;
import com.laocuo.jumpjump.R;
import com.laocuo.jumpjump.base.LedTextView;
import com.laocuo.jumpjump.battle.BattleContract;
import com.laocuo.jumpjump.battle.BattlePresenter;
import com.laocuo.jumpjump.battle.MessageIDCommon;
import com.laocuo.jumpjump.battle.Room;
import com.laocuo.jumpjump.battle.RoomInfo;
import com.laocuo.jumpjump.help.HelpActivity;
import com.laocuo.jumpjump.login.JumpUser;
import com.laocuo.jumpjump.setting.SettingActivity;
import com.laocuo.jumpjump.utils.FactoryInterface;
import com.laocuo.jumpjump.utils.L;
import com.laocuo.jumpjump.utils.NetWorkUtil;
import com.laocuo.jumpjump.utils.SnackbarUtil;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class GameActivity extends AppCompatActivity implements GameContract, BattleContract.View, LedTextView.UpdateListener {

    private final int padding_width = 10;
    private final int padding_height = 10;
    private Boolean isNetworkBattle;
    private GameView mv;
    private GameBgView mbgv;
    private Button b1, b2, b3, b4;
    private ImageView selectedChess;
    private ListView chessRecordList;
    private MainButtonClick mbc;
    private ArrayList<Integer> width_list;
    private ArrayList<Integer> height_list;
    private int son_width;
    private Bitmap SonBitmap = null;
    private int screen_width;
    private PopupWindow mBattleMenuPop;
    private View mBattleMenu;
    private RelativeLayout mRelativeLayout;
    private ServiceHandler mServiceHandler;
    private ProgressDialog mCreateRoomDialog;
    private AlertDialog mJoinRoomDialog, mResetRoomDialog,
            mExitConfirmDialog, mGameOverDialog;
    private ProgressDialog mWaittingDialog;
    private DialogInterface.OnClickListener mRoomSelectListener;
    private RoomListAdapter mRoomListAdapter;
    private BattlePresenter mBattlePresenter;
    private BattleContract.Presenter mPresenter;
    private List<RoomInfo> mRoomList;
    private boolean mIsHomeUser = true;
    private boolean isBattleStart,isGameExit;
    private String mCurrentChessStep;
    private TextView mHomeMemberAlias, mAwayMemberAlias;
    private JumpUser mCurrentUser, mAnotherUser;
    private ImageView mHomeMemberPane, mAwayMemberPane;
    private ImageView mHomeMemberAvatar, mAwayMemberAvatar;
    private LedTextView mTimeLeave;

    @Override
    public void setPresenter(BattleContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void createRoomSuccess() {
        mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.CREATE_ROOM_SUCCESS, MessageIDCommon.DELAY_TIME);
    }

    @Override
    public void createRoomFail() {
        if (mCreateRoomDialog.isShowing() == true) {
            mCreateRoomDialog.dismiss();
        }
        mBattleMenuPop.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);
    }

    @Override
    public void setRoomCanJoinResult(boolean result) {
        if (result == true) {
            startLoopQueryRoomJoin();
        } else {
            //TODO
        }
    }

    @Override
    public void updateRoom(List<Room> list) {
        mRoomList.clear();
        for (Room r : list) {
            mRoomList.add(new RoomInfo(r));
        }
        if (mJoinRoomDialog != null && mJoinRoomDialog.isShowing() == true) {
            mServiceHandler.sendEmptyMessage(MessageIDCommon.RETURN_ROOM_NAME);
        }
    }

    @Override
    public void startGame(boolean isHomeUser) {
        mIsHomeUser = isHomeUser;
        if (isHomeUser == true) {
            stopLoopQueryRoomJoin();
        } else {
//            startLoopQueryChessStep();
        }
        isBattleStart = true;
        mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.JOIN_ROOM_SUCCESS, MessageIDCommon.DELAY_TIME);
    }

    @Override
    public void joinRoomFail() {
        closeWaittingDialog();
        joinRoom();
    }

    @Override
    public void parseChessStep(String chessStep) {
        Message msg = mServiceHandler.obtainMessage(MessageIDCommon.CHESS_STEP_UPDATE);
        msg.obj = chessStep;
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public void exitGame() {
        if (isGameExit == false) {
            isGameExit = true;
            closeWaittingDialog();
            finish();
        }
    }

    @Override
    public void roomCanJoin() {
        mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.ROOM_CAN_JOIN, MessageIDCommon.DELAY_TIME);
    }

    @Override
    public void updateChessStepResult(boolean result) {
//        Message msg = mServiceHandler.obtainMessage(MessageIDCommon.CHESS_STEP_UPDATE_RESULT);
//        msg.obj = result;
//        mServiceHandler.sendMessageDelayed(msg, MessageIDCommon.LOOP_TIME);
        if (result == false) {
            mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.CHESS_STEP_UPDATE_RESULT, MessageIDCommon.LOOP_TIME);
        }
    }

    @Override
    public void resetRoomConfirmResult(int result) {
//        if (result > 1) {
//            stopLoopQueryResetRoom();
//        }
        Message msg = mServiceHandler.obtainMessage(MessageIDCommon.RESET_ROOM_CONFIRM_RESULT);
        msg.obj = result;
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public void updateMemberInfo(JumpUser anotherUser) {
        mAnotherUser = anotherUser;
        updateMemberInfo();
    }

    @Override
    public void deleteRoomResult(boolean result) {
        if (false == result) {
            mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.DELETE_ROOM, MessageIDCommon.DELAY_TIME);
        } else {
            closeWaittingDialog();
            mBattleMenuPop.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void resetRoomResult(boolean result) {
        Message msg = mServiceHandler.obtainMessage(MessageIDCommon.RESET_ROOM_RESULT);
        msg.obj = result;
        mServiceHandler.sendMessageDelayed(msg, MessageIDCommon.LOOP_TIME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        isNetworkBattle = getIntent().getBooleanExtra("NETWORK_BATTLE", false);
        L.d("isNetworkBattle=" + isNetworkBattle);
        setContentView(R.layout.activity_game);
        mCurrentUser = BmobUser.getCurrentUser(JumpUser.class);
        mHomeMemberAlias = (TextView) findViewById(R.id.home_member_alias);
        mAwayMemberAlias = (TextView) findViewById(R.id.away_member_alias);
        mHomeMemberPane = (ImageView) findViewById(R.id.home_member_pane);
        mAwayMemberPane = (ImageView) findViewById(R.id.away_member_pane);
        mHomeMemberAvatar = (ImageView) findViewById(R.id.home_member_avatar);
        mAwayMemberAvatar = (ImageView) findViewById(R.id.away_member_avatar);
        mTimeLeave = (LedTextView) findViewById(R.id.time_leave);
        if (!isNetworkBattle) {
            mTimeLeave.start();
        }

        mRelativeLayout = (RelativeLayout) findViewById(R.id.main_activity);
        mv = (GameView) findViewById(R.id.mainview);
        mv.setContact((GameContract) this);
        mv.setTimeControl(mTimeLeave);
        mbgv = (GameBgView) findViewById(R.id.mainbgview);
        mTimeLeave.addListener(this);
        mTimeLeave.addListener(mv);
        selectedChess = (ImageView) findViewById(R.id.selectedChess);
        selectedChess.setImageResource(R.drawable.redstar);
        selectedChess.setAlpha(1f);
        selectedChess.setVisibility(View.GONE);
        chessRecordList = (ListView) findViewById(R.id.chessRecordList);
        init();
        b1 = (Button) this.findViewById(R.id.button1);
        b2 = (Button) this.findViewById(R.id.button2);
        b3 = (Button) this.findViewById(R.id.button3);
        b4 = (Button) this.findViewById(R.id.button4);

        mbc = new MainButtonClick();

        b1.setOnClickListener(mbc);
        b2.setOnClickListener(mbc);
        b3.setOnClickListener(mbc);
        b4.setOnClickListener(mbc);

        mServiceHandler = new ServiceHandler();

        if (isNetworkBattle == true) {
//            Bmob.initialize(this, JumpApp.getContext().getApplicationID());
            mBattlePresenter = new BattlePresenter(GameActivity.this);
            mRoomList = new ArrayList<RoomInfo>();
        }

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        b.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mGameOverDialog = b.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMemberInfo();
    }

    private void updateMemberInfo() {
        L.d("updateMemberInfo isBattleStart="+isBattleStart);
        if (isNetworkBattle) {
            TextView myAlias, anotherAlias;
            ImageView myAvatar, anotherAvatar;
            if (mIsHomeUser) {
                myAlias = mHomeMemberAlias;
                myAvatar = mHomeMemberAvatar;
                anotherAlias = mAwayMemberAlias;
                anotherAvatar = mAwayMemberAvatar;
            } else {
                myAlias = mAwayMemberAlias;
                myAvatar = mAwayMemberAvatar;
                anotherAlias = mHomeMemberAlias;
                anotherAvatar = mHomeMemberAvatar;
            }

            String userAlias = mCurrentUser.getAlias();
            if (TextUtils.isEmpty(userAlias)) {
                myAlias.setText(mCurrentUser.getUsername());
            } else {
                myAlias.setText(userAlias);
            }
            FactoryInterface.setAvatar(this, mCurrentUser, myAvatar);
            if (isBattleStart) {
                String anotherUserAlias = mAnotherUser.getAlias();
                if (TextUtils.isEmpty(anotherUserAlias)) {
                    anotherAlias.setText(mAnotherUser.getUsername());
                } else {
                    anotherAlias.setText(anotherUserAlias);
                }
                FactoryInterface.setAvatar(this, mAnotherUser, anotherAvatar);
            } else {
                anotherAlias.setText(getResources().getString(R.string.empty));
            }
        } else {
            if (JumpApp.getContext().isVsComputer()) {
                mAwayMemberAlias.setText(getResources().getString(R.string.computer));
                mHomeMemberAlias.setText(getResources().getString(R.string.you));
//                FactoryInterface.setAvatar(this, mCurrentUser, mHomeMemberAvatar);
            } else {
                mAwayMemberAlias.setText(getResources().getString(R.string.man));
                mHomeMemberAlias.setText(getResources().getString(R.string.man));
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (isNetworkBattle == true) {
            if (mBattleMenuPop.isShowing()) {
                finish();
            } else {
                if (mExitConfirmDialog == null) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage(getResources().getString(R.string.press_yes_exit))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    // TODO Auto-generated method stub
                                    showWaittingDialog(getResources().getString(R.string.exiting), WAITTING_DIALOG_CATEGORY.WAITTING_EXIT);
                                    mPresenter.exit(mIsHomeUser);
                                }
                            })
                            .setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    // TODO Auto-generated method stub

                                }
                            })
                            .create();
                    mExitConfirmDialog = dialog;
                }
                mExitConfirmDialog.show();
            }
        } else {
            if (mExitConfirmDialog == null) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.press_yes_exit))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub
                                finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub

                            }
                        })
                        .create();
                mExitConfirmDialog = dialog;
            }
            mExitConfirmDialog.show();
        }
//        super.onBackPressed();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (isNetworkBattle == true) {
            if (mBattleMenuPop != null && mBattleMenuPop.isShowing()) {
                mBattleMenuPop.dismiss();
            } else {
                L.d("onDestroy isGameExit=" + isGameExit);
                if (isGameExit == false) {
                    isGameExit = true;
                    mPresenter.exit(mIsHomeUser);
                }
            }
            stopLoopQueryRoomJoin();
//            stopLoopQueryChessStep();
//            stopLoopQueryResetRoom();
        }
        mTimeLeave.stop();
        mTimeLeave.release();
        super.onDestroy();
    }

    private void init() {
        // TODO Auto-generated method stub
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screen_width = dm.widthPixels;
        SonBitmap = FactoryInterface.getBitmap(this, R.drawable.greenstar);
        son_width = SonBitmap.getWidth();
        // son_height = GreenBitmap.getHeight();

        int common_width = (screen_width - padding_width * 2 - son_width) / 4;

        width_list = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++) {
            width_list.add(padding_width + i * common_width);
        }

        height_list = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++) {
            height_list.add(padding_height + i * common_width);
        }
        mv.setScreenParam(width_list, height_list);
        mv.setSelectChess(selectedChess);
        mv.setChessRecordList(chessRecordList);
        mbgv.setScreenParam(width_list, height_list);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mbgv.invalidate();
        mv.invalidate();
        if (isNetworkBattle) {
            b4.setTextColor(Color.GRAY);
            initBattleMenuPop();
        } else {
            if (JumpApp.getContext().isVsComputer() == false) {
                b4.setTextColor(Color.GRAY);
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow();
        if (isNetworkBattle) {
            mBattleMenuPop.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mBattleMenuPop != null && mBattleMenuPop.isShowing() == true) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initBattleMenuPop() {
        // TODO Auto-generated method stub
        mBattleMenu = LayoutInflater.from(this).inflate(R.layout.battle_menu, mRelativeLayout, false);
        mBattleMenu.findViewById(R.id.create_room).setOnClickListener(mbc);
        mBattleMenu.findViewById(R.id.join_room).setOnClickListener(mbc);
//        mBattleMenu.setFocusable(true);
//        mBattleMenu.setFocusableInTouchMode(true);
        measureView(mBattleMenu);
        mBattleMenuPop = new PopupWindow(mBattleMenu,
                mBattleMenu.getMeasuredWidth(),
                mBattleMenu.getMeasuredHeight(), false);
//        mBattleMenuPop.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
//        mBattleMenuPop.setFocusable(false);
//        mBattleMenuPop.setOutsideTouchable(false);

        mCreateRoomDialog = new ProgressDialog(this);
        mCreateRoomDialog.setCancelable(false);
        mCreateRoomDialog.setMessage(getResources().getString(R.string.waitting_join));
        mCreateRoomDialog.setButton(-1, getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                stopLoopQueryRoomJoin();
                showWaittingDialog(getResources().getString(R.string.exiting), WAITTING_DIALOG_CATEGORY.WAITTING_DELETE);
                mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.DELETE_ROOM, MessageIDCommon.DELAY_TIME);
            }
        });

        mRoomSelectListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                showWaittingDialog(getResources().getString(R.string.connecting), WAITTING_DIALOG_CATEGORY.WAITTING_CONNECT);
                RoomInfo room = mRoomList.get(arg1);
                mPresenter.joinRoom(room.getObjectId());
            }
        };

        mRoomListAdapter = new RoomListAdapter();
        mJoinRoomDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.room_list))
                .setCancelable(false)
                .setAdapter(mRoomListAdapter, mRoomSelectListener)
                .setNegativeButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        mBattleMenuPop.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);
                    }
                })
                .create();

        mWaittingDialog = new ProgressDialog(this);
        mWaittingDialog.setCancelable(false);

        mResetRoomDialog = new AlertDialog.Builder(GameActivity.this)
                .setMessage(getResources().getString(R.string.agree_reset))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        mPresenter.resetRoom(2);
                        resetGameSuccess();
                    }
                })
                .setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        mPresenter.resetRoom(3);
                    }
                })
                .create();
    }

    private void resetGameSuccess() {
//        stopLoopQueryChessStep();
//        if (!mIsHomeUser) {
//            startLoopQueryChessStep();
//        }
        mv.Restart();
        mPresenter.initChessListener();
    }

    private void showWaittingDialog(String msg, WAITTING_DIALOG_CATEGORY category) {
        // TODO Auto-generated method stub
        if (mWaittingDialog != null && mWaittingDialog.isShowing() == false) {
            if (msg != null) {
                mWaittingDialog.setMessage(msg);
            } else {
                mWaittingDialog.setMessage("");
            }
            mWaittingDialog.show();
            Message m = mServiceHandler.obtainMessage(MessageIDCommon.MAIN_HANDLER_TIMER_30S);
            m.obj = category;
            mServiceHandler.sendMessageDelayed(m, 15000);
        }
    }

    private void closeWaittingDialog() {
        if (mWaittingDialog != null && mWaittingDialog.isShowing() == true) {
            mWaittingDialog.dismiss();
            mServiceHandler.removeMessages(MessageIDCommon.MAIN_HANDLER_TIMER_30S);
        }
    }

    private void resetGame() {
        if (mv.isComRuning() == true) {
            return;
        }
        if (isNetworkBattle == false) {
            mv.Restart();
        } else {
            showWaittingDialog(getResources().getString(R.string.waitting_confirm), WAITTING_DIALOG_CATEGORY.WAITTING_RESET);
            mPresenter.resetRoom(1);
        }
    }

    private void measureView(View v) {
        int childWidthSpec = 0;
        int childHeightSpec = 0;
        ViewGroup.LayoutParams params = v.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        if (params.width > 0) {
            childWidthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
        }
        if (params.height > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
        }
        v.measure(childWidthSpec, childHeightSpec);
    }

    private void createRoom() {
        // TODO Auto-generated method stub
        if (NetWorkUtil.isNetWorkAvailable(this)) {
            showCreateRoomPop();
            mPresenter.createRoom();
        } else {
            SnackbarUtil.showShortSnackbar(mRelativeLayout, "DATA DISCONNECTED!");
        }
    }

    private void showCreateRoomPop() {
        // TODO Auto-generated method stub
        if (mBattleMenuPop.isShowing()) {
            mBattleMenuPop.dismiss();
        }
        if (mCreateRoomDialog != null && mCreateRoomDialog.isShowing() == false) {
            mCreateRoomDialog.show();
        }
    }

    private void joinRoom() {
        // TODO Auto-generated method stub
        if (NetWorkUtil.isNetWorkAvailable(this)) {
            showJoinRoomPop();
            mPresenter.queryRoom();
        } else {
            SnackbarUtil.showShortSnackbar(mRelativeLayout, "DATA DISCONNECTED!");
        }
    }

    private void showJoinRoomPop() {
        // TODO Auto-generated method stub
        mRoomList.clear();
        if (mBattleMenuPop.isShowing()) {
            mBattleMenuPop.dismiss();
        }
        if (mJoinRoomDialog != null && mJoinRoomDialog.isShowing() == false) {
            mJoinRoomDialog.show();
        }
    }

    public void refreshCurrentChess(int dir) {
        if (1 == dir) { //GREEN
            mHomeMemberPane.setVisibility(View.INVISIBLE);
            mAwayMemberPane.setVisibility(View.VISIBLE);
        }
        if (2 == dir) { //RED
            mHomeMemberPane.setVisibility(View.VISIBLE);
            mAwayMemberPane.setVisibility(View.INVISIBLE);
        }
    }

    public void updateChessStep(String rec) {
        mCurrentChessStep = rec;
        mPresenter.updateChessStep(rec);
    }

    private void startLoopQueryRoomJoin() {
        mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.ROOM_JOIN_QUERY_UPDATE, MessageIDCommon.LOOP_TIME);
    }

    private void stopLoopQueryRoomJoin() {
        mServiceHandler.removeMessages(MessageIDCommon.ROOM_JOIN_QUERY_UPDATE);
    }

//    private void startLoopQueryChessStep() {
//        mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.CHESS_STEP_QUERY_UPDATE, MessageIDCommon.LOOP_TIME);
//    }
//
//    public void stopLoopQueryChessStep() {
//        mServiceHandler.removeMessages(MessageIDCommon.CHESS_STEP_QUERY_UPDATE);
//    }

    private void showGameOverDialog(String msg) {
        mGameOverDialog.setMessage(msg);
        mGameOverDialog.show();
    }

    @Override
    public void gameOver(String title) {
        if (mExitConfirmDialog != null && mExitConfirmDialog.isShowing()) {
            mExitConfirmDialog.dismiss();
        }
        Message msg = mServiceHandler.obtainMessage(MessageIDCommon.DIALOG_SHOW);
        msg.obj = title;
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public void opponentExit() {
        if (mIsHomeUser) {
            mAwayMemberAlias.setText("Empty");
            mAwayMemberAvatar.setImageDrawable(getDrawable(R.drawable.user));
        } else {
            mHomeMemberAlias.setText("Empty");
            mHomeMemberAvatar.setImageDrawable(getDrawable(R.drawable.user));
        }
        mTimeLeave.stop();
        mv.setDisableTouch(true);
        mPresenter.opponentExit();
    }

//    private void startLoopQueryResetRoom() {
//        mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.RESET_ROOM_QUERY_UPDATE, MessageIDCommon.LOOP_TIME);
//    }
//
//    public void stopLoopQueryResetRoom() {
//        mServiceHandler.removeMessages(MessageIDCommon.RESET_ROOM_QUERY_UPDATE);
//    }

    @Override
    public void timeout() {
        if (isNetworkBattle) {
            mPresenter.timeout();
        }
    }

    public enum CONNECT_OWNER {
        IDLE,
        SERVER,
        CLIENT
    }

    public enum WAITTING_DIALOG_CATEGORY {
        WAITTING_CONNECT,
        WAITTING_RESET,
        WAITTING_EXIT,
        WAITTING_DELETE
    }

    private class RoomListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mRoomList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub
            View v = arg1;
            if (v == null) {
                v = LayoutInflater.from(GameActivity.this).inflate(R.layout.battle_room_item, arg2, false);
            }
            TextView room_name = (TextView) v.findViewById(R.id.room_name);
            room_name.setText(mRoomList.get(arg0).getRoomName());
            if (mRoomList.get(arg0).getPlaying()) {
                TextView room_status = (TextView) v.findViewById(R.id.room_status);
                room_status.setText(getResources().getString(R.string.playing));
            }
            return v;
        }
    }

    protected class MainButtonClick implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                case R.id.button1: {
                    resetGame();
                    break;
                }

                case R.id.button2: {
                    Intent i = new Intent(GameActivity.this, SettingActivity.class);
                    if (isNetworkBattle) {
                        i.putExtra("NETWORK_BATTLE", true);
                    }
                    startActivity(i);
                    break;
                }

                case R.id.button3: {
                    Intent i = new Intent(GameActivity.this, HelpActivity.class);
                    startActivity(i);
                    break;
                }

                case R.id.button4: {
                    if (isNetworkBattle == false) {
                        if (false == mv.goBackStep()) {
                            SnackbarUtil.showShortSnackbar(mRelativeLayout, "Can not regret!");
                        }
                    }
                    break;
                }

                case R.id.create_room: {
                    createRoom();
                    break;
                }

                case R.id.join_room: {
                    joinRoom();
                    break;
                }
            }
        }
    }

    private class ServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MessageIDCommon.CREATE_ROOM_SUCCESS:
                    mPresenter.createRoomSuccess();
                    break;

                case MessageIDCommon.ROOM_JOIN_QUERY_UPDATE:
                    mPresenter.queryRoomJoin();
                    mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.ROOM_JOIN_QUERY_UPDATE, MessageIDCommon.LOOP_TIME);
                    break;

                case MessageIDCommon.ROOM_CAN_JOIN:
                    mPresenter.setRoomCanJoin();
                    break;

                case MessageIDCommon.RETURN_ROOM_NAME:
                    mRoomListAdapter.notifyDataSetChanged();
                    break;

                case MessageIDCommon.JOIN_ROOM_SUCCESS:
                    if (mIsHomeUser) {
                        if (mCreateRoomDialog.isShowing() == true) {
                            mCreateRoomDialog.dismiss();
                        }
                        mv.setConnectOwner(CONNECT_OWNER.SERVER);
                        mTimeLeave.start();
                    } else {
                        closeWaittingDialog();
                        mv.setConnectOwner(CONNECT_OWNER.CLIENT);
                    }
                    mPresenter.start();
                    break;

                case MessageIDCommon.CHESS_STEP_UPDATE:
                    String chessStep = (String) msg.obj;
                    mv.parseChessStep(chessStep);
                    break;

                case MessageIDCommon.CHESS_STEP_UPDATE_RESULT: {
//                    boolean result = (boolean) msg.obj;
//                    if (result == true) {
////                        startLoopQueryChessStep();
//                    } else {
                        mPresenter.updateChessStep(mCurrentChessStep);
//                    }
                }
                break;

                case MessageIDCommon.RESET_ROOM_RESULT: {
                    boolean result = (boolean) msg.obj;
                    if (result == true) {
//                        startLoopQueryResetRoom();
                    } else {
                        mPresenter.resetRoom(1);
                    }
                }
                break;

                case MessageIDCommon.RESET_ROOM_CONFIRM_RESULT: {
                    int result = (int) msg.obj;
                    if (mWaittingDialog.isShowing() == true) {
                        switch (result) {
                            case 2:
                                closeWaittingDialog();
                                resetGameSuccess();
//                                mPresenter.resetRoom(0);
                                break;
                            case 3:
                                closeWaittingDialog();
                                break;
                            default:
                                break;
                        }
                    } else {
                        switch (result) {
                            case 1:
                                mResetRoomDialog.show();
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;

//                case MessageIDCommon.CHESS_STEP_QUERY_UPDATE:
//                    mPresenter.queryChessStep();
//                    mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.CHESS_STEP_QUERY_UPDATE, MessageIDCommon.LOOP_TIME);
//                    break;

//                case MessageIDCommon.RESET_ROOM_QUERY_UPDATE:
//                    mPresenter.queryResetRoom();
//                    mServiceHandler.sendEmptyMessageDelayed(MessageIDCommon.RESET_ROOM_QUERY_UPDATE, MessageIDCommon.LOOP_TIME);
//                    break;

                case MessageIDCommon.MAIN_HANDLER_TIMER_30S:
                    WAITTING_DIALOG_CATEGORY category = (WAITTING_DIALOG_CATEGORY) msg.obj;
                    SnackbarUtil.showShortSnackbar(mRelativeLayout, "CONNECT TIMEOUT!");
                    if (category == WAITTING_DIALOG_CATEGORY.WAITTING_CONNECT) {
                        joinRoomFail();
                    } else if (category == WAITTING_DIALOG_CATEGORY.WAITTING_RESET) {
                        //TODO do nothing
                    } else if (category == WAITTING_DIALOG_CATEGORY.WAITTING_EXIT) {
                        exitGame();
                    } else if (category == WAITTING_DIALOG_CATEGORY.WAITTING_DELETE) {
                        closeWaittingDialog();
                    }
                    break;

                case MessageIDCommon.DELETE_ROOM:
                    mPresenter.deleteRoom();
                    break;

                case MessageIDCommon.DIALOG_SHOW:
                    String title = (String) msg.obj;
                    showGameOverDialog(title);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
