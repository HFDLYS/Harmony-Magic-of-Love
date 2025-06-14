package com.hfdlys.harmony.magicoflove.network.message.EntityRegister;

import com.hfdlys.harmony.magicoflove.network.message.ControlMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色注册信息
 * @author Jiasheng Wang
 * @since 2024-07-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterRegisterMessage extends EntityRegisterMessage {
    private int userId;

    private String username;

    private int weaponType;

    public CharacterRegisterMessage() {
    }

    public CharacterRegisterMessage(int type, int userId, String username, int weaponType) {
        super(type);
        this.userId = userId;
        this.username = username;
        this.weaponType = weaponType;
    }
}
