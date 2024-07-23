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

        int minX = -350; // Minimum x coordinate
        int maxX = 350; // Maximum x coordinate
        int minY = -550; // Minimum y coordinate
        int maxY = 550; // Maximum y coordinate
        for (int i = 0; i < 6; i++) {
            int randomX = (int) (Math.random() * (maxX - minX + 1) + minX);
            int randomY = (int) (Math.random() * (maxY - minY + 1) + minY);
            gameManager.getEntityManager().add(ObstacleFactory.getObstacle(ObstacleFactory.STONE, randomX, randomY, 50, 50), new ObstacleRegisterMessage(ObstacleFactory.STONE));
        }

        for (int i = 0; i < 6; i++) {
            int randomX = (int) (Math.random() * (maxX - minX + 1) + minX);
            int randomY = (int) (Math.random() * (maxY - minY + 1) + minY);
            gameManager.getEntityManager().add(ObstacleFactory.getObstacle(ObstacleFactory.WOOD, randomX, randomY, 50, 50), new ObstacleRegisterMessage(ObstacleFactory.WOOD));
        }

        for (int i = 0; i < 6; i++) {
            int randomX = (int) (Math.random() * (maxX - minX + 1) + minX);
            int randomY = (int) (Math.random() * (maxY - minY + 1) + minY);
            gameManager.getEntityManager().add(ObstacleFactory.getObstacle(ObstacleFactory.CONCRETE, randomX, randomY, 50, 50), new ObstacleRegisterMessage(ObstacleFactory.CONCRETE));
        }
    }
}
