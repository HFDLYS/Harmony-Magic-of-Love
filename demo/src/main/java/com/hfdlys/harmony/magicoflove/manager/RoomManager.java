package com.hfdlys.harmony.magicoflove.manager;

import com.hfdlys.harmony.magicoflove.Server;
import com.hfdlys.harmony.magicoflove.database.entity.User;
import com.hfdlys.harmony.magicoflove.network.handler.RoomHandler;
import com.hfdlys.harmony.magicoflove.network.message.RoomInfoMessage;
import com.hfdlys.harmony.magicoflove.view.ServerFrame;

import lombok.Data;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 房间管理器
 * @auther Jiasheng Wang
 * @since 2024-07-23
 */
@Data
public class RoomManager {

    private ThreadPoolExecutor roomExecutorPool;

    public RoomManager() {
        roomMap = new HashMap<>();
        roomExecutorPool = new ThreadPoolExecutor(50, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    /**
     * 游戏房间
     */
    private HashMap<Integer, RoomHandler> roomMap;

    /**
     * 房间号
     */
    private int roomId = 0;

    public void run() {
        RoomLiveHandler roomLiveHandler = new RoomLiveHandler();
        roomLiveHandler.start();
    }

    /**
     * 创建房间
     * @param user 用户
     * @param roomName 房间名
     */
    public int createRoom(User user, String roomName) {
        roomId++;
        RoomHandler roomHandler = new RoomHandler(roomId, roomName);
        roomMap.put(roomId, roomHandler);
        joinRoom(roomId, user);
        ServerFrame.getInstance().appendText(user.getUsername() + "创建了房间" + roomId + "\n");
        roomExecutorPool.execute(roomHandler);
        return roomId;
    }

    /**
     * 加入房间
     * @param roomId 房间号
     * @param user 用户
     * @return  是否加入成功    true:成功    false:失败
     */
    public boolean joinRoom(int roomId, User user) {
        RoomHandler roomHandler = roomMap.get(roomId);
        if (roomHandler == null) {
            return false;
        }
        return roomHandler.addPlayer(user);
    }

    
    public boolean startGame(int roomId, User user) {
        RoomHandler roomHandler = roomMap.get(roomId);
        if (roomHandler == null) {
            return false;
        }
        return roomHandler.startGame(user);
    }

    public boolean leaveRoom(int roomId, User user) {
        RoomHandler roomHandler = roomMap.get(roomId);
        if (roomHandler == null) {
            return true;
        }
        ServerFrame.getInstance().appendText(user.getUsername() + "离开了房间" + roomId + "\n");
        Server.getInstance().getClientMapByUserId().get(user.getUserId()).setRoomId(0);
        return roomHandler.removePlayer(user);
    }

    public boolean removeRoom(int roomId) {
        RoomHandler roomHandler = roomMap.get(roomId);
        if (roomHandler == null) {
            return false;
        }
        roomHandler.closeRoom();
        for (User user : roomHandler.getPlayers()) {
            Server.getInstance().getClientMapByUserId().get(user.getUserId()).setRoomId(0);
        }
        roomMap.remove(roomId);
        ServerFrame.getInstance().appendText("房间" + roomId + "已解散\n");
        return true;
    }

    public List<RoomInfoMessage> getRoomList() {
        List<RoomInfoMessage> roomInfoMessages = new ArrayList<>();
        for (Map.Entry<Integer, RoomHandler> entry : roomMap.entrySet()) {
            RoomHandler roomHandler = entry.getValue();
            RoomInfoMessage roomInfoMessage = new RoomInfoMessage();
            roomInfoMessage.setRoomId(roomHandler.getRoomId());
            roomInfoMessage.setRoomName(roomHandler.getRoomName());
            roomInfoMessage.setRoomPlayer(roomHandler.getPlayerNum());
            roomInfoMessages.add(roomInfoMessage);
        }
        return roomInfoMessages;
    }

    private class RoomLiveHandler extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    for (Map.Entry<Integer, RoomHandler> entry : roomMap.entrySet()) {
                        RoomHandler roomHandler = entry.getValue();
                        if (roomHandler.getPlayerNum() == 0) {
                            removeRoom(entry.getKey());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
