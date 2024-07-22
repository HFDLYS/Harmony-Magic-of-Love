package com.hfdlys.harmony.magicoflove.game.factory;

import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;
import com.hfdlys.harmony.magicoflove.game.entity.EntityManager;
import com.hfdlys.harmony.magicoflove.game.entity.Projectile;
import com.hfdlys.harmony.magicoflove.game.entity.weapon.Weapon;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.ProjectileRegisterMessage;
import com.hfdlys.harmony.magicoflove.util.ImageUtil;

import java.io.IOException;

/**
 * 武器工厂类
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
public class WeaponFactory {
    /**
     * 爱之剑
     */
    public static final int LOVE_SWORD = 1;

    /**
     * 爱之杖
     */
    public static final int LOVE_STAVES = 2;

    /**
     * 混沌杖
     */
    public static final int CHAOS_STAVES = 3;

    /**
     * 获取武器原型的一份复制
     * @param type 武器编号(常量)
     * @return 武器原型的一份复制
     */
    public static Weapon getWeapon(int type, GameManager gameManager) {
        try {
            if(type == LOVE_SWORD) {
                Projectile projectile = ProjectileFactory.getProjectile(ProjectileFactory.SWORD_QI, 0, 0, 0);
                Texture texture = new Texture("weapon/love_sword.png", 32, 32, 16, 16);
                Texture texture_Right = new Texture(ImageUtil.rotate(texture.getImage(), -90), 32, 32, 16, 16);
                Texture texture_Left = new Texture(ImageUtil.rotate(texture.getImage(), 90), 32, 32, 16, 16);
                Texture[] textures = {texture_Right, texture_Left};
                Weapon weapon = new Weapon(
                        type,
                        gameManager,
                        projectile, 1440 / gameManager.getFps(), 50, 7200 / gameManager.getFps(), 12, ProjectileFactory.RAMDOM,
                        textures,
                        ProjectileFactory.SWORD_QI
                );
                weapon.setTag("Love Sword");
                return weapon;
            } else if (type == LOVE_STAVES) {
                Projectile projectile = ProjectileFactory.getProjectile(ProjectileFactory.HEART, 0, 0, 0);
                Texture texture = new Texture("weapon/love_staves.png", 32, 32, 16, 16);
                Texture texture_Right = new Texture(ImageUtil.rotate(texture.getImage(), -90), 32, 32, 16, 16);
                Texture texture_Left = new Texture(ImageUtil.rotate(texture.getImage(), 90), 32, 32, 16, 16);
                Texture[] textures = {texture_Right, texture_Left};
                Weapon weapon = new Weapon(
                        type,
                        gameManager,
                        projectile, 360 / gameManager.getFps(), 20, 7200 / gameManager.getFps(), 200, ProjectileFactory.HEART,
                        textures,
                        ProjectileFactory.HEART
                );
                weapon.setTag("Love Staves");
                return weapon;
            } else if (type == CHAOS_STAVES) {
                Projectile projectile = ProjectileFactory.getProjectile(ProjectileFactory.MUSIC_NOTE, 0, 0, 0);
                Texture texture = new Texture("weapon/chaos_staves.png", 32, 32, 16, 16);
                Texture texture_Right = new Texture(ImageUtil.rotate(texture.getImage(), -90), 32, 32, 16, 16);
                Texture texture_Left = new Texture(ImageUtil.rotate(texture.getImage(), 90), 32, 32, 16, 16);
                Texture[] textures = {texture_Right, texture_Left};
                Weapon weapon = new Weapon(
                        type,
                        gameManager,
                        projectile, 360 / gameManager.getFps(), 5, 1440 / gameManager.getFps(), 500, ProjectileFactory.RAMDOM,
                        textures,
                        ProjectileFactory.RAMDOM
                ) {
                    @Override
                    public boolean attack(int senderID, int x, int y, int aimX, int aimY) {
                    if(gameManager.getTimeStamp() - getLastShootTimeStamp() <= getInterval()) return false;
                        setLastShootTimeStamp(gameManager.getTimeStamp());
                        Projectile newProjectile = ProjectileFactory.getProjectile(ProjectileFactory.RAMDOM, senderID, x, y);
                        newProjectile.setDamage(getDamage());
                        newProjectile.setRange(getRange());
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
                        newProjectile.setOx(x);
                        newProjectile.setOy(y);

                        newProjectile.setHitbox(new Hitbox(x, y, 8));
                        newProjectile.getHitbox().setVelocity(
                                (int)Math.ceil(1.0 * getVelocity() * dx / len),
                                (int)Math.ceil(1.0 * getVelocity() * dy / len));
                        
                        gameManager.getEntityManager().add(newProjectile, new ProjectileRegisterMessage(type, x, y, senderID));
                        return true;
                    }
                };
                weapon.setTag("Chaos Staves");
                return weapon;
            }
        } catch (IOException e) {
            System.out.println("武器生成时异常!");
            e.getStackTrace();
        }
        return null;
    }

}