package com.hfdlys.harmony.magicoflove;

import java.net.*;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.manager.RoomManager;
import com.hfdlys.harmony.magicoflove.network.handler.ClientHandler;
import com.hfdlys.harmony.magicoflove.network.handler.RoomHandler;
import com.hfdlys.harmony.magicoflove.network.message.Message;
import com.hfdlys.harmony.magicoflove.network.message.PingMessage;
import com.hfdlys.harmony.magicoflove.view.ServerFrame;

import java.util.concurrent.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务器类
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
@Data
@Slf4j
public class Server {
    /**
     * 单例模式
     */
    private static Server instance;

    /**
     * 获取服务器实例
     * @return 服务器实例
     */
    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    /**
     * ServerSocket
     */
    private ServerSocket serverSocket;

    /**
     * 根据UUID定位客户端
     */
    private HashMap<String, ClientHandler> clientMap;

    /**
     * 线程锁
     */
    private final Object clientMapLock = new Object();

    /**
     * 根据userId定位客户端
     */
    private HashMap<Integer, ClientHandler> clientMapByUserId;

    /**
     * 服务器开放端口
     */
    private int port = 2336;

    /**
     * 玩家人数
     * 默认人数为100
     */
    private final int playerMax = 100;

    private RoomManager roomManager;

    private ThreadPoolExecutor executorPool;

    public void run() {
        ServerFrame.getInstance().launchFrame();
        new ServerHandler().start();
        roomManager.run();
    }

    /**
     * 构造方法
     */
    private Server() {
        try {
            clientMap = new HashMap<>();
            clientMapByUserId = new HashMap<>();
            serverSocket = new ServerSocket(port);
            roomManager = new RoomManager();
            executorPool = new ThreadPoolExecutor(50, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 广播信息
     */
    public void broadcast(int code, Object content) {
        synchronized (clientMapLock) {
            for (Map.Entry<String, ClientHandler> entry : clientMap.entrySet()) {
                ClientHandler clientHandler = entry.getValue();
                clientHandler.sendMessage(code, content);
            }
        }
    }

    /**
     * 广播信息
     */
    public void broadcast(int roomId, int code, Object content) {
        synchronized (clientMapLock) {
            for (Map.Entry<String, ClientHandler> entry : clientMap.entrySet()) {
                ClientHandler clientHandler = entry.getValue();
                if (clientHandler.getRoomId() == roomId) {
                    clientHandler.sendMessage(code, content);
                }
            }
        }
    }


    /**
     * 服务器处理线程
     */
    private class ServerHandler extends Thread {
        @Override
        public void run() {
            new HeartBeatThread().start();
            while (true) {
                try {
                    log.info("Waiting for connection...");
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket);
                    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                    clientMap.put(uuid, clientHandler);
                    executorPool.execute(clientHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 心跳线程
     */
    private class HeartBeatThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);
                    PingMessage pingMessage = new PingMessage();
                    pingMessage.setTimestamp(System.currentTimeMillis());
                    synchronized (clientMapLock) {
                        for (Map.Entry<String, ClientHandler> entry : clientMap.entrySet()) {
                            ClientHandler clientHandler = entry.getValue();
                            if (clientHandler.getSocket().isClosed()) {
                                ServerFrame.getInstance().appendText("客户端" + entry.getKey() + "已断开连接\n");
                                log.info("Client " + entry.getKey() + " disconnected");
                                clientMap.remove(entry.getKey());
                                continue;
                            }
                            clientHandler.sendMessage(MessageCodeConstants.HEART_BEAT, pingMessage);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    

    public static void main(String[] args) {
        
        Server.getInstance().run();
    }
}
