package com.hfdlys.harmony.magicoflove.network.handler;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.hfdlys.harmony.magicoflove.Server;
import com.hfdlys.harmony.magicoflove.network.protoc.Message;
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
    
    private CodedInputStream codedInputStream;
    private CodedOutputStream codedOutputStream;

    Codec<Message> messagCodec;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        codedInputStream = CodedInputStream.newInstance(socket.getInputStream());
        codedOutputStream = CodedOutputStream.newInstance(socket.getOutputStream());
        messagCodec = ProtobufProxy.create(Message.class);
    }

    private Integer userId;

    @Override
    public void run() {
        try {
            log.info("Client connected: " + socket.getInetAddress());
            ServerFrame.getInstance().appendText("接收连接：" + socket.getInetAddress() + "\n");
            while (true) {
                synchronized(codedInputStream) {
                    Message message = messagCodec.readFrom(codedInputStream);
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
                log.info("Sending message: " + message.getCode());
                messagCodec.writeTo(message, codedOutputStream);
                codedOutputStream.flush();
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
