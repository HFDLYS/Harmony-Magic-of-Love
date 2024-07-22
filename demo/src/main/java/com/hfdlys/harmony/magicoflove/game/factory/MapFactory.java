package com.hfdlys.harmony.magicoflove.game.factory;

import com.hfdlys.harmony.magicoflove.game.entity.EntityManager;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.ObstacleRegisterMessage;

public class MapFactory {
    public static void createMap() {
        // 四面墙
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, -425, 0, 25, 550), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 425, 0, 25, 550), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 0, -525, 450, 25), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 0, 525, 450, 25), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
    }
}
