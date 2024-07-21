package com.hfdlys.harmony.magicoflove.network.protoc;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Message {
    /**
     * 信息类型
     */
    private int code;

    /*
     * 信息内容
     */
    private String content;
}
