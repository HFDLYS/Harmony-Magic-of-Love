package com.hfdlys.harmony.magicoflove;

import java.net.*;
import java.util.List;
import java.io.*;

import javax.swing.*;

import org.apache.ibatis.io.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfdlys.harmony.magicoflove.constant.GameViewConstants;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.game.controller.ClientController;
import com.hfdlys.harmony.magicoflove.game.controller.Controller;
import com.hfdlys.harmony.magicoflove.manager.EntityManager;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.manager.MusicManager;
import com.hfdlys.harmony.magicoflove.network.message.ControlMessage;
import com.hfdlys.harmony.magicoflove.network.message.EntityManagerMessage;
import com.hfdlys.harmony.magicoflove.network.message.Message;
import com.hfdlys.harmony.magicoflove.network.message.PingMessage;
import com.hfdlys.harmony.magicoflove.network.message.RoomInfoMessage;
import com.hfdlys.harmony.magicoflove.network.message.UserMessgae;
import com.hfdlys.harmony.magicoflove.view.GameFrame;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端类
 * @auther Jiasheng Wang
 * @since 2024-07-18
 */
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

    private GameManager gameManager;

    private Controller controller;

    private Client() {
        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            objectMapper = new ObjectMapper();
            gameManager = new GameManager();
        } catch (Exception e) {
            GameFrame.getInstance().setGameState(GameViewConstants.LOADING_VIEW);
            JOptionPane.showMessageDialog(null, "服务端不可用", "错误", JOptionPane.ERROR_MESSAGE);
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
                            // 服务器心跳
                            case MessageCodeConstants.HEART_BEAT:
                                PingMessage pingMessage = objectMapper.readValue(message.getContent(), PingMessage.class);
                                log.info("Ping: " + (System.currentTimeMillis() - pingMessage.getTimestamp()));
                                break;
                            // 登录成功
                            case MessageCodeConstants.SUCCESS:
                                if (userId != null) {
                                    break;
                                }
                                userId = objectMapper.readValue(message.getContent(), UserMessgae.class).getUserId();
                                log.info("User id: " + userId);
                                Client.getInstance().setUserId(userId);
                                Client.getInstance().sendMessage(MessageCodeConstants.ASK_LOBBY_INFO, null);
                                break;
                            // 操作失败
                            case MessageCodeConstants.FAIL:
                                try {
                                    String info = objectMapper.readValue(message.getContent(), String.class);
                                    log.error(info);
                                    if (info != null && !info.isEmpty()) {
                                        JOptionPane.showMessageDialog(null, info, "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                } catch (Exception e) {
                                }
                                GameFrame.getInstance().setGameState(GameViewConstants.LOGIN_VIEW);
                                break;
                            // 服务器返回的房间信息
                            case MessageCodeConstants.USER_INFO:
                                if (userId == null) {
                                    break;
                                }
                                List<UserMessgae> userMessgae = objectMapper.readValue(message.getContent(), objectMapper.getTypeFactory().constructCollectionType(List.class, UserMessgae.class));
                                for (UserMessgae user : userMessgae) {
                                    gameManager.addPlayerSkin(user.getUserId(), user.getSkin());
                                }
                                GameFrame.getInstance().getRoomPlayerModel().clear();
                                for (UserMessgae user : userMessgae) {
                                    GameFrame.getInstance().getRoomPlayerModel().addElement(user);
                                }
                                GameFrame.getInstance().setGameState(GameViewConstants.ROOM_VIEW);
                                break;
                            // 服务器返回的房间信息
                            case MessageCodeConstants.ROOM_LIST_INFO:
                                if (userId == null) {
                                    break;
                                }
                                List<RoomInfoMessage> roomInfoMessages = objectMapper.readValue(message.getContent(), objectMapper.getTypeFactory().constructCollectionType(List.class, RoomInfoMessage.class));
                                GameFrame.getInstance().getRoomListModel().clear();
                                for (RoomInfoMessage roomInfoMessage : roomInfoMessages) {
                                    GameFrame.getInstance().getRoomListModel().addElement(roomInfoMessage);
                                }
                                GameFrame.getInstance().setGameState(GameViewConstants.LOBBY_VIEW);
                                break;
                            // 服务器返回的游戏内实体信息
                            case MessageCodeConstants.ENTITY_MANAGER_INFO:
                                if (userId == null) {
                                    break;
                                }
                                if (GameFrame.getInstance().getGameState() != GameViewConstants.GAME_VIEW) {
                                    GameFrame.getInstance().setGameState(GameViewConstants.GAME_VIEW);
                                }
                                gameManager.getEntityManager().loadEntityManagerMessage(objectMapper.readValue(message.getContent(), EntityManagerMessage.class));
                                break;
                            // 游戏开始信号
                            case MessageCodeConstants.START_GAME:
                                if (userId == null) {
                                    break;
                                }
                                gameManager.getEntityManager().restart();
                                GameFrame.getInstance().setGameState(GameViewConstants.GAME_VIEW);
                            // 游戏结束信号
                            case MessageCodeConstants.GAME_OVER:
                                if (userId == null) {
                                    break;
                                }
                                JOptionPane.showMessageDialog(null, "大家一起走到了最后，真是又一次合家欢庆的结果", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                                Client.getInstance().getGameManager().getEntityManager().restart();
                                sendMessage(MessageCodeConstants.ASK_LOBBY_INFO, null);
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
        try {
            MusicManager.getInstance().playMusic(Resources.getResourceAsStream("music/main.mp3"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameManager.run();
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
