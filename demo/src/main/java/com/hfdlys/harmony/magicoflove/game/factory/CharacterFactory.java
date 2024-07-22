package com.hfdlys.harmony.magicoflove.game.factory;

import com.hfdlys.harmony.magicoflove.game.common.Animation;
import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;
import com.hfdlys.harmony.magicoflove.game.controller.Client2Controller;
import com.hfdlys.harmony.magicoflove.game.controller.ClientController;
import com.hfdlys.harmony.magicoflove.game.controller.Controller;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.network.message.ControlMessage;
import com.hfdlys.harmony.magicoflove.view.GameFrame;
import com.hfdlys.harmony.magicoflove.game.entity.Character;

/**
 * 角色工厂
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
public class CharacterFactory {
    /**
     * 获取角色
     * @param type 类型
     * @return 对应类型角色
     */
    /*
    public static Character getTestCharacter(int type, int x, int y, int hp, int weaponType) {
        try {
            switch (type) {
                case 1: {
                    Texture[][] texture_move = new Texture[4][9];
                    Texture texture_all = new Texture("catgame.png", 1344, 832, 32, 32);
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 9; j++) {
                            texture_move[i][j] = texture_all.getCutTexture(512 + 64 * i, 64 * j, 64, 64, 32, 32); 
                        }
                    }
                    Animation moveAnimation[] = new Animation[4];
                    for (int i = 0; i < 4; i++) {
                        moveAnimation[i] = new Animation(texture_move[i], 9);
                    }
                    Texture[] texture = new Texture[6];
                    for (int i = 0; i < 6; i++) {
                        texture[i] = texture_all.getCutTexture(1280, i * 64, 64, 64, 32, 32);
                    }
                    Animation deadAnimation = new Animation(texture, 6);
                    return new Character(
                        new Hitbox(x, y, 0, 0, 8, 16),
                        texture_move[2][0],
                        hp,
                        hp,
                        240 / gameManager.getFps(),
                        moveAnimation,
                        deadAnimation,
                        new ClientController(GameFrame.getInstance()),
                        WeaponFactory.getWeapon(weaponType)
                    );
                }
                case 2: {
                    Texture[][] texture_move = new Texture[4][9];
                    Texture texture_rest = new Texture("character/enemy.png", 1344, 832, 32, 32);
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 9; j++) {
                            texture_move[i][j] = texture_rest.getCutTexture(512 + 64 * i, 64 * j, 64, 64, 32, 32); 
                        }
                    }
                    Animation moveAnimation[] = new Animation[4];
                    for (int i = 0; i < 4; i++) {
                        moveAnimation[i] = new Animation(texture_move[i], 9);
                    }
                    Texture[] texture = new Texture[6];
                    for (int i = 0; i < 6; i++) {
                        texture[i] = texture_rest.getCutTexture(1280, i * 64, 64, 64, 32, 32);
                    }
                    Animation deadAnimation = new Animation(texture, 6);
                    return new Character(
                        new Hitbox(x, y, 0, 0, 8, 16),
                        texture_move[2][0],
                        hp,
                        hp,
                        240 / gameManager.getFps(),
                        moveAnimation,
                        deadAnimation,
                        new Client2Controller(),
                        WeaponFactory.getWeapon(weaponType)
                    );
                }
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    */
    /**
     * /**
     * 获取角色
     * @param userId 用户编号
     * @param weaponType 武器类型
     * @param Controller 控制器
     * @return 对应类型角色
     */
    public static Character getCharacter(int userId, String username, int weaponType, Controller controller, GameManager gameManager) {
        try {
            Texture[][] texture_move = new Texture[4][9];
            Texture texture_all = gameManager.getPlayerSkin(userId);
            if (texture_all == null) {
                texture_all = new Texture("character/enemy.png", 1344, 832, 32, 32);
            }
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 9; j++) {
                    texture_move[i][j] = texture_all.getCutTexture(512 + 64 * i, 64 * j, 64, 64, 32, 32); 
                }
            }
            Animation moveAnimation[] = new Animation[4];
            for (int i = 0; i < 4; i++) {
                moveAnimation[i] = new Animation(texture_move[i], 9);
            }
            Texture[] texture = new Texture[6];
            for (int i = 0; i < 6; i++) {
                texture[i] = texture_all.getCutTexture(1280, i * 64, 64, 64, 32, 32);
            }
            Animation deadAnimation = new Animation(texture, 6);
            return new Character(
                new Hitbox(0, 0, 0, 0, 8, 16),
                texture_move[2][0],
                400,
                400,
                240 / gameManager.getFps(),
                userId,
                username,
                moveAnimation,
                deadAnimation,
                controller,
                WeaponFactory.getWeapon(weaponType, gameManager)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
