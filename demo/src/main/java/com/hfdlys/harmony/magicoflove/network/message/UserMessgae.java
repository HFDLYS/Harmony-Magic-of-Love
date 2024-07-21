package com.hfdlys.harmony.magicoflove.network.message;

import lombok.Data;

@Data
public class UserMessgae {
    private Integer userId;

    private String userName;

    private byte[] skin;
}
