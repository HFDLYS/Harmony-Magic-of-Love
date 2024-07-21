package com.hfdlys.harmony.magicoflove;

import java.net.*;
import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfdlys.harmony.magicoflove.network.protoc.Message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {
    /**
     * 单例模式
     */
    private static Client instance;

    private final String host = "localhost";

    private final int port = 2336;

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

    Socket socket;



    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public void run() {
        try {
            Message test = new Message();
            test.setCode(1);
            sendMessage(test);
            sendMessage(test);
            sendMessage(test);
            while (true) {
                synchronized(reader) {
                    log.info("waiting for message");
                    String line = reader.readLine();
                    Message message = objectMapper.readValue(line, Message.class);
                    log.info("Received message: " + message.getCode());
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
