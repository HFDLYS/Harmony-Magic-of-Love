package com.hfdlys.harmony.magicoflove.game.entity;

import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;

/**
 * <p>障碍物类</p>
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
public class Obstacle extends Entity {
    /**
     * 构造一个障碍物
     * @param hitbox 碰撞箱
     * @param texture 贴图
     * @param hp 障碍物血量
     */
    public Obstacle(Hitbox hitbox, Texture texture, int hp) {
        super(hitbox, texture, hp);
    }
}

