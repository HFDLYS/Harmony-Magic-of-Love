package com.hfdlys.harmony.magicoflove.manager;

import com.hfdlys.harmony.magicoflove.database.entity.User;
import com.hfdlys.harmony.magicoflove.network.handler.RoomHandler;
import com.hfdlys.harmony.magicoflove.network.message.RoomInfoMessage;
import com.hfdlys.harmony.magicoflove.view.ServerFrame;

import lombok.Data;

import java.util.*;

@Data
public class RoomManager {
    public RoomManager() {
        roomMap = new HashMap<>();
    }

    /**
     * 游戏房间
     */
    private HashMap<Integer, RoomHandler> roomMap;

    /**
     * 房间号
     */
    private int roomId = 0;

    /**
     * 
     */
    public int createRoom(User user, String roomName) {
        roomId++;
        RoomHandler roomHandler = new RoomHandler(roomId, roomName);
        roomMap.put(roomId, roomHandler);
        joinRoom(roomId, user);
        roomHandler.start();
        return roomId;
    }

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
            return false;
        }
        ServerFrame.getInstance().appendText(user.getUsername() + "离开了房间" + roomId + "\n");
        return roomHandler.removePlayer(user);
    }

    public boolean removeRoom(int roomId) {
        RoomHandler roomHandler = roomMap.get(roomId);
        if (roomHandler == null) {
            return false;
        }
        roomHandler.closeRoom();
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
}
