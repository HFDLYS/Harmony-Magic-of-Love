package com.hfdlys.harmony.magicoflove.game.entity;

import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;

import lombok.Data;

/**
 * <p>
 * 实体类
 * 所有实体的抽象类
 * </p>
 * @author Jiasheng Wang
 */
@Data
abstract public class Entity {
    /**
     * 实体ID
     */
    private int id;

    /**
     * 碰撞箱
     */
    private Hitbox hitbox;

    /**
     * 贴图
     */
    private Texture texture;

    /**
     * 血量
     */
    private int hp;
    
    /**
     * 构造函数
     * @param texture 贴图
     * @param hp 血量
     */
    public Entity(Hitbox hitbox, Texture texture, int hp) {
        this.hitbox = hitbox;
        this.texture = texture;
        this.hp = hp;
    }

    /**
     * 复制构造函数
     * @param another 另一个实体
     */
    public Entity(Entity another) {
        this(another.getHitbox(), (another.getTexture() != null)?(new Texture(another.getTexture())):(null), another.getHp());
    }

    /**
     * 增加血量
     * @param hp 血量
     */
    public void healHp(int hp) {
        this.hp += hp;
    }

    /**
     * 减少血量
     * @param hp 血量
     */
    public void reduceHp(int hp) {
        this.hp -= hp;
    }

    /**
     * 判断实体是否存在
     * @return 若存在，返回true；否则，返回false。
     */
    public boolean isExist() {
        return hp > 0;
    }
}
