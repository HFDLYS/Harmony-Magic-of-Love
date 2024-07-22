package com.hfdlys.harmony.magicoflove.network.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.EntityRegisterMessage;

import lombok.Data;

@Data
public class EntityManagerMessage {
    /**
     * 实体注册信息
     */
    private HashMap<Integer, EntityRegisterMessage> entityRegisterMessages;

    /**
     * 实体信息
     */
    private HashMap<Integer, EntityMessage> entityMessageHashMap;

    public EntityManagerMessage() {
    }

    public EntityManagerMessage(HashMap<Integer, EntityRegisterMessage> entityRegisterMessages, HashMap<Integer, EntityMessage> entityMessageHashMap) {
        this.entityRegisterMessages = entityRegisterMessages;
        this.entityMessageHashMap = entityMessageHashMap;
    }
}
