package com.hfdlys.harmony.magicoflove.game.entity;

import com.hfdlys.harmony.magicoflove.game.common.Animation;
import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;
import com.hfdlys.harmony.magicoflove.game.controller.Controller;
import com.hfdlys.harmony.magicoflove.game.entity.weapon.Weapon;
import com.hfdlys.harmony.magicoflove.game.factory.ProjectileFactory;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.util.ImageUtil;

import java.awt.image.BufferedImage;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Character extends Entity {
    /**
     * 最大血量
     */
    private int maxHp;

    /**
     * 攻击方式
     */
    private Weapon weapon;

    /**
     * 控制器
     */
    private Controller controller;

    /**
     * 瞄准位置(x)
     */
    private int aimX;

    /**
     * 瞄准位置(y)
     */
    private int aimY;

    /**
     * 角色的移动方向（由控制器控制）
     * 值域[0, 8]，0为静止，1为正上方，1~8按逆时针顺序排列。
     */
    private int direct;

    /**
     * 角色的最后移动方向
     */
    private int lastDirect;

    /**
     * 角色的最大移动速度
     */
    private int velocity;

    /**
     * 用户id
     */
    private int userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 移动动画
     * 0: 上
     * 1: 左
     * 2: 下
     * 3: 右
     */
    private Animation moveAnimation[];

    /**
     * 死亡动画
     */
    private Animation deadAnimation; // 死亡动画

    /**
     * 构造函数
     * @param texture 贴图
     * @param hp 血量
     * @param maxHp 最大血量
     * @param moveAnimation 移动动画
     * @param deadAnimation 死亡动画
     */
    public Character(Hitbox hitbox, Texture texture, int hp, int maxHp, int velocity, Animation[] moveAnimation, Animation deadAnimation) {
        super(hitbox, texture, hp);
        this.maxHp = maxHp;
        this.velocity = velocity;
        this.moveAnimation = moveAnimation;
        this.deadAnimation = deadAnimation;
    }
    
    /**
     * 构造函数（带控制器）
     * @param texture 贴图
     * @param hp 血量
     * @param maxHp 最大血量
     * @param moveAnimation 移动动画
     * @param deadAnimation 死亡动画
     * @param controller 控制器
     */
    public Character(Hitbox hitbox, Texture texture, int hp, int maxHp, int velocity, Animation[] moveAnimation, Animation deadAnimation, Controller controller) {
        this(hitbox, texture, hp, maxHp, velocity ,moveAnimation, deadAnimation);
        this.controller = controller;
    }

    /**
     * 构造函数（带控制器和武器）
     */
    public Character(Hitbox hitbox, Texture texture, int hp, int maxHp, int velocity, Animation[] moveAnimation, Animation deadAnimation, Controller controller, Weapon weapon) {
        this(hitbox, texture, hp, maxHp, velocity, moveAnimation, deadAnimation, controller);
        this.weapon = weapon;
    }

    /**
     * 构造函数（带控制器和武器）
     */
    public Character(Hitbox hitbox, Texture texture, int hp, int maxHp, int velocity, int userId, String username, Animation[] moveAnimation, Animation deadAnimation, Controller controller, Weapon weapon) {
        this(hitbox, texture, hp, maxHp, velocity, moveAnimation, deadAnimation, controller);
        this.userId = userId;
        this.username = username;
        this.weapon = weapon;
    }


    public void move(int direct) throws IllegalArgumentException {
        if(direct < 0 || direct > 8)
            throw new IllegalArgumentException("move() argument exception.");
        this.direct = direct;
    }

        /**
     * 角色控制
     */
    public void play() {

        // controller
        if(controller != null)
            controller.control(this);

        float a = (float)(3600.0 / GameManager.getInstance().getFps() / GameManager.getInstance().getFps());
        int v2 = (int)(velocity / Math.sqrt(2));
        switch (direct) {
            case 0: {getHitbox().setTargetVelocity(0, 0, a); break;}
            case 1: {getHitbox().setTargetVelocity(velocity, 0, a); break;}
            case 2: {getHitbox().setTargetVelocity(v2, -v2, a); break;}
            case 3: {getHitbox().setTargetVelocity(0, -velocity, a); break;}
            case 4: {getHitbox().setTargetVelocity(-v2, -v2, a); break;}
            case 5: {getHitbox().setTargetVelocity(-velocity, 0, a); break;}
            case 6: {getHitbox().setTargetVelocity(-v2, v2, a); break;}
            case 7: {getHitbox().setTargetVelocity(0, velocity, a); break;}
            case 8: {getHitbox().setTargetVelocity(v2, v2, a); break;}
        }

    }

    /**
     * 角色攻击
     * 不需要持续控制。
     */
    public void attack() {
        if(weapon != null && weapon.attack(getId(), getHitbox().getX(), getHitbox().getY(), aimX, aimY)) {

        }
    }


    /**
     * 角色瞄准
     * @param aim_x 目标x轴坐标
     * @param aim_y 目标y轴坐标
     */
    public void aim(int aim_x, int aim_y) {
        this.aimX = aim_x;
        this.aimY = aim_y;
    }


    /**
     * 获取当前角色贴图
     * @return 贴图
     */
    public Texture getCurrentTexture() {
        Texture character;
        int vx = getHitbox().getVx();
        int vy = getHitbox().getVy();
        if(vx == 0 && vy == 0) character = moveAnimation[lastDirect].getTextures()[0];
        else if(vx > 0) {
            character = moveAnimation[2].play(GameManager.getInstance().getTimeStamp());
            lastDirect = 2;
        } else if(vx < 0) {
            character = moveAnimation[0].play(GameManager.getInstance().getTimeStamp());
            lastDirect = 0;
        } else if(vy < 0) {
            character = moveAnimation[1].play(GameManager.getInstance().getTimeStamp());
            lastDirect = 1;
        }else {
            character = moveAnimation[3].play(GameManager.getInstance().getTimeStamp());
            lastDirect = 3;
        }

        if(weapon != null) {
            BufferedImage image;
            float dy = ((controller == null || controller.getControlMessage() == null) ? aimY : controller.getControlMessage().getAimY()) - getHitbox().getY();
            float dx = ((controller == null || controller.getControlMessage() == null) ? aimX : controller.getControlMessage().getAimX()) - getHitbox().getX();
            if(dx == 0) { // weapon in the front of character
                if(dy <= 0)
                    return new Texture(ImageUtil.combineTwoBufferedImage(character.getImage(), weapon.getTextures(0).getImage(), 16, 32), 64, 64, 32, 32);
                else
                    return new Texture(ImageUtil.combineTwoBufferedImage(character.getImage(), weapon.getTextures(1).getImage(), 16, 32), 64, 64, 32, 32);
            } else {
                int deg = 90;
                if(dx * dy > 0) deg = (int)(90 - Math.atan(dy / dx) * 180 / Math.PI);
                else deg = -(int)(90 - Math.atan(-dy / dx) * 180 / Math.PI);

                if(dy <= 0)
                    image = ImageUtil.rotate(weapon.getTextures(0).getImage(), deg);
                else
                    image = ImageUtil.rotate(weapon.getTextures(1).getImage(), deg);

                if(dy > 0) { 
                    return new Texture(ImageUtil.combineTwoBufferedImage(character.getImage(), image, 32, 32), 64, 64, 32, 32);
                } else if(dy < 0) {
                    return new Texture(ImageUtil.combineTwoBufferedImage(character.getImage(), image, 0, 32), 64, 64, 32, 32);
                }
            }
        }
        return character;
    }
}
