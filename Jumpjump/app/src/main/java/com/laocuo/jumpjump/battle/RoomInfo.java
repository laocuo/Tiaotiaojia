package com.laocuo.jumpjump.battle;

/**
 * Created by hoperun on 12/29/16.
 */

public class RoomInfo {
    private String objectId;
    private String roomId; //Chess.objectId
    private String roomName;
    private String homeUser; //RED
    private String awayUser; //GREEN
    private Boolean isPlaying;
    private Boolean canJoin;

    public RoomInfo() {
        clearInfo();
    }

    public RoomInfo(Room r) {
        this.objectId = r.getObjectId();
        this.roomId = r.getRoomId();
        this.roomName = r.getRoomName();
        this.homeUser = r.getHomeUser();
        this.awayUser = r.getAwayUser();
        this.isPlaying = r.getPlaying();
        this.canJoin = r.getCanJoin();
    }

    public void clearInfo() {
        this.objectId = "";
        this.roomId = "";
        this.roomName = "";
        this.homeUser = "";
        this.awayUser = "";
        this.isPlaying = false;
        this.canJoin = false;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    public Boolean getCanJoin() {
        return canJoin;
    }

    public void setCanJoin(Boolean canJoin) {
        this.canJoin = canJoin;
    }
}
