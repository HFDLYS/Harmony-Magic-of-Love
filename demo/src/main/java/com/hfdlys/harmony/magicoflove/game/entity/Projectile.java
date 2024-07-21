package com.hfdlys.harmony.magicoflove.game.entity;

import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>弹射物类</p>
 * <p>用于处理弹射物</p>
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Projectile extends Entity {
    /**
     * 子弹的伤害
     */
    private int damage;

    /**
     * 发送者编号
     * （若碰撞至发射者，则直接跳过）
     */
    private int senderID;

    /**
     * 射程
     */
    private int range;

    /**
     * 原始x
     */
    private int ox;

    /**
     * 原始y
     */
    private int oy;

    /**
     * 构造一个发送者编号的弹射物
     * @param hitbox 碰撞箱
     * @param texture 贴图
     * @param damage 伤害
     * @param senderID 发送者编号
     */
    public Projectile(Hitbox hitbox, Texture texture, int damage, int senderID, int range) {
        super(hitbox, texture, damage); // 伤害越大，hp越多
        this.damage = damage;
        this.senderID = senderID;
        this.range = range;
    }

    /**
     * 深拷贝弹射物
     */
    public Projectile(Projectile projectile) {
        super(projectile.getHitbox(), projectile.getTexture(), projectile.getHp());
        this.damage = projectile.getDamage();
        this.senderID = projectile.getSenderID();
        this.range = projectile.getRange();
    }
}
