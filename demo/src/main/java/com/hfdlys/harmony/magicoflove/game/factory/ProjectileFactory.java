package com.hfdlys.harmony.magicoflove.game.factory;

import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;
import com.hfdlys.harmony.magicoflove.game.entity.Entity;
import com.hfdlys.harmony.magicoflove.game.entity.Projectile;

public class ProjectileFactory {
    /**
     * 获取弹射物
     * @param tx 纹理x
     * @param ty 纹理y
     * @param damage 伤害
     * @param senderID 发送者ID
     * @param range 射程
     * @return 弹射物
     */
    public static Projectile getProjectile(int tx, int ty, int damage, int senderID, int range) {
        try {
            Texture texture_all = new Texture("weapon/projectile.png", 48, 160, 16, 16);
            return new Projectile(
                new Hitbox(0, 0, 0, 0, 4),
                texture_all.getCutTexture(tx * 16, ty * 16, 16, 16, 8, 8),
                damage, 
                senderID,
                range);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 
     */
    public static final int RAMDOM = -1;

    /**
     * 
     */
    public static final int BROKEN_HEART = 1;

    /**
     *
     */
    public static final int HEART = 2;

    /**
     * 
     */
    public static final int DOUBLE_HEART = 3;

    /**
     * S
     */
    public static final int MUSIC_NOTE = 4;

    /** */
    public static final int SWORD_QI = 5;

    /**
     * 
     * @param type
     * @return
     */
    public static Projectile getProjectile(int type, int senderId, int ox, int oy) {
        try {
            Texture texture_all = new Texture("weapon/projectile.png", 48, 160, 16, 16);
            switch (type) {
                case BROKEN_HEART:
                    return new Projectile(
                        new Hitbox(0, 0, 0, 0, 4),
                        texture_all.getCutTexture(0 * 16, 4 * 16, 16, 16, 8, 8),
                        20,
                        senderId,
                        20,
                        ox,
                        oy);
                case HEART:
                    return new Projectile(
                        new Hitbox(0, 0, 0, 0, 4),
                        texture_all.getCutTexture(0 * 16, 5 * 16, 16, 16, 8, 8),
                        10,
                        senderId,
                        20,
                        ox,
                        oy);
                case SWORD_QI:
                    return new Projectile(
                        new Hitbox(0, 0, 0, 0, 8),
                        null,
                        50,
                        senderId,
                        4,
                        ox,
                        oy);
                default:
                    int x = (int)((Math.random() * 2) + 0.5);
                    int y = (int)((Math.random() * 9) + 0.5);
                    return new Projectile(
                        new Hitbox(0, 0, 0, 0, 4),
                        texture_all.getCutTexture(x * 16, y * 16, 16, 16, 8, 8),
                        10,
                        senderId,
                        20,
                        ox,
                        oy);
            }
                
        } catch (Exception e) {
            return null;
        }
    }
}
