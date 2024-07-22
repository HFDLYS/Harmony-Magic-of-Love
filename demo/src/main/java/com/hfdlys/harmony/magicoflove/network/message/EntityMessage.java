package com.hfdlys.harmony.magicoflove.network.message;

import lombok.Data;

@Data
public class EntityMessage {
    /**
     * id
     */
    private int id;

    /**
     * 血量
     */
    private int hp;

    /**
     * x轴坐标
     */
    private int x;

    /**
     * y轴坐标
     */
    private int y;

    /**
     * x轴速度
     */
    private int vx;

    /**
     * y轴速度
     */
    private int vy;

    /**
     * x轴长度
     */
    private int lx;

    /**
     * y轴长度
     */
    private int ly;

    /**
     * 瞄准
     */
    private int aimX;

    /**
     * 瞄准
     */
    private int aimY;

    /**
     * 武器类型
     */
    private int weaponType;

    public EntityMessage() {
    }

    public EntityMessage(int id, int hp, int x, int y, int vx, int vy, int lx, int ly) {
        this.id = id;
        this.hp = hp;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.lx = lx;
        this.ly = ly;
        weaponType = 0;
    }
}
