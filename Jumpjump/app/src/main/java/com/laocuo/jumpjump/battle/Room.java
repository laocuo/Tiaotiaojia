package com.laocuo.jumpjump.battle;


import cn.bmob.v3.BmobObject;

/**
 * Created by hoperun on 12/27/16.
 */

public class Room extends BmobObject {
    private String roomId; //Chess.objectId
    private String roomName;
    private String homeUser; //RED
    private String awayUser; //GREEN
    private Boolean isPlaying;
    private Boolean canJoin;

    public Boolean getCanJoin() {
        return canJoin;
    }

    public void setCanJoin(Boolean canJoin) {
        this.canJoin = canJoin;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

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

    public Boolean getPlaying() {
        return isPlaying;
    }

    public void setPlaying(Boolean playing) {
        isPlaying = playing;
    }
}
