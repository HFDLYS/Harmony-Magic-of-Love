package com.hfdlys.harmony.magicoflove.network.message;

import lombok.Data;

/**
 * 登录信息
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
@Data
public class LoginMessage {
    public String username;
    public String password;
}
