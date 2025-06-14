package com.hfdlys.harmony.magicoflove.network.handler;

import com.hfdlys.harmony.magicoflove.manager.EntityManager;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.network.message.UserMessgae;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.CharacterRegisterMessage;
import com.hfdlys.harmony.magicoflove.Server;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.game.entity.Character;
import com.hfdlys.harmony.magicoflove.database.entity.User;
import com.hfdlys.harmony.magicoflove.game.factory.CharacterFactory;

import java.util.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 房间处理类
 * @auther Jiasheng Wang
 * @since 2024-07-18
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class RoomHandler extends Thread {
    /**
     * 房间id
     */
    private int roomId;

    /**
     * 玩家数量
     */
    private int playerNum;

    private final int WAITING = 0;

    private final int GAMING = 1;

    private final int END = 2;

    /**
     * 房间状态
     */
    private int roomStatus;

    /**
     * 玩家列表
     */
    private List<User> players;

    /**
     * 房间名
     */
    private String roomName;

    /**
     * 最大玩家数量
     */
    private final int MAX_PLAYER_NUM = 6;

    /**
     * 玩家锁
     */
    private final Object playerLock = new Object();

    /**
     * 游戏管理器，获取引用
     */
    private GameManager gameManager;

    public RoomHandler(int roomId, String roomName) {
        this.roomId = roomId;
        this.gameManager = new GameManager(roomId);
        this.roomName = roomName;
        this.players = new ArrayList<>();
        this.roomStatus = WAITING;
    }

    @Override
    public void run() {
        while (roomStatus == WAITING) {
            if (playerNum < 1) {
                Server.getInstance().getRoomManager().removeRoom(roomId);
                break;
            }
            List<UserMessgae> userMessgaes = new ArrayList<>();
            synchronized(playerLock) {
                for (User user : players) {
                    UserMessgae userMessgae = new UserMessgae();
                    userMessgae.setUserId(user.getUserId());
                    userMessgae.setUsername(user.getUsername());
                    userMessgae.setSkin(user.getSkin());
                    userMessgaes.add(userMessgae);
                }
            }
            Server.getInstance().broadcast(roomId, MessageCodeConstants.USER_INFO, userMessgaes);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (roomStatus == END) {
            return;
        }
        List<UserMessgae> userMessgaes = new ArrayList<>();
        EntityManager entityManager = gameManager.getEntityManager();
        int minX = -300;
        int maxX = 300;
        int minY = -500;
        int maxY = 500;
        for (User user : players) {
            UserMessgae userMessgae = new UserMessgae();
            userMessgae.setUserId(user.getUserId());
            userMessgae.setUsername(user.getUsername());
            userMessgae.setSkin(user.getSkin());
            userMessgaes.add(userMessgae);
            int weaponType = (int) (Math.random() * 3) + 1;
            Character character = CharacterFactory.getCharacter(user.getUserId(), user.getUsername(), weaponType,
                Server.getInstance().getClientMapByUserId().get(user.getUserId()).getController(),
                gameManager
            );
            character.setCamp(user.getUserId());
            int randomX = (int) (Math.random() * (maxX - minX + 1) + minX);
            int randomY = (int) (Math.random() * (maxY - minY + 1) + minY);
            character.getHitbox().setCoordinate(randomX, randomY);
            entityManager.add(character, new CharacterRegisterMessage(1, user.getUserId(), user.getUsername(), weaponType));
        }
        Server.getInstance().broadcast(roomId, MessageCodeConstants.USER_INFO, userMessgaes);
        log.info("房间{}开始游戏", roomName);
        gameManager.runServer();
    }

    public boolean addPlayer(User user) {
        if (roomStatus == GAMING || playerNum >= MAX_PLAYER_NUM || roomStatus == END) {
            return false;
        }
        synchronized (playerLock) {
            if (playerNum < MAX_PLAYER_NUM) {
                players.add(user);
                playerNum++;
                return true;
            }
            return false;
        }
    }

    /**
     * 移除玩家
     * @param user
     * @return 是否成功
     */
    public boolean removePlayer(User user) {
        synchronized (playerLock) {
            if (players.contains(user)) {
                players.remove(user);
                playerNum--;
                return true;
            }
            if (players.size() == 0) {
                Server.getInstance().getRoomManager().removeRoom(roomId);
            }
            return true;
        }
    }

    /**
     * 关闭房间
     */
    public void closeRoom() {
        synchronized (playerLock) {
        }
        roomStatus = END;
        this.interrupt();
    }

    /**
     * 开始游戏
     * @param user
     * @return 是否成功
     */
    public boolean startGame(User user) {
        if (roomStatus == GAMING) {
            return false;
        }
        synchronized (playerLock) {
            if (players.get(0).getUserId() != user.getUserId()) {
                return false;
            }
            if (playerNum >= 1) {
                roomStatus = GAMING;
                return true;
            }
            return false;
        }
    }
}
