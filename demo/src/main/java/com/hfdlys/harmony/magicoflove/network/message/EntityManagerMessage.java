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
    private List<EntityRegisterMessage> entityRegisterMessages = new ArrayList<>();

    /**
     * 实体信息
     */
    private HashMap<Integer, EntityMessage> entityMessageHashMap;

    public EntityManagerMessage() {
    }

    public EntityManagerMessage(List<EntityRegisterMessage> entityRegisterMessages, HashMap<Integer, EntityMessage> entityMessageHashMap) {
        this.entityRegisterMessages = entityRegisterMessages;
        this.entityMessageHashMap = entityMessageHashMap;
    }
}
