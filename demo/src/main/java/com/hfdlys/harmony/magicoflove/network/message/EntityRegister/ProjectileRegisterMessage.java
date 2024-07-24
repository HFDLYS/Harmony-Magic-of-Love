package com.hfdlys.harmony.magicoflove.network.message.EntityRegister;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 投射物注册信息
 * @author Jiasheng Wang
 * @since 2024-07-21
 */
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
