package com.hfdlys.harmony.magicoflove;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;

import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.network.handler.ClientHandler;
import com.hfdlys.harmony.magicoflove.network.message.Messages.Message;
import com.hfdlys.harmony.magicoflove.network.message.Messages.PingMessage;
import com.hfdlys.harmony.magicoflove.view.ServerFrame;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server extends WebSocketServer {
    private static volatile Server instance;
    private final HashMap<String, ClientHandler> clientMap;
    private int port = 2336;
    private final int playerMax = 100;
    private final ThreadPoolExecutor executorPool;
    private final HeartBeatThread heartBeatThread;

    public static Server getInstance() {
        if (instance == null) {
            synchronized (Server.class) {
                if (instance == null) {
                    instance = new Server(new InetSocketAddress(2336));
                }
            }
        }
        return instance;
    }

    private Server(InetSocketAddress address) {
        super(address);
        clientMap = new HashMap<>();
        executorPool = new ThreadPoolExecutor(50, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        heartBeatThread = new HeartBeatThread();
    }

    public void runs() {
        ServerFrame.getInstance().launchFrame();
        log.info("Starting server on port " + port);
        this.start();
        this.heartBeatThread.start();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log.info("New connection: " + conn.getRemoteSocketAddress().toString());
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        ClientHandler clientHandler = new ClientHandler(conn);
        clientMap.put(uuid, clientHandler);
        executorPool.execute(clientHandler);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        log.info("Closed connection: " + conn.getRemoteSocketAddress().toString());
        clientMap.values().removeIf(clientHandler -> clientHandler.getConnection().equals(conn));
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        //没用，但父类要求必须重写
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        clientMap.values().stream()
                .filter(handler -> handler.getConnection().equals(conn))
                .findFirst().ifPresent(clientHandler -> clientHandler.handleMessage(message.array()));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        log.info("Server started successfully on port " + port);
    }

    private class HeartBeatThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);
                    PingMessage pingMessage = PingMessage.newBuilder()
                            .setTimestamp(System.currentTimeMillis())
                            .build();
                    Message message = Message.newBuilder()
                            .setCode(MessageCodeConstants.HEART_BEAT)
                            .setPingMessage(pingMessage)
                            .build();
                    for (Map.Entry<String, ClientHandler> entry : clientMap.entrySet()) {
                        ClientHandler clientHandler = entry.getValue();
                        if (clientHandler.getConnection().isClosed()) {
                            ServerFrame.getInstance().appendText("客户端" + entry.getKey() + "已断开连接\n");
                            log.info("Client " + entry.getKey() + " disconnected");
                            clientMap.remove(entry.getKey());
                            continue;
                        }

                        clientHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Server.getInstance().runs();
    }
}