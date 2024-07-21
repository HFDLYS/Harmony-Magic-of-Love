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

public class Testo {
    public static void main(String[] args) {
        Codec<Message> codecMessage = ProtobufProxy.create(Message.class);
        Message message = new Message();
        byte array[] = new byte[1000];
        
        try {
            CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(new FileOutputStream("test"));
            ControlMessage controlMessage = new ControlMessage();
            controlMessage.setAimX(233);
            message.setContent(Any.pack(controlMessage));
            message.setCode(1);
            System.out.println("message code: " + message.getCode());
            codecMessage.writeTo(message, codedOutputStream);
            codedOutputStream.flush();
            codecMessage.writeTo(message, codedOutputStream);
            codedOutputStream.flush();
            Message newMessage = codecMessage.readFrom(CodedInputStream.newInstance(new FileInputStream("test")));
            System.out.println(newMessage.getCode());
            newMessage = codecMessage.readFrom(CodedInputStream.newInstance(new FileInputStream("test")));
            System.out.println(newMessage.getCode());
            newMessage = codecMessage.readFrom(CodedInputStream.newInstance(new FileInputStream("test")));
            System.out.println(newMessage.getCode());
            // Message newMessage = codecMessage.decode(bytes);
            System.out.println(((newMessage.getContent().unpack(ControlMessage.class))).getAimX());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
