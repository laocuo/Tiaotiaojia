package com.laocuo.jumpjump.chat;

import cn.bmob.v3.BmobObject;

public class Chat extends BmobObject {
    private String name;
    private String username;
    private String content;
    private String avatar;
    private String time;
    private String userObjectId;

    public Chat(String name, String content, String username){
        this.name = name;
        this.content = content;
        this.username = username;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getUserObjectId() {
        return userObjectId;
    }
    public void setUserObjectId(String userObjectId) {
        this.userObjectId = userObjectId;
    }
}