package com.hfdlys.harmony.magicoflove.network.message.EntityRegister;

import com.hfdlys.harmony.magicoflove.network.message.ControlMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterRegisterMessage extends EntityRegisterMessage {
    private int userId;

    private int weaponType;

    public CharacterRegisterMessage() {
    }

    public CharacterRegisterMessage(int type, int userId, int weaponType) {
        super(type);
        this.userId = userId;
        this.weaponType = weaponType;
    }
}
