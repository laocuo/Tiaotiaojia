package com.laocuo.jumpjump.login;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by hoperun on 11/25/16.
 */

public class JumpUser extends BmobUser {
    private String alias;
    private BmobFile avatar;
    private Boolean canChat;

    public Boolean getCanChat() {
        return canChat;
    }

    public void setCanChat(Boolean canChat) {
        this.canChat = canChat;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }
}
