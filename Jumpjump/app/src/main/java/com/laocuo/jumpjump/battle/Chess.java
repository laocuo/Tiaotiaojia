package com.laocuo.jumpjump.battle;

import cn.bmob.v3.BmobObject;

/**
 * Created by hoperun on 12/27/16.
 */

public class Chess extends BmobObject {
    private String homeUser; //RED
    private String awayUser; //GREEN
    private String currentUser;
    private String chessStep;
    private int resetConfirm; //0:idle,1:waitting,2:agree,3:disagree

    public String getHomeUser() {
        return homeUser;
    }

    public void setHomeUser(String homeUser) {
        this.homeUser = homeUser;
    }

    public String getAwayUser() {
        return awayUser;
    }

    public void setAwayUser(String awayUser) {
        this.awayUser = awayUser;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getChessStep() {
        return chessStep;
    }

    public void setChessStep(String chessStep) {
        this.chessStep = chessStep;
    }

    public int getResetConfirm() {
        return resetConfirm;
    }

    public void setResetConfirm(int resetConfirm) {
        this.resetConfirm = resetConfirm;
    }
}
