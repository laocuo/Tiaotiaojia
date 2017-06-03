package com.laocuo.jumpjump.battle;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/4/16.
 */

public class Command extends BmobObject {
    private String roomId;
    private String chessStep;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getChessStep() {
        return chessStep;
    }

    public void setChessStep(String chessStep) {
        this.chessStep = chessStep;
    }
}
