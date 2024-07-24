package com.hfdlys.harmony.magicoflove.network.message;

import lombok.Data;

@Data
public class RegisterMessage {
    private String username;
    private String password;
    private String email;
    private byte[] skin;

    public RegisterMessage() {
    }

    public RegisterMessage(String username, String password, String email, byte[] skin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.skin = skin;
    }
}
