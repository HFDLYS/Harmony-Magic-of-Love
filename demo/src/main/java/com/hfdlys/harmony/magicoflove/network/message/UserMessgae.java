package com.hfdlys.harmony.magicoflove.network.message;

import com.hfdlys.harmony.magicoflove.database.entity.User;

import lombok.Data;

@Data
public class UserMessgae {
    private Integer userId;

    private String username;

    private byte[] skin;

    public UserMessgae() {
    }

    public UserMessgae(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.skin = user.getSkin();
    }

    @Override
    public String toString() {
        return "用户名：" + username;
    }
}
