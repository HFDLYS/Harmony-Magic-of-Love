package com.hfdlys.harmony.magicoflove;


import com.baidu.bjf.remoting.protobuf.Any;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.hfdlys.harmony.magicoflove.network.protoc.ControlMessage;
import com.hfdlys.harmony.magicoflove.network.protoc.Message;

import java.io.*;
import java.net.*;

public class Test {
    public static void main(String[] args) {
        Codec<Message> codecMessage = ProtobufProxy.create(Message.class);
        Message message = new Message();
        byte array[] = new byte[1000];
        
        try {
            ServerSocket serverSocket = new ServerSocket(2344);
            Socket socket2 = serverSocket.accept();
            InputStream inputStream = socket2.getInputStream();
            System.out.println("waiting for message");
            CodedInputStream codedInputStream = CodedInputStream.newInstance(inputStream);
            Message newMessage = codecMessage.readFrom(codedInputStream);
            System.out.println(newMessage.getCode());
            System.out.println(((newMessage.getContent().unpack(ControlMessage.class))).getAimX());
            newMessage.setCode(2);
            OutputStream outputStream = socket2.getOutputStream();
            CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(outputStream);
            codecMessage.writeTo(newMessage, codedOutputStream);
            codedOutputStream.flush();
            // Message newMessage = codecMessage.decode(bytes);
            // System.out.println(((newMessage.getContent().unpack(ControlMessage.class))).getAimX());
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
