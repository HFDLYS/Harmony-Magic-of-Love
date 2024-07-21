package com.hfdlys.harmony.magicoflove;

import java.net.ServerSocket;
import java.net.Socket;

import com.hfdlys.harmony.magicoflove.view.ServerFrame;

import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
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
     * 服务器开放端口
     */
    private int port = 2336;

    /**
     * 玩家人数
     * 默认人数为10
     */
    private final int playerMax = 10;

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
            serverSocket = new ServerSocket(port);
            executorPool = new ThreadPoolExecutor(10, 10, 1000, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(String threadName, Socket socket) {
            super(threadName);
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                log.info("Client connected: " + socket.getInetAddress());
                ServerFrame.getInstance().appendText("Client connected: " + socket.getInetAddress() + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ServerHandler extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    
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
