package com.hfdlys.harmony.magicoflove.network.message.EntityRegister;



import lombok.Data;

@Data
public class EntityRegisterMessage {
    /**
     * ID
     */
    private int id;

    /**
     * 类型
     */
    private int type;

    public EntityRegisterMessage() {
    }

    public EntityRegisterMessage(int type) {
        this.type = type;
    }


}
