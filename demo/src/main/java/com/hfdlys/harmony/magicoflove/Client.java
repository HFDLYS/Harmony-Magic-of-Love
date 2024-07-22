package com.hfdlys.harmony.magicoflove;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;

import com.hfdlys.harmony.magicoflove.network.message.Messages.*;
import com.hfdlys.harmony.magicoflove.view.ClientFrame;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.manager.GameManager;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.bytecode.ByteArray;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

@Data
@Slf4j
public class Client {
    private static Client instance;

    private final String host = "localhost";
    private final int port = 2336;
    private WebSocketClient webSocketClient;

    private Integer userId;

    private Client() {
        try {
            URI serverUri = new URI("ws://" + host + ":" + port);
            webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    log.info("Connected to server");
                }

                @Override
                public void onMessage(String s) {
                    //没用，但父类要求必须重写
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    handleMessage(bytes.array());
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("Disconnected from server");
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }


            };
            webSocketClient.connectBlocking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public void run() {
        ClientFrame.getInstance().launchFrame();
    }

    public void handleMessage(byte[] messageBytes) {
        try {
            Message message = Message.parseFrom(messageBytes);
            switch (message.getCode()) {
                case MessageCodeConstants.HEART_BEAT:
                    PingMessage pingMessage = message.getPingMessage();
                    log.info("Ping: {}", System.currentTimeMillis() - pingMessage.getTimestamp());
                    break;
                case MessageCodeConstants.SUCCESS:
                    if (userId != null) {
                        break;
                    }
                    UserMessage userMessage = message.getUserMessage();
                    userId = userMessage.getUserId();
                    log.info("User id: " + userId);
                    ClientFrame.getInstance().getDialog().finish("登录成功");
                    break;
                case MessageCodeConstants.FAIL:
                    if (userId != null) {
                        break;
                    }
                    ClientFrame.getInstance().getDialog().finish("登录失败");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            synchronized (webSocketClient) {
                log.info("Sending message: " + message.getCode());
                webSocketClient.send(message.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client.getInstance().run();
    }
}