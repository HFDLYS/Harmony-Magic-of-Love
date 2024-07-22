package com.hfdlys.harmony.magicoflove.game.factory;

import com.hfdlys.harmony.magicoflove.game.entity.EntityManager;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.ObstacleRegisterMessage;

public class MapFactory {
    public static void createMap() {
        // 四面墙
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, -375, 0, 25, 600), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 375, 0, 25, 600), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 0, -575, 400, 25), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 0, 575, 400, 25), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
    }
}
