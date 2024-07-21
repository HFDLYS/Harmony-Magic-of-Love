package com.hfdlys.harmony.magicoflove;

import java.net.*;
import java.io.*;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
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

    private Client() {
        try {
            socket = new Socket(host, port);
            codedInputStream = CodedInputStream.newInstance(socket.getInputStream());
            codedOutputStream = CodedOutputStream.newInstance(socket.getOutputStream());
            messageCodec = ProtobufProxy.create(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Socket socket;

    private CodedInputStream codedInputStream;
    private CodedOutputStream codedOutputStream;

    Codec<Message> messageCodec;

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
                synchronized(codedInputStream) {
                    log.info("waiting for message");
                    Message message = messageCodec.readFrom(codedInputStream);
                    log.info("Received message: " + message.getCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            synchronized(codedOutputStream) {
                messageCodec.writeTo(message, codedOutputStream);
                codedOutputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Client.getInstance().run();
    }
}
