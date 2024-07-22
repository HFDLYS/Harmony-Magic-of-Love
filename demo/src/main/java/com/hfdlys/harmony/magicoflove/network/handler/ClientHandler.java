package com.hfdlys.harmony.magicoflove.network.handler;

import com.hfdlys.harmony.magicoflove.Server;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.database.service.UserService;
import com.hfdlys.harmony.magicoflove.network.message.Messages.Message;
import com.hfdlys.harmony.magicoflove.network.message.Messages.LoginMessage;
import com.hfdlys.harmony.magicoflove.network.message.Messages.RegisterMessage;
import com.hfdlys.harmony.magicoflove.network.message.Messages.UserMessage;
import com.hfdlys.harmony.magicoflove.view.ServerFrame;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class ClientHandler extends Thread {
    private WebSocket connection;
    private Integer userId;
    private String username;

    public ClientHandler(WebSocket connection) {
        this.connection = connection;
    }

    public void handleMessage(byte[] messageBytes) {
        try {
            Message message = Message.parseFrom(messageBytes);
            log.info("Received message: " + message.getCode());
            switch (message.getMessageCase()) {
                case PINGMESSAGE:
                    // Handle heartbeat
                    break;
                case LOGINMESSAGE:
                    if (this.userId != null) {
                        ServerFrame.getInstance().appendText("用户" + this.userId + "已登录\n");
                        sendMessage(Message.newBuilder()
                                .setCode(MessageCodeConstants.FAIL)
                                .build());
                        break;
                    }
                    LoginMessage loginMessage = message.getLoginMessage();
                    Integer userId = UserService.getInstance().login(loginMessage.getUsername(), loginMessage.getPassword());
                    if (userId != null && userId != -1) {
                        this.userId = userId;
                        ServerFrame.getInstance().appendText("用户" + userId + "登录成功\n");
                        UserMessage userMessage = UserMessage.newBuilder()
                                .setUserId(userId)
                                .setUserName(loginMessage.getUsername())
                                .build();
                        username = loginMessage.getUsername();
                        sendMessage(Message.newBuilder()
                                .setCode(MessageCodeConstants.SUCCESS)
                                .setUserMessage(userMessage)
                                .build());
                    } else {
                        ServerFrame.getInstance().appendText("用户" + userId + "登录失败\n");
                        sendMessage(Message.newBuilder()
                                .setCode(MessageCodeConstants.FAIL)
                                .build());
                    }
                    break;
                case REGISTERMESSAGE:
                    if (this.userId != null) {
                        ServerFrame.getInstance().appendText("用户" + this.userId + "已登录\n");
                        sendMessage(Message.newBuilder()
                                .setCode(MessageCodeConstants.FAIL)
                                .build());
                        break;
                    }
                    RegisterMessage registerMessage = message.getRegisterMessage();
                    Integer registerUserId = UserService.getInstance().register(registerMessage.getUsername(), registerMessage.getPassword(), registerMessage.getSkin().toByteArray());
                    if (registerUserId != null && registerUserId != -1) {
                        this.userId = registerUserId;
                        ServerFrame.getInstance().appendText("用户" + registerUserId + "注册成功\n");
                        UserMessage userMessage = UserMessage.newBuilder()
                                .setUserId(registerUserId)
                                .setUserName(registerMessage.getUsername())
                                .build();
                        username = registerMessage.getUsername();
                        sendMessage(Message.newBuilder()
                                .setCode(MessageCodeConstants.SUCCESS)
                                .setUserMessage(userMessage)
                                .build());
                    } else {
                        ServerFrame.getInstance().appendText("用户" + registerUserId + "注册失败\n");
                        sendMessage(Message.newBuilder()
                                .setCode(MessageCodeConstants.FAIL)
                                .build());
                    }
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
            connection.send(message.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}