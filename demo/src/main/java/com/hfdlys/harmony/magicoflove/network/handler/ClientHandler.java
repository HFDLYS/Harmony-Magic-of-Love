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
                                ServerFrame.getInstance().appendText("用户" + userId + "登录成功\n");
                                UserMessgae userMessgae = new UserMessgae();
                                userMessgae.setUserId(userId);
                                GameManager.getInstance().addPlayerSkin(userId, user.getSkin());
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
                                GameManager.getInstance().addPlayerSkin(userId, registerMessage.getSkin());
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
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            ServerFrame.getInstance().appendText("用户" + user.getUsername() + "退出游戏\n");
            Server.getInstance().getClientMapByUserId().remove(user.getUserId());
        }
    }

    public void initGame() {
        Server.getInstance().getClientMapByUserId().put(user.getUserId(), this);
        ServerFrame.getInstance().appendText("用户" + user.getUsername() + "初始化游戏\n");
        List<UserMessgae> userMessgaes = new ArrayList<>();
        for (ClientHandler clientHandler : Server.getInstance().getClientMap().values()) {
            if (clientHandler.getUser() != null) {
                UserMessgae userMessgae = new UserMessgae();
                userMessgae.setUserId(clientHandler.getUser().getUserId());
                userMessgae.setUsername(clientHandler.getUser().getUsername());
                userMessgae.setSkin(clientHandler.getUser().getSkin());
                userMessgaes.add(userMessgae);
            }
        }
        try {
            sendMessage(MessageCodeConstants.USER_INFO, userMessgaes);
        } catch (Exception e) {
            e.printStackTrace();
        }


        UserMessgae myUserMessgae = new UserMessgae(user);
        List<UserMessgae> myUserMessgaes = new ArrayList<>();
        myUserMessgaes.add(myUserMessgae);
        try {
            Server.getInstance().broadcast(MessageCodeConstants.USER_INFO, myUserMessgaes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Random random = new Random();
        int min = 1;
        int max = 3;
        int randomInt = random.nextInt(max - min) + min;
        EntityManager.getInstance().add(CharacterFactory.getCharacter(user.getUserId(), randomInt, controller), new CharacterRegisterMessage(0, user.getUserId(), user.getUsername(), randomInt));
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


    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
