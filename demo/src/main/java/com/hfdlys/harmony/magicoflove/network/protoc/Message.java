package com.hfdlys.harmony.magicoflove.network.protoc;

import com.baidu.bjf.remoting.protobuf.Any;
import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ProtobufClass
public class Message {
    /**
     * 信息类型
     */
    @Protobuf(fieldType = FieldType.INT32, order = 1, required = true)
    private Integer code;

    /*
     * 信息内容
     */
    @Protobuf(fieldType = FieldType.OBJECT, order = 2, required = true)
    private Any content;

}
