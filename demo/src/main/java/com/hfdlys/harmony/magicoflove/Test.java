package com.hfdlys.harmony.magicoflove;


import com.baidu.bjf.remoting.protobuf.Any;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.hfdlys.harmony.magicoflove.network.protoc.ControlMessage;
import com.hfdlys.harmony.magicoflove.network.protoc.Message;

public class Test {
    public static void main(String[] args) {
        Codec<Message> codecMessage = ProtobufProxy.create(Message.class);
        Codec<ControlMessage> codecC = ProtobufProxy.create(ControlMessage.class);
        Message message = new Message();
        
        try {
            ControlMessage controlMessage = new ControlMessage();
            controlMessage.setAimX(233);
            message.setContent(Any.pack(controlMessage));
            message.setCode(1);
            byte[] bytes = codecMessage.encode(message);
            Message newMessage = codecMessage.decode(bytes);
            System.out.println(((newMessage.getContent().unpack(ControlMessage.class))).getAimX());

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
