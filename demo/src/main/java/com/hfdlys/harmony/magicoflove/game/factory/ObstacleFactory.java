package com.hfdlys.harmony.magicoflove.game.factory;

import java.io.IOException;

import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;
import com.hfdlys.harmony.magicoflove.game.entity.Obstacle;

/**
 * 障碍物工厂类
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
public class ObstacleFactory {
    /**
     * 障碍物编号：空气墙 - 0
     */
    public static final int AIR_WALL = 0;

    /**
     * 障碍物编号：石头 - 1
     */
    public static final int STONE = 1;

    /**
     * 障碍物编号：混凝土 - 2
     */
    public static final int CONCRETE = 2;

    /**
     * 障碍物编号：木头 - 3
     */
    public static final int WOOD = 3;

    /**
     * 无限血量
     */
    private static final int INFINITE_HP = 2147483647;

    /**
     * 石头血量
     */
    private static final int STONE_HP = 200;

    /**
     * 混凝土血量
     */
    private static final int CONCRETE_HP = 1000;

    private static final int WOOD_HP = 50;

    /**
     * 获取障碍物
     * @param type 障碍物类型
     * @param x 障碍物x轴坐标
     * @param y 障碍物y轴坐标
     * @param lx 障碍物x轴方向长度
     * @param ly 障碍物y轴方向长度
     * @return 障碍物
     */
    public static Obstacle getObstacle(int type, int x, int y, int lx, int ly) {
        try {
            if(type == AIR_WALL) {
                //if(lx == 0 && ly == 0) {
                //    System.out.println("空气墙碰撞箱参数异常!");
                //    return null;
                //}
                return new Obstacle(
                        new Hitbox(x, y, lx, ly),
                        null,
                        INFINITE_HP
                );
            } else if(type == STONE) {
                return new Obstacle(
                        new Hitbox(x, y, 50),
                        new Texture("pics/stone.png", 100, 100, 50, 50),
                        STONE_HP
                );
            } else if(type == CONCRETE) {
                return new Obstacle(
                        new Hitbox(x, y, 50),
                        new Texture("pics/block.png", 200, 200, 120, 100),
                        CONCRETE_HP
                );
            } else if(type == WOOD) {
                return new Obstacle(
                        new Hitbox(x, y, 50),
                        new Texture("pics/wood.png", 100, 100, 50, 50),
                        WOOD_HP
                );
            } else {
                System.out.println("未知障碍物类型!");
            }
        } catch (IOException e) {
            System.out.println("障碍物生成异常!");
            e.getStackTrace();
        }
        return null;
    }

    public static Obstacle getObstacle(int type, int x, int y) {
        return getObstacle(type, x, y, 0, 0);
    }
}
