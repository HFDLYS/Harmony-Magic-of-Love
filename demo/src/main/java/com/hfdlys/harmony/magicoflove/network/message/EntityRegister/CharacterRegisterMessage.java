package com.hfdlys.harmony.magicoflove.network.message.EntityRegister;

import com.hfdlys.harmony.magicoflove.network.message.ControlMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterRegisterMessage extends EntityRegisterMessage {
    private int userId;

    public CharacterRegisterMessage() {
    }

    public CharacterRegisterMessage(int type, int userId, ControlMessage control) {
        super(type);
        this.userId = userId;
    }
}
