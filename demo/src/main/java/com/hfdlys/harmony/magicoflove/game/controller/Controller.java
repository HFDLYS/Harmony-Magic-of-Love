package com.hfdlys.harmony.magicoflove.game.controller;

import com.hfdlys.harmony.magicoflove.network.protoc.ControlMessage;
import com.hfdlys.harmony.magicoflove.game.entity.Character;

import lombok.Data;

/**
 * <p>控制器类</p>
 * <p>用于处理游戏逻辑</p>
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
@Data
public abstract class Controller {
    /**
     * 控制包
     */
    private ControlMessage control = new ControlMessage();

    /**
     * 控制
     */
    public void control(Character character) {
    }
}
