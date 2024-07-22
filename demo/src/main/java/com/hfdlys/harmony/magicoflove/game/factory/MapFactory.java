package com.hfdlys.harmony.magicoflove.game.factory;

import com.hfdlys.harmony.magicoflove.game.entity.EntityManager;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.ObstacleRegisterMessage;

public class MapFactory {
    public static void createMap(GameManager gameManager) {
        // 四面墙
        gameManager.getEntityManager().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, -375, 0, 25, 600), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        gameManager.getEntityManager().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 375, 0, 25, 600), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        gameManager.getEntityManager().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 0, -575, 400, 25), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        gameManager.getEntityManager().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 0, 575, 400, 25), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
    }
}
