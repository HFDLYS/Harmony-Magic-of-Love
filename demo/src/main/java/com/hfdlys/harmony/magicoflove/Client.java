package com.hfdlys.harmony.magicoflove;

import java.net.*;
import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.network.message.Message;
import com.hfdlys.harmony.magicoflove.network.message.PingMessage;
import com.hfdlys.harmony.magicoflove.network.message.UserMessgae;
import com.hfdlys.harmony.magicoflove.view.ClientFrame;
import com.hfdlys.harmony.magicoflove.view.ServerFrame;

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




    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public void run() {
        ClientFrame.getInstance().launchFrame();
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            synchronized(writer) {
                log.info("Sending message: " + message.getCode());
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
