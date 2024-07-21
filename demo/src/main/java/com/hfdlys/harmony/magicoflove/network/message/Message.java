package com.hfdlys.harmony.magicoflove.network.message;



import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Message {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * 信息类型
     */
    private int code;

    /*
     * 信息内容
     */
    private String content;

    public Message() {
    }

    public Message(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public Message(int code, Object content) throws Exception {
        this.code = code;
        this.content = objectMapper.writeValueAsString(content);
    }
}
