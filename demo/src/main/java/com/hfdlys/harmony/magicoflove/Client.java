package com.hfdlys.harmony.magicoflove;

import java.net.*;
import java.util.List;
import java.io.*;

import javax.swing.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.game.controller.ClientController;
import com.hfdlys.harmony.magicoflove.game.controller.Controller;
import com.hfdlys.harmony.magicoflove.game.entity.EntityManager;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.network.message.ControlMessage;
import com.hfdlys.harmony.magicoflove.network.message.EntityManagerMessage;
import com.hfdlys.harmony.magicoflove.network.message.Message;
import com.hfdlys.harmony.magicoflove.network.message.PingMessage;
import com.hfdlys.harmony.magicoflove.network.message.UserMessgae;
import com.hfdlys.harmony.magicoflove.view.GameFrame;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Client {
    /**
     * 单例模式
     */
    private static Client instance;

    private final String host = "localhost";

    private final int port = 2336;

    private Socket socket;

    private Integer userId;

    private BufferedReader reader;

    private PrintWriter writer;

    private ObjectMapper objectMapper;

    private Controller controller;

    private Client() {
        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            objectMapper = new ObjectMapper();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ControlMessage getControlMessage() {
        if (controller == null) {
            return null;
        }
        controller.control(null);
        return controller.getControlMessage();
    }


    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    private class ServerHandler extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    synchronized(reader) {
                        String line = reader.readLine();
                        Message message = objectMapper.readValue(line, Message.class);
                        switch (message.getCode()) {
                            case MessageCodeConstants.HEART_BEAT:
                                PingMessage pingMessage = objectMapper.readValue(message.getContent(), PingMessage.class);
                                log.info("Ping: " + (System.currentTimeMillis() - pingMessage.getTimestamp()));
                                break;
                            case MessageCodeConstants.SUCCESS:
                                if (userId != null) {
                                    break;
                                }
                                userId = objectMapper.readValue(message.getContent(), UserMessgae.class).getUserId();
                                log.info("User id: " + userId);
                                Client.getInstance().setUserId(userId);
                                GameFrame.getInstance().setMenuState(5);
                                break;
                            case MessageCodeConstants.FAIL:
                                if (userId != null) {
                                    break;
                                }
                                GameFrame.getInstance().setMenuState(1);
                                break;
                            case MessageCodeConstants.USER_INFO:
                                if (userId == null) {
                                    break;
                                }
                                // log.info("User info: " + message.getContent());
                                List<UserMessgae> userMessgae = objectMapper.readValue(message.getContent(), objectMapper.getTypeFactory().constructCollectionType(List.class, UserMessgae.class));
                                for (UserMessgae user : userMessgae) {
                                    GameManager.getInstance().addPlayerSkin(user.getUserId(), user.getSkin());
                                }
                                break;
                            case MessageCodeConstants.ENTITY_MANAGER_INFO:
                                // log.info("Entity manager info: " + message.getContent());
                                EntityManager.getInstance().loadEntityManagerMessage(objectMapper.readValue(message.getContent(), EntityManagerMessage.class));
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        new ServerHandler().start();
        
        GameManager.getInstance().run();
    }
    

    public void sendMessage(int code, Object content) {
        try {
            synchronized(writer) {
                Message message = new Message();
                message.setCode(code);
                message.setContent(objectMapper.writeValueAsString(content));
                writer.println(objectMapper.writeValueAsString(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Client.getInstance().run();
    }
}
