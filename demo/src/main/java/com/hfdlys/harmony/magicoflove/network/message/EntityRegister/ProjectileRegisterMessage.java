package com.hfdlys.harmony.magicoflove.network.message.EntityRegister;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectileRegisterMessage extends EntityRegisterMessage {
    int ox;

    int oy;

    int senderId;

    public ProjectileRegisterMessage() {
    }

    public ProjectileRegisterMessage(int type, int ox, int oy, int senderId) {
        super(type);
        this.ox = ox;
        this.oy = oy;
        this.senderId = senderId;
    }

}
