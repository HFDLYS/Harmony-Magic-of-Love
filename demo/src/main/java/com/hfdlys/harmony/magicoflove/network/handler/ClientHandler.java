package com.hfdlys.harmony.magicoflove.network.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfdlys.harmony.magicoflove.Server;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.database.service.UserService;
import com.hfdlys.harmony.magicoflove.network.message.LoginMessage;
import com.hfdlys.harmony.magicoflove.network.message.Message;
import com.hfdlys.harmony.magicoflove.network.message.RegisterMessage;
import com.hfdlys.harmony.magicoflove.network.message.UserMessgae;
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

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        objectMapper = new ObjectMapper();
    }

    private Integer userId;

    private String username;

    @Override
    public void run() {
        try {
            log.info("Client connected: " + socket.getInetAddress());
            ServerFrame.getInstance().appendText("接收连接：" + socket.getInetAddress() + "\n");
            while (true) {
                synchronized(reader) {
                    Message message = objectMapper.readValue(reader.readLine(), Message.class);
                    log.info("Received message: " + message.getCode());
                    switch (message.getCode()) {
                        case MessageCodeConstants.HEART_BEAT:
                            
                            break;
                        case MessageCodeConstants.LOGIN:
                            if (this.userId != null) {
                                ServerFrame.getInstance().appendText("用户" + this.userId + "已登录\n");
                                sendMessage(new Message(MessageCodeConstants.FAIL, null));
                                break;
                            }
                            LoginMessage loginMessage = objectMapper.readValue(message.getContent(), LoginMessage.class);
                            Integer userId = UserService.getInstance().login(loginMessage.getUsername(), loginMessage.getPassword());
                            if (userId != null && userId != -1) {
                                this.userId = userId;
                                ServerFrame.getInstance().appendText("用户" + userId + "登录成功\n");
                                UserMessgae userMessgae = new UserMessgae();
                                userMessgae.setUserId(userId);
                                username = loginMessage.getUsername();
                                sendMessage(new Message(MessageCodeConstants.SUCCESS, objectMapper.writeValueAsString(userMessgae)));
                            } else {
                                ServerFrame.getInstance().appendText("用户" + userId + "登录失败\n");
                                sendMessage(new Message(MessageCodeConstants.FAIL, null));
                            }
                            break;
                        case MessageCodeConstants.REGISTER:
                            if (this.userId != null) {
                                ServerFrame.getInstance().appendText("用户" + this.userId + "已登录\n");
                                sendMessage(new Message(MessageCodeConstants.FAIL, null));
                                break;
                            }
                            RegisterMessage registerMessage = objectMapper.readValue(message.getContent(), RegisterMessage.class);
                            Integer registerUserId = UserService.getInstance().register(registerMessage.getUsername(), registerMessage.getPassword(), registerMessage.getSkin());
                            if (registerUserId != null && registerUserId != -1) {
                                this.userId = registerUserId;
                                ServerFrame.getInstance().appendText("用户" + registerUserId + "注册成功\n");
                                UserMessgae userMessgae = new UserMessgae();
                                userMessgae.setUserId(registerUserId);
                                username = registerMessage.getUsername();
                                sendMessage(new Message(MessageCodeConstants.SUCCESS, objectMapper.writeValueAsString(userMessgae)));
                            } else {
                                ServerFrame.getInstance().appendText("用户" + registerUserId + "注册失败\n");
                                sendMessage(new Message(MessageCodeConstants.FAIL, null));
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            
        }
    }

    public void sendMessage(Message message) {
        try {
            synchronized(writer) {
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
