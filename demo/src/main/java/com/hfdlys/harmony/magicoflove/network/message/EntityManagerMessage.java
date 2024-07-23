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

    /**
     * 阵营信息
     */
    private HashMap<Integer, Integer> entityCamp;

    public EntityManagerMessage() {
    }

    public EntityManagerMessage(HashMap<Integer, EntityRegisterMessage> entityRegisterMessages, HashMap<Integer, EntityMessage> entityMessageHashMap, HashMap<Integer, Integer> entityCamp) {
        this.entityRegisterMessages = entityRegisterMessages;
        this.entityMessageHashMap = entityMessageHashMap;
        this.entityCamp = entityCamp;
    }
}
