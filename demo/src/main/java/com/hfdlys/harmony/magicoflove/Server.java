package com.hfdlys.harmony.magicoflove;

import java.net.*;
import java.util.*;

import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.network.handler.ClientHandler;
import com.hfdlys.harmony.magicoflove.network.protoc.Message;
import com.hfdlys.harmony.magicoflove.view.ServerFrame;

import java.util.concurrent.*;

import lombok.extern.slf4j.Slf4j;

/**
 * 服务器类
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
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
     * 服务器开放端口
     */
    private int port = 2336;

    /**
     * 玩家人数
     * 默认人数为100
     */
    private final int playerMax = 100;

    private ThreadPoolExecutor executorPool;

    public void run() {
        ServerFrame.getInstance().launchFrame();
        new ServerHandler().start();
        
    }

    /**
     * 构造方法
     */
    private Server() {
        try {
            clientMap = new HashMap<>();
            serverSocket = new ServerSocket(port);
            executorPool = new ThreadPoolExecutor(50, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    

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

    private class HeartBeatThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);
                    for (Map.Entry<String, ClientHandler> entry : clientMap.entrySet()) {
                        ClientHandler clientHandler = entry.getValue();
                        Message message = new Message();
                        message.setCode(MessageCodeConstants.HEART_BEAT);
                        clientHandler.sendMessage(message);
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
