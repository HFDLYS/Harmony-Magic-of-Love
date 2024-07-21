package com.hfdlys.harmony.magicoflove.game.factory;

import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;
import com.hfdlys.harmony.magicoflove.game.entity.EntityManager;
import com.hfdlys.harmony.magicoflove.game.entity.Projectile;
import com.hfdlys.harmony.magicoflove.game.entity.weapon.Weapon;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.util.ImageUtil;

import java.io.IOException;

/**
 * 武器工厂类
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
public class WeaponFactory {
    /**
     * 
     */
    public static final int LOVE_SWORD = 0;

    /**
     * 
     */
    public static final int LOVE_STAVES = 1;

    /** */
    public static final int CHAOS_STAVES = 2;

    /**
     * 获取武器原型的一份复制
     * @param type 武器编号(常量)
     * @return 武器原型的一份复制
     */
    public static Weapon getWeapon(int type) {
        try {
            if(type == LOVE_SWORD) {
                Projectile projectile = ProjectileFactory.getProjectile(ProjectileFactory.RAMDOM);
                Texture texture = new Texture("weapon/love_sword.png", 32, 32, 16, 16);
                Texture texture_Right = new Texture(ImageUtil.rotate(texture.getImage(), -90), 32, 32, 16, 16);
                Texture texture_Left = new Texture(ImageUtil.rotate(texture.getImage(), 90), 32, 32, 16, 16);
                Texture[] textures = {texture_Right, texture_Left};
                Weapon weapon = new Weapon(
                        projectile, 1440 / GameManager.getInstance().getFps(), 1, 7200 / GameManager.getInstance().getFps(), 64,
                        textures,
                        ProjectileFactory.RAMDOM
                );
                weapon.setTag("Pistol");
                return weapon;
            } else if (type == LOVE_STAVES) {
                Projectile projectile = ProjectileFactory.getProjectile(ProjectileFactory.HEART);
                Texture texture = new Texture("weapon/love_staves.png", 32, 32, 16, 16);
                Texture texture_Right = new Texture(ImageUtil.rotate(texture.getImage(), -90), 32, 32, 16, 16);
                Texture texture_Left = new Texture(ImageUtil.rotate(texture.getImage(), 90), 32, 32, 16, 16);
                Texture[] textures = {texture_Right, texture_Left};
                Weapon weapon = new Weapon(
                        projectile, 360 / GameManager.getInstance().getFps(), 1, 7200 / GameManager.getInstance().getFps(), 200,
                        textures,
                        ProjectileFactory.HEART
                );
                weapon.setTag("Love Staves");
                return weapon;
            } else if (type == CHAOS_STAVES) {
                Projectile projectile = ProjectileFactory.getProjectile(ProjectileFactory.MUSIC_NOTE);
                Texture texture = new Texture("weapon/chaos_staves.png", 32, 32, 16, 16);
                Texture texture_Right = new Texture(ImageUtil.rotate(texture.getImage(), -90), 32, 32, 16, 16);
                Texture texture_Left = new Texture(ImageUtil.rotate(texture.getImage(), 90), 32, 32, 16, 16);
                Texture[] textures = {texture_Right, texture_Left};
                Weapon weapon = new Weapon(
                        projectile, 360 / GameManager.getInstance().getFps(), 1, 1440 / GameManager.getInstance().getFps(), 500,
                        textures,
                        ProjectileFactory.RAMDOM
                ) {
                    @Override
                    public boolean attack(int senderID, int x, int y, int aimX, int aimY) {
                    if(GameManager.getInstance().getTimeStamp() - getLastShootTimeStamp() <= getInterval()) return false;
                        setLastShootTimeStamp(GameManager.getInstance().getTimeStamp());
                        Projectile newProjectile = ProjectileFactory.getProjectile(ProjectileFactory.RAMDOM);
                        newProjectile.setSenderID(senderID);
                        newProjectile.setDamage(getDamage());
                        newProjectile.setOx(x);
                        newProjectile.setOy(y);
                        newProjectile.setRange(getRange());
                        int dx = aimX - x;
                        int dy = aimY - y;
                        int len = (int)Math.sqrt(dx * dx + dy * dy);

                        newProjectile.setHitbox(new Hitbox(x, y, 8));
                        newProjectile.getHitbox().setVelocity(
                                (int)Math.ceil(1.0 * getVelocity() * dx / len),
                                (int)Math.ceil(1.0 * getVelocity() * dy / len));
                        
                        EntityManager.getInstance().addWithoutMessage(newProjectile);
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