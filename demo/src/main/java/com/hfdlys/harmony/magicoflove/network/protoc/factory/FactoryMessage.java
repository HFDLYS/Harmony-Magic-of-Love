package com.hfdlys.harmony.magicoflove.network.protoc.factory;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import lombok.Data;

@Data
@ProtobufClass
public class FactoryMessage {
    /**
     * ID
     */
    private int id;

    /**
     * 类型
     */
    private int type;

}
