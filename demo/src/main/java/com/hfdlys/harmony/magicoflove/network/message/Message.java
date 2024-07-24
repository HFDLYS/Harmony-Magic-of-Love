package com.hfdlys.harmony.magicoflove.network.message;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfdlys.harmony.magicoflove.util.RSAUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 信息
 * @since 2024-07-18
 */
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

    public Message(int code, Object content) throws Exception {
        this.code = code;
        this.content = objectMapper.writeValueAsString(content);
    }

    public String getContent() {
        return RSAUtil.getInstance().decrypt(content);
    }

    public void setContent(String content) {
        this.content = RSAUtil.getInstance().encrypt(content);
    }
}
