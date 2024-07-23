package com.hfdlys.harmony.magicoflove.network.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfdlys.harmony.magicoflove.Server;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.database.entity.User;
import com.hfdlys.harmony.magicoflove.database.service.UserService;
import com.hfdlys.harmony.magicoflove.game.controller.Controller;
import com.hfdlys.harmony.magicoflove.game.controller.ServerRemoteController;
import com.hfdlys.harmony.magicoflove.game.entity.EntityManager;
import com.hfdlys.harmony.magicoflove.game.factory.CharacterFactory;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.network.message.ControlMessage;
import com.hfdlys.harmony.magicoflove.network.message.LoginMessage;
import com.hfdlys.harmony.magicoflove.network.message.Message;
import com.hfdlys.harmony.magicoflove.network.message.RegisterMessage;
import com.hfdlys.harmony.magicoflove.network.message.RoomInfoMessage;
import com.hfdlys.harmony.magicoflove.network.message.UserMessgae;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.CharacterRegisterMessage;
import com.hfdlys.harmony.magicoflove.view.ServerFrame;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.io.*;
import java.util.*;

/**
 * 客户端处理类
 * 话虽如此，其实是给客户端处理的线程
 * @author Jiasheng Wang
 * @since 2024-07-18
 */

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ClientHandler extends Thread {
    private Socket socket;
    
    private BufferedReader reader;

    private PrintWriter writer;

    private ObjectMapper objectMapper;

    private Controller controller;

    private int roomId;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        objectMapper = new ObjectMapper();
        controller = new ServerRemoteController();
    }

    private User user;

    @Override
    public void run() {
        try {
            log.info("Client connected: " + socket.getInetAddress());
            ServerFrame.getInstance().appendText("接收连接：" + socket.getInetAddress() + "\n");
            while (true) {
                synchronized(reader) {
                    Message message = objectMapper.readValue(reader.readLine(), Message.class);
                    switch (message.getCode()) {
                        case MessageCodeConstants.HEART_BEAT:
                            
                            break;
                        case MessageCodeConstants.LOGIN:
                            if (this.user != null) {
                                ServerFrame.getInstance().appendText("用户" + this.user.getUsername() + "已登录\n");
                                sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            LoginMessage loginMessage = objectMapper.readValue(message.getContent(), LoginMessage.class);
                            this.user = UserService.getInstance().login(loginMessage.getUsername(), loginMessage.getPassword());
                            if (user != null) {
                                Integer userId = user.getUserId();
                                if (Server.getInstance().getClientMapByUserId().containsKey(userId)) {
                                    ServerFrame.getInstance().appendText("用户" + userId + "已登录\n");
                                    sendMessage(MessageCodeConstants.FAIL, "");
                                    break;
                                }
                                ServerFrame.getInstance().appendText("用户" + userId + "登录成功\n");
                                UserMessgae userMessgae = new UserMessgae();
                                userMessgae.setUserId(userId);
                                sendMessage(MessageCodeConstants.SUCCESS, userMessgae);
                                initGame();
                            } else {
                                ServerFrame.getInstance().appendText("用户登录失败\n");
                                sendMessage(MessageCodeConstants.FAIL, "");
                            }
                            break;
                        case MessageCodeConstants.REGISTER:
                            if (this.user != null) {
                                ServerFrame.getInstance().appendText("用户" + this.user.getUsername() + "已登录\n");
                                sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            RegisterMessage registerMessage = objectMapper.readValue(message.getContent(), RegisterMessage.class);
                            Integer registerUserId = UserService.getInstance().register(registerMessage.getUsername(), registerMessage.getPassword(), registerMessage.getSkin());
                            this.user = UserService.getInstance().login(registerMessage.getUsername(), registerMessage.getPassword());
                            if (registerUserId != null && registerUserId != -1) {
                                Integer userId = registerUserId;
                                ServerFrame.getInstance().appendText("用户" + registerUserId + "注册成功\n");
                                UserMessgae userMessgae = new UserMessgae();
                                userMessgae.setUserId(registerUserId);
                                sendMessage(MessageCodeConstants.SUCCESS, userMessgae);
                                initGame();
                            } else {
                                ServerFrame.getInstance().appendText("用户" + registerUserId + "注册失败\n");
                                // sendMessage(MessageCodeConstants.FAIL, "");
                            }
                            break;
                        case MessageCodeConstants.CONTROL:
                            if (this.user == null) {
                                ServerFrame.getInstance().appendText("用户未登录\n");
                                // sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            controller.setControlMessage(objectMapper.readValue(message.getContent(), ControlMessage.class));
                            break;
                        case MessageCodeConstants.CREATE_ROOM:
                            if (this.user == null) {
                                ServerFrame.getInstance().appendText("用户未登录\n");
                                // sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            if (roomId != 0) {
                                ServerFrame.getInstance().appendText("用户" + user.getUsername() + "已在房间" + roomId + "中\n");
                                // sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            String roomName = objectMapper.readValue(message.getContent(), String.class);
                            roomId = Server.getInstance().getRoomManager().createRoom(user, roomName);
                        case MessageCodeConstants.JOIN_ROOM:
                            if (this.user == null) {
                                ServerFrame.getInstance().appendText("用户未登录\n");
                                // sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            if (roomId != 0) {
                                ServerFrame.getInstance().appendText("用户" + user.getUsername() + "已在房间" + roomId + "中\n");
                                // sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            roomId = objectMapper.readValue(message.getContent(), Integer.class);
                            if (Server.getInstance().getRoomManager().joinRoom(roomId, user) == false) {
                                ServerFrame.getInstance().appendText("用户" + user.getUsername() + "加入房间" + roomId + "失败\n");
                                sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            break;
                        case MessageCodeConstants.START_GAME:
                            if (this.user == null) {
                                ServerFrame.getInstance().appendText("用户未登录\n");
                                // sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            if (roomId == 0) {
                                ServerFrame.getInstance().appendText("用户" + user.getUsername() + "未在房间中\n");
                                // sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            if (Server.getInstance().getRoomManager().startGame(roomId, user) == false) {
                                ServerFrame.getInstance().appendText("用户" + user.getUsername() + "开始游戏失败\n");
                                sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            break;
                        case MessageCodeConstants.EXIT_ROOM:
                            if (this.user == null) {
                                ServerFrame.getInstance().appendText("用户未登录\n");
                                // sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            if (roomId == 0) {
                                ServerFrame.getInstance().appendText("用户" + user.getUsername() + "未在房间中\n");
                                // sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            if (Server.getInstance().getRoomManager().leaveRoom(roomId, user) == false) {
                                ServerFrame.getInstance().appendText("用户" + user.getUsername() + "退出房间失败\n");
                                sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            sendMessage(MessageCodeConstants.SUCCESS, "");
                            break;
                        case MessageCodeConstants.ASK_LOBBY_INFO:
                            if (this.user == null) {
                                ServerFrame.getInstance().appendText("用户未登录\n");
                                sendMessage(MessageCodeConstants.FAIL, "");
                                break;
                            }
                            List<RoomInfoMessage> roomInfoMessages = new ArrayList<>();
                            for (RoomHandler roomHandler : Server.getInstance().getRoomManager().getRoomMap().values()) {
                                RoomInfoMessage roomInfoMessage = new RoomInfoMessage();
                                roomInfoMessage.setRoomId(roomHandler.getRoomId());
                                roomInfoMessage.setRoomName(roomHandler.getRoomName());
                                roomInfoMessage.setRoomPlayer(roomHandler.getPlayerNum());
                                roomInfoMessage.setMaxPlayer(roomHandler.getMAX_PLAYER_NUM());
                                roomInfoMessages.add(roomInfoMessage);
                            }
                            sendMessage(MessageCodeConstants.ROOM_LIST_INFO, roomInfoMessages);
                            break;
                        default:
                            
                            break;
                    }
                }
            }
        } catch (Exception e) {
            close();
        }
    }

    public void initGame() {
        Server.getInstance().getClientMapByUserId().put(user.getUserId(), this);
    }

    public void sendMessage(int code, Object content) {
        try {
            synchronized(writer) {
                if (!socket.isConnected()) {
                    return;
                }
                Message message = new Message();
                if (code == MessageCodeConstants.ENTITY_MANAGER_INFO && user == null) {
                    return;
                }
                message.setCode(code);
                message.setContent(objectMapper.writeValueAsString(content));
                writer.println(objectMapper.writeValueAsString(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void close() {
        try {
            ServerFrame.getInstance().appendText("用户" + user.getUsername() + "退出游戏\n");
            Server.getInstance().getClientMapByUserId().remove(user.getUserId());
            if (roomId != 0) {
                Server.getInstance().getRoomManager().leaveRoom(roomId, user);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
