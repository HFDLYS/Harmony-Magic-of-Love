package com.hfdlys.harmony.magicoflove.network.message;

import lombok.Data;

/**
 * 房间信息
 * @author Jiasheng Wang
 * @since 2024-07-21
 */
@Data
public class RoomInfoMessage {
    /**
     * 房间ID
     */
    private int roomId;

    /**
     * 房间名
     */
    private String roomName;

    /**
     * 房间人数
     */
    private int roomPlayer;

    /**
     * 人数上限
     */
    private int maxPlayer;

    public RoomInfoMessage() {
    }

    public RoomInfoMessage(int roomId, String roomName, int roomPeople) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomPlayer = roomPeople;
    }

    @Override
    public String toString() {
        return "房间名：" + roomName + "\t房间人数：" + roomPlayer + "/" + maxPlayer;
    }

}
