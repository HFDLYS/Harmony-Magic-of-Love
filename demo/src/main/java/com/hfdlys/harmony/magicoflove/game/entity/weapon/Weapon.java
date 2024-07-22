package com.hfdlys.harmony.magicoflove.game.entity.weapon;

import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;
import com.hfdlys.harmony.magicoflove.game.entity.EntityManager;
import com.hfdlys.harmony.magicoflove.game.entity.Projectile;
import com.hfdlys.harmony.magicoflove.game.factory.ProjectileFactory;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.ProjectileRegisterMessage;

import lombok.Data;

/**
 * 武器类
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
@Data
public class Weapon {
    /**
     * 武器type
     */
    private int type;
    /**
     * 武器Tag
     */
    private String Tag;

    /**
     * 发出的攻击
     */
    private Projectile projectile;

    /**
     * 武器发射的攻击速度（速率）
     */
    private int velocity;

    /**
     * 武器发射的攻击伤害
     */
    private int damage;

    /**
     * 武器射击间隔（射速）
     */
    private int interval;

    /**
     * 射程
     */
    private int range;

    /**
     * 抛射物类型
     */
    private int projectileType;

    /**
     * 武器纹理
     */
    private Texture[] texture;

    /**
     * 射击音效編號
     */
    private int shootSoundEffectType;

    /**
     * 游戏管理器
     */
    private GameManager gameManager;

    /**
     * 构造一把武器
     * @param projectile 攻击
     * @param velocity 速度
     * @param damage 伤害
     * @param interval 射击间隔
     * @param range 射程
     * @param textures 纹理
     */
    public Weapon(int type, GameManager gameManager, Projectile projectile, int velocity, int damage, int interval, int range, int projectileType, Texture[] textures, int shootSoundEffectType) {
        this.projectile = projectile;
        this.type = type;
        this.gameManager = gameManager;
        this.velocity = velocity;
        this.damage = damage;
        this.range = range;
        this.projectileType = projectileType;
        this.interval = interval;
        this.texture = textures;
        this.shootSoundEffectType = shootSoundEffectType;
    }

    /**
     * deep copy constructor
     */
    public Weapon(Weapon another) {
        this(another.type, another.gameManager, new Projectile(another.projectile), another.velocity, another.damage, another.interval, another.range, another.projectileType, another.texture, another.shootSoundEffectType);
    }

    /**
     * 获取武器中攻击的一份拷贝
     * @return 攻击的一份拷贝
     */
    public Projectile getNewProjectile() {
        return new Projectile(projectile);
    }


    /**
     * 上一次射击的帧数
     */
    private long lastShootTimeStamp;


    /**
     * 获取纹理
     * 0 Left
     * 1 Right
     * @param index 纹理编号
     * @return 对应纹理
     */
    public Texture getTextures(int index) {
        if (index < 0 || index >= texture.length) return null;
        return texture[index];
    }

    /**
     * 抽象方法：攻击
     * @param senderID 攻击者ID
     * @param x 攻击者x轴坐标
     * @param y 攻击者y轴坐标
     * @param aimX 瞄准坐标x
     * @param aimY 瞄准坐标y
     * @return true if shoot
     */
    public boolean attack(int senderID, int x, int y, int aimX, int aimY) {
        if(gameManager.getTimeStamp() - lastShootTimeStamp <= interval) return false;
        lastShootTimeStamp = gameManager.getTimeStamp();
        Projectile newProjectile = getNewProjectile();
        x += 12;
        int dx = aimX - x;
        int dy = aimY - y;
        if (dy > 0) {
            y += 16;
        } else {
            y -= 16;
        }
        dy = aimY - y;
        int len = (int)Math.sqrt(dx * dx + dy * dy);

        
        newProjectile.setSenderID(senderID);
        newProjectile.setDamage(damage);
        newProjectile.setOx(x);
        newProjectile.setOy(y);
        newProjectile.setRange(range);

        newProjectile.setHitbox(new Hitbox(x, y, 8));
        newProjectile.getHitbox().setVelocity(
                (int)Math.ceil(1.0 * velocity * dx / len),
                (int)Math.ceil(1.0 * velocity * dy / len));
        
        gameManager.getEntityManager().add(newProjectile, new ProjectileRegisterMessage(projectileType, x, y, senderID));
        return true;
    }
}
